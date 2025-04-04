<script setup lang="ts">
import { VDataTable } from "vuetify/lib/labs/components.mjs";
import { useRouter } from "vue-router";
import * as XLSX from "xlsx";

import type { RouteLocationRaw } from "#vue-router";
import { accountStore } from "@/store/account/account";
import AddSlipModal from "@/components/account/account/Modal/AddSlipModal.vue";
import AddJournalModal from "@/components/account/account/Modal/AddJournalModal.vue";
import AddJournalDetailModal from "@/components/account/account/Modal/AddJournalDetailModal.vue";
import AddDeptCodeModal from "@/components/account/account/Modal/AddDeptCodeModal.vue";
import EditAddSlipModal from "@/components/account/account/Modal/EditAddSlipModal.vue";

// 테이블 데이터
const newSlipForm: Ref<any[]> = ref([]);
//const newJournalForm: Ref<any[]> = ref([]); // ref([])로 선언한 리스트는 Array<never> 이라 push사용 불가
const newJournalModalForm: Ref<any[]> = ref([]);
const newJournalList = [];
const newJournalDetailList = [];
const Store = accountStore();
const isCustomerCodeDialogVisible = ref<Boolean>(false);
//2025.03.18 오상섭
const journalMap = ref(new Map()); // slipNo를 key로 하는 Map
const newJournalForm = computed(() => {
  if (!selectedSlips.value.length) return []; // 선택된 전표가 없으면 빈 배열 반환

  // 현재 선택된 전표의 slipNo 목록 가져오기
  const selectedSlipNos = selectedSlips.value.map((slip) => slip);

  // journalMap에서 선택된 전표들에 해당하는 모든 분개를 모아 반환
  return selectedSlipNos.flatMap(
    (slipNo) => journalMap.value.get(slipNo) || []
  );
});
const selectedSlipData = computed(() => {
  return newSlipForm.value.filter((slip) =>
    selectedSlips.value.includes(slip.slipNo)
  );
});
const selectedSlips = ref([]); // 선택된 전표 리스트
const isJournalAddWarningVisible = ref(false); // 경고창 상태

const VDataTabledeleteSlipRow = () => {
  if (!selectedSlips.value.length) return;

  selectedSlips.value.forEach((slipNo) => {
    journalMap.value.delete(slipNo);
  });

  newSlipForm.value = newSlipForm.value.filter(
    (slip) => !selectedSlips.value.includes(slip.slipNo)
  );

  journalMap.value = new Map(journalMap.value);

  selectedSlips.value = [];
};

watch(selectedSlips, (newValue) => {
  openJournalButton.value = newValue.length === 1; // 하나만 선택했을 때 활성화
});
//2025.03.18 오상섭
const { controlCodeList } = storeToRefs(Store);

const journalDetailList: Ref<any[]> = ref([]);
const detailJournal = ref("");

// 카드 데이터
const newNoteDetailForm: Ref<any[]> = ref([]);

// 대차 합계 확인
const totalPrice = ref(0);
const clearPriceState = ref(true);

const isMatchingDebitCredit = computed(() => {
  if (!selectedSlips.value.length) return false; // 전표가 선택되지 않았을 경우 비활성화

  let totalDebit = 0;
  let totalCredit = 0;

  for (const slipNo of selectedSlips.value) {
    const journalList = journalMap.value.get(slipNo) || [];

    if (journalList.length === 0) {
      return false; //선택한 전표 중 하나라도 분개가 없으면 버튼 비활성화
    }

    journalList.forEach((journal) => {
      totalDebit += Number(journal.leftDebitPrice) || 0;
      totalCredit += Number(journal.rightCreditPrice) || 0;
    });
  }

  return totalDebit === totalCredit; // 차변과 대변이 일치하면 true (버튼 활성화)
});

// Dialog창 visible조절
const isSlipDialogVisible = ref(false);
const isEditSlip = ref(false);
const isJournalDialogVisible = ref(false);
const isConfirmDialogVisible = ref(false);
const isFileUploadFailedSnackBarVisible = ref(false);
const isNoteDetailCardVisible = ref(false);
const isCodeDetailCardVisible = ref(false);
const isNoteDetailSuccessAlertVisible = ref(false);
const isRequestAllAlertVisible = ref(false);
const isRequestNoteDetailAlertVisible = ref(false);
const isAddSlipSuccessAlertVisible = ref(false);

// 버튼 visible조절
const openSlipButton = ref(false);
const openJournalButton = ref(false);

// 분개 상세페이지에 분개 일렬번호 전달
const rowJournalNo = ref("");
const count = ref(0);

// 어음상세 추가시 flag
const noteFlag = ref(true);

const saveMap: Map<string, object> = new Map();

const selectedJournalNo = ref("");

const TemporarySave = (e: any) => {
  saveMap.set(
    selectedJournalNo.value,
    JSON.parse(JSON.stringify(controlCodeList.value))
  );

  journalDetailList.value = [];
};

// 2024 03 12 기존 로직 너무 보기 불편해서 새로  만듬 isjournalDetailVisible 보면은 내마음이해됩니다
//아직 파일 추가는 구현안함 그건 나중에구현현

const slipSave = async () => {
  if (!selectedSlips.value.length) {
    console.warn("⚠️ 선택된 전표가 없습니다.");
    return;
  }

  // 선택된 전표들만 필터링하여 slip 배열 생성
  const slipList = selectedSlips.value
    .map((slipNo) => {
      const slipData = newSlipForm.value.find((slip) => slip.slipNo === slipNo);
      if (!slipData) return null;

      return {
        ...slipData, // 기존 전표 정보 유지
        journalList: (journalMap.value.get(slipNo) || []).map(
          (JournalForm) => ({
            ...JournalForm,
            journalDetailList: saveMap.get(JournalForm.journalNo) || [],
          })
        ),
      };
    })
    .filter(Boolean); // `null` 값 제거

  if (slipList.length === 0) {
    console.warn("⚠️ 선택한 전표에 해당하는 데이터가 없습니다.");
    return;
  }

  console.log("📌 저장할 전표 리스트:", slipList);

  // ✅ 선택된 전표들을 하나씩 순차적으로 저장
  for (const slip of slipList) {
    try {
      const response = await accountStore().CREATE_SLIP(slip);
      console.log(`✅ 전표 (${slip.slipNo}) 저장 완료:`, response);
    } catch (error) {
      console.error(`❌ 전표 (${slip.slipNo}) 저장 실패:`, error);
    }
  }

  isAddSlipSuccessAlertVisible.value = true;
  isConfirmDialogVisible.value = false;
  addSlipSuccess();
};

// 파일 관련
const refInputEl = ref<HTMLElement>();
const jsonKey = ref(0);
const fileKey = ref(0);
const file = ref<File | null>(null);
const slipJsonData = ref({});

const selectedFileName = ref("");

// 페이지 이동시 사용될 router
const router = useRouter();

// 분개테이블 선택된 아이템
const selectedItems: Ref<any[]> = ref([]);

// 분개 상세 메뉴
const isJournalDetailDialogVisible = ref(false);
const isDeptDialogVisible = ref(false);
const isDeptVisible = ref(false);
const isTextVisible = ref(false);
const isCalendarVisible = ref(false);
const isSelectVisible = ref(false);
const isNotCodeDetailCardVisible = ref(false);

// 분개 상세 메뉴 라벨
const textName = ref("");
const calendarName = ref("");
const selectName = ref("");
const searchName = ref("");

// 분개 상세 셀렉트 메뉴
const selectList = ref<string[]>();

// 분개 상세 유형별 타입
const newDescriptionList = [];
const newDescriptionAccount = {
  journalNo: "",
  accountName: "",
  option: "",
};
const newDescription = ref({
  text: "",
  calendar: null,
  select: "",
  search: "",
});

// 전표테이블 헤더
const slipField: any = [
  { title: "전표유형", key: "slipType" },
  { title: "기수일련번호", key: "accountPeriodNo" },
  { title: "임시전표일련번호", key: "slipNo" },
  { title: "부서명", key: "deptName" },
  { title: "사용일자", key: "postingDate" },
  { title: "작성자코드", key: "reportingEmpCode" },
  { title: "작성자명", key: "reportingEmpName" },
  { title: "작성일자", key: "reportingDate" },
  { title: "품의내역", key: "expenseReport" },
];

// 분개테이블 헤더
const journalField: any = [
  { title: "분개일련번호", key: "journalNo" },
  { title: "계정코드", key: "accountCode" },
  { title: "계정과목", key: "accountName" },
  { title: "대차구분", key: "balanceDivision" },
  { title: "거래처코드", key: "customerCode" },
  { title: "거래처명", key: "customerName" },
  { title: "차변금액", key: "leftDebitPrice" },
  { title: "대변금액", key: "rightCreditPrice" },
];

// 분개상세테이블 헤더  -- 2024/06/10일 이후로 안쓰게 됨, 79기 플젝 이전의 분개 상세 구조에 쓰임
const journalDetailField: any = [
  { title: "분개번호", key: "accountControlCode" },
  { title: "분개상세항목", key: "accountControlName" },
  { title: "코드", key: "accountControlType" },
  { title: "상세내용", key: "description" },
  // { title: "", key: "showDetails" },
];

// 어음유형
const options = ref<string[]>(["1.당좌", "2.가계", "3.어음", "4.전자"]);

// 수금사원 리스트
const draweeList = ref<string[]>();

//계정코드 클릭한값

const selectedCustomerCode = ref();

// 어음상세 데이터칼럼
const noteNo = ref("");
const noteType = ref("");
const accountInnerCode = ref("");
const drawer = ref("");
const endorser = ref("");
const drawee = ref("");
const issuanceDate = ref("");
const maturityDate = ref("");

// 어음 임력 rule
const noteRules = [
  (value: string | null) => {
    if (value?.length && value.length > 0) {
      if (value?.length && value.length === 20) return true;
      else return "올바른 어음번호가 아닙니다.";
    }

    return "필수 입력사항입니다.";
  },
];

const customerCodeSelected = ({ selected, row }: any) => {
  selectedCustomerCode.value = row.internalItem.columns.customerCode;
  console.log("선택한거래처 코드", selectedCustomerCode.value);
};

// 사원정보 조회
const fetchData = async () => {
  await accountStore().FETCH_ALL_EMPLOYEES();
  draweeList.value = Object.values(accountStore().empList).map(
    (item: any) => item.empName
  );
};

onMounted(() => {
  fetchData();
  fetchJournalDetailSelect();
});

// 분개 계정과목 체크

const isdetail = async (selected: any, row: any) => {
  if (!saveMap.get(row.item.journalNo)) {
    await Store.SEARCH_CODE_DETAIL(row.item.accountCode);
    selectedJournalNo.value = row.item.journalNo;

    journalDetailList.value = controlCodeList.value;
  } else {
    //

    journalDetailList.value = saveMap.get(row.item.journalNo);
    console.log("이거실행되나?", saveMap.get(row.item.journalNo));
  }
};

//2025 03월 내가slipSave로 만듬
const isjournalDetailVisible = async (selected: any, row: any) => {
  isNoteDetailCardVisible.value = false;
  isCodeDetailCardVisible.value = false;
  isNotCodeDetailCardVisible.value = false;
  console.log("어떤게들어올까?", selected);
  console.log("row는멀까?", row);
  console.log("계정코드들어있나 ?", row.item.accountCode);

  // console.log(row.internalItem.columns);
  // await accountStore().SEARCH_CODE_DETAIL(row.item.accountCode);
  // resetVisible();
  // console.log(accountStore().controlCodeList[0]);

  // console.log("journalDetailList 이게멀까123",journalDetailList);

  //같은 분개행을 두번 클릭할땐 분개상세 카드가 꺼지게 한다.
  if (rowJournalNo.value === row.internalItem.columns.journalNo) {
    rowJournalNo.value = "";
    isNoteDetailCardVisible.value = false;
    isCodeDetailCardVisible.value = false;
    isNotCodeDetailCardVisible.value = false;
  }

  //정상적으로 한번 눌렀을때 실행
  else {
    rowJournalNo.value = row.internalItem.columns.journalNo;

    //0110과 0252는 분개 상세가 아니라 어음상세로 들어가야한다.
    if (
      row.internalItem.columns.accountCode === "0110" ||
      row.internalItem.columns.accountCode === "0252"
    ) {
      accountInnerCode.value = row.internalItem.columns.accountCode;

      const noteflag = ref(true);
      for (let i = 0; i < newNoteDetailForm.value.length; i++) {
        if (newNoteDetailForm.value[i].journalNo === rowJournalNo.value) {
          noteNo.value = newNoteDetailForm.value[i].noteNo;
          noteType.value = newNoteDetailForm.value[i].noteType;
          issuanceDate.value = newNoteDetailForm.value[i].issuanceDate;
          maturityDate.value = newNoteDetailForm.value[i].maturityDate;
          drawer.value = newNoteDetailForm.value[i].drawer;
          endorser.value = newNoteDetailForm.value[i].endorser;
          drawee.value = newNoteDetailForm.value[i].drawee;

          noteflag.value = false;
          break;
        }
      }
      if (noteflag.value) {
        noteNo.value = "";
        noteType.value = "";
        issuanceDate.value = "";
        maturityDate.value = "";
        drawer.value = "";
        endorser.value = "";
        drawee.value = "";
      }

      isNoteDetailCardVisible.value = true;
    }

    //어음 상세가 기입될 필요 없는 나머지 계정코드들에 대한 검사
    else {
      //이 check변수는 아래 2번 for문이 시행된 후 accountStore().controlCodeList.length의 길이와 숫자가 같아진다.
      //그럼 for문이 돌때까지 같은 코드를 찾지 못했다는 말이므로 분개 상세가 필요 없는 과목이란 뜻이 된다.
      //이 변수를 이용해서 분개상세가 필요 없는 계정과목은 다른 카드를 띄워준다.
      let checkCount = 0;

      //같은 분개번호를 가진 분개상세를 모두 지워준다.
      //해당 for문이 존재 함으로써 한 분개에 중복으로 분개상세가 추가되는것을 방지한다.
      //해당 for문이 없으면 같은행을 2번 3번클릭할때마다 중복 분개상세가 하나의 분개에 계속해서 추가된다.
      for (let l = newJournalDetailList.length - 1; l >= 0; l--) {
        if (
          newJournalDetailList[l].journalNo ===
          row.internalItem.columns.journalNo
        ) {
          newJournalDetailList.splice(l, 1);
        }
      }

      //checkout에서 언급한 2번 FOR문이 여기임
      for (let i = 0; i < accountStore().controlCodeList.length; i++) {
        //store에서 응답받은 계정관리코드 리스트의 계정Code에 선택한 행의 계정Code가 일치하면 실행
        //루프로 if문을 검사하기때문에 한 계정과목에 여러 특이사항이 오더라도 모두 반영가능
        if (
          accountStore().controlCodeList[i].accountCode ===
          row.internalItem.columns.accountCode
        ) {
          console.log("코드 일치!!");

          //계정명과 분개 번호를 화면에 출력해줌과 동시에 해당 분개에서 사용중인 과목을 변수에 저장한다.
          //이는 같은 행을 클릭했을때 중복으로 특이사항이 추가되는것을 방지할때 사용된다.
          newDescriptionAccount.accountName =
            row.internalItem.columns.accountName;
          newDescriptionAccount.journalNo = row.internalItem.columns.journalNo;
          //분개 상세에 필요한 내용을 JSON으로 만들어 둔다. --중요1
          //한 계정에 여러 분개상세가 있다면 여기서 여러 리스트가 생성되게 되고
          //이 리스트들을 모두 해당 분개하나에 담아서 backend로 보내야 요구사항대로 데이터에 저장하기 쉽다.
          const addJournalDetailList = {
            journalNo: row.internalItem.columns.journalNo,
            accountControlType:
              accountStore().controlCodeList[i].accountControlType,
            accountControlName:
              accountStore().controlCodeList[i].accountControlName,
            journalDetailNo: newJournalDetailList.length + 1,
            accountControlCode:
              accountStore().controlCodeList[i].accountControlCode,
            description: "",
            accountName: row.internalItem.columns.accountName,
          };
          //값을 입력받을 준비가 완료되면 해당 값을 화면에 출력하고, 새 분개리스트에 저장.
          //새 분개리스트는 최종적으로 backend로 보낼때 사용된다.
          //description만 유일하게 비어있는데 이 부분을 추후 사용자에게 받은 내용으로 채워 보내게 된다.

          isCodeDetailCardVisible.value = true;
          newJournalDetailList.push(addJournalDetailList);
          console.log("제대로 왓냐 1 ", newJournalDetailList);
          console.log("제대로 왓냐 2 ", addJournalDetailList);
          // 계정관리코드 타입이 SELECT인지 TEXT인지 CALENDAR인지 SEARCH인지 확인
          // 아래 템플릿의 각 컴포넌트에 특이사항 속성에 맞는 이름 배치 및 컴포넌트 출력
          if (
            accountStore().controlCodeList[i].accountControlType === "SELECT"
          ) {
            selectName.value =
              accountStore().controlCodeList[i].accountControlName;
            isSelectVisible.value = true;
            console.log("SELECT");
          } else if (
            accountStore().controlCodeList[i].accountControlType === "TEXT"
          ) {
            textName.value =
              accountStore().controlCodeList[i].accountControlName;
            isTextVisible.value = true;
            console.log("TEXT");
          } else if (
            accountStore().controlCodeList[i].accountControlType === "CALENDAR"
          ) {
            calendarName.value =
              accountStore().controlCodeList[i].accountControlName;
            isCalendarVisible.value = true;
            console.log("CALENDAR");
          } else if (
            accountStore().controlCodeList[i].accountControlType === "SEARCH"
          ) {
            searchName.value =
              accountStore().controlCodeList[i].accountControlName;
            isDeptVisible.value = true;
            console.log("SEARCH");
          }
        } else {
          checkCount++;
        }
      }

      console.log("체크카운트 얼만데 : ", checkCount);

      if (
        checkCount === accountStore().controlCodeList.length &&
        isCodeDetailCardVisible.value != true
      ) {
        isNotCodeDetailCardVisible.value = true;
        checkCount = 1;
      }
    }
  }
};

// 파일 Parsing
const parseExcel = (importfile: any) => {
  const reader = new FileReader();

  reader.readAsArrayBuffer(importfile);

  // reader가 readAsArrayBuffer로 파일을 다 읽으면 자동 트리거
  reader.onload = (e: any) => {
    try {
      const data = new Uint8Array(e.target.result);

      const workbook = XLSX.read(data, { type: "array" });

      // 엑셀 파일 시트를 할당
      const sheetName = workbook.SheetNames[0];
      const sheet = workbook.Sheets[sheetName];

      // JSON 형식으로 변환
      slipJsonData.value = XLSX.utils.sheet_to_json(sheet, { header: 1 });
    } catch (error: any) {
      console.error("Error parsing Excel file: ", error);
    }
  };
};

// 전표 파일 추가
const onChange = (event: any) => {
  const fileInput = event.target.files;

  if (fileInput.length > 0) {
    if (
      fileInput[0].name.includes(".xlsx") ||
      fileInput[0].name.includes(".xls")
    ) {
      file.value = fileInput[0];
      selectedFileName.value = fileInput[0].name;

      jsonKey.value++;

      newSlipForm.value = [];
      newJournalForm.value = [];
      newJournalModalForm.value = [];

      openSlipButton.value = true;
      clearPriceState.value = false;

      parseExcel(file.value);
    } else {
      selectedFileName.value = "";
      isFileUploadFailedSnackBarVisible.value = true;
    }
  } else {
    selectedFileName.value = "";
    openSlipButton.value = false;
    clearPriceState.value = true;
  }
};
//2025.03.17 오상섭 수정
// 전표 추가
const updateNewSlipForm = (updatedData: any) => {
  newSlipForm.value.push(...updatedData);
};
//전표 수정
const updateSlipForm = (updatedData: any) => {
  newSlipForm.value = newSlipForm.value.map((slip) =>
    slip.slipNo === updatedData.slipNo ? { ...slip, ...updatedData } : slip
  );
};
//2025.03.17 오상섭 수정

//분개 추가

const updateNewJournalForm = (updatedData: any) => {
  if (selectedSlips.value.length !== 1) {
    console.warn("⚠️ 반드시 하나의 전표를 선택해야 합니다.");
    return;
  }

  // 선택한 전표의 slipNo 가져오기
  const selectedSlip = selectedSlips.value[0];

  if (!selectedSlip || !selectedSlip) {
    return;
  }

  const selectedSlipNo = selectedSlip;
  console.log("선택된 전표 slipNo:", selectedSlipNo);

  // 기존 분개 리스트 가져오기
  const existingJournalList = journalMap.value.get(selectedSlipNo) || [];

  // 새로운 분개 추가
  const updatedJournalList = [...existingJournalList, ...updatedData];

  // Map에 업데이트된 분개 저장
  journalMap.value.set(selectedSlipNo, updatedJournalList);

  console.log(Array.from(journalMap.value.entries()));
};

// 분개상세 추가
const updatenewJournalModalForm = (updatedData: any) => {
  if (newJournalModalForm.value.length === 0)
    newJournalModalForm.value = updatedData;
  else newJournalModalForm.value.push(updatedData[0]);

  // id 증가
  count.value++;
};

//분개상세 클릭 이벤트
const detailRowClick = (selected: any, row: any) => {
  console.log("selected==", selected);
  console.log("row==", row);
  console.log("이렇게해서값이나올려나?", row.item.accountControlType);

  if (row.item.accountControlType == "select") {
  }
};

// 어음상세 추가 / 수정
const updateNewNoteDetailForm = () => {
  if (
    noteNo.value === "" ||
    noteType.value === "" ||
    accountInnerCode.value === "" ||
    issuanceDate.value === "" ||
    maturityDate.value === "" ||
    drawer.value === "" ||
    endorser.value === "" ||
    drawee.value === ""
  ) {
    isRequestAllAlertVisible.value = true;
  } else {
    const flag = ref(true);
    for (let i = 0; i < newNoteDetailForm.value.length; i++) {
      if (newNoteDetailForm.value[i].journalNo === rowJournalNo.value) {
        (newNoteDetailForm.value[i].noteNo = noteNo.value),
          (newNoteDetailForm.value[i].noteType = noteType.value),
          (newNoteDetailForm.value[i].accountInnerCode =
            accountInnerCode.value),
          (newNoteDetailForm.value[i].issuanceDate = issuanceDate.value),
          (newNoteDetailForm.value[i].maturityDate = maturityDate.value),
          (newNoteDetailForm.value[i].drawer = drawer.value),
          (newNoteDetailForm.value[i].endorser = endorser.value),
          (newNoteDetailForm.value[i].drawee = drawee.value),
          (flag.value = false);
        break;
      }
    }

    if (flag.value) {
      newNoteDetailForm.value.push({
        journalNo: rowJournalNo.value,
        noteNo: noteNo.value,
        noteType: noteType.value,
        accountInnerCode: accountInnerCode.value,
        issuanceDate: issuanceDate.value,
        maturityDate: maturityDate.value,
        drawer: drawer.value,
        endorser: endorser.value,
        drawee: drawee.value,
      });
    }

    isNoteDetailSuccessAlertVisible.value = true;

    noteFlag.value = true;
  }
};

// DB 저장
const addSlipToDB = async () => {
  if (!noteFlag.value) {
    isRequestNoteDetailAlertVisible.value = true;
    isConfirmDialogVisible.value = false;
  } else {
    for (let i = 0; i < newJournalForm.value.length; i++) {
      const flag = ref(false);
      const preJournalDetailList = [];
      const journalDetailList = [];
      //이전 분개 상세 리스트 추가 로직
      for (let j = 0; j < newJournalModalForm.value.length; j++) {
        if (
          newJournalForm.value[i].journalNo ===
          newJournalModalForm.value[j].journalNo
        ) {
          preJournalDetailList.push({
            journalNo: newJournalModalForm.value[j].journalNo,
            accountControlName: newJournalModalForm.value[j].accountControlName,
            accountControlType: newJournalModalForm.value[j].accountControlType,
            journalDescription: newJournalModalForm.value[j].journalDescription,
          });
          flag.value = true;
        }
      }

      // 새로 추가된 - 분개 상세 리스트에 Description 추가 로직 -
      // ** 아래 분개상세 저장 로직이 있습니다. 먼저 해석하고 이 부분을 이해하시면 됩니다. ( 2024.05.24 분개 상세 저장 참조 ) **
      // 2중 for문으로 newJournalDetailList에 이미 담겨있는 분개일련번호와 newDescriptionList의 분개번호를 비교하여
      // 같은 분개번호가 있으면 해당 분개번호의 newDescriptionList 에서 특이사항을 가져와서 newJournalDetailList의 accountControlType과 비교,
      // 같은 값이 있으면 newJournalDetailList의 description에 newDescriptionList의 option의 값을 넣어준다.
      // 이렇게 검사하면 backend로 넘기기전에 분개번호별로 특이사항을 '중복없이' 분개상세에 넣어줄 수 있다.
      // 중복을 피하기 위해 검사에 사용되는 키가 accountControlType과 분개번호인데 이 둘이 복합 프라이머리키와 같은 역할을 한다고 보면
      // 이 가독성 박살난 코드를 이해하는데 도움이 되지않을까 한다.
      for (let k = 0; k < newJournalDetailList.length; k++) {
        for (let n = 0; n < newDescriptionList.length; n++) {
          console.log(
            "newJournalDetailList[k].journalNo: ",
            newJournalDetailList
          );
          console.log("newDescriptionList[n].journalNo: ", newDescriptionList);
          if (
            newJournalDetailList[k].journalNo ===
            newDescriptionList[n].journalNo
          ) {
            const optionKeys = Object.keys(newDescriptionList[n].option);
            for (let j = 0; j < optionKeys.length; j++) {
              console.log("키값나오냐: ", optionKeys[j]); // 수정된 부분
              console.log(
                "컨트롤타입은: ",
                newJournalDetailList[k].accountControlType
              ); // 수정된 부분
              if (
                optionKeys[j].toUpperCase() ===
                newJournalDetailList[k].accountControlType
              ) {
                console.log(
                  "Description 안설정됨: ",
                  newDescriptionList[n].option[optionKeys[j]]
                ); // 설정된 값 확인
                newJournalDetailList[k].description =
                  newDescriptionList[n].option[optionKeys[j]];
                console.log(
                  "Description 설정됨: ",
                  newJournalDetailList[k].description
                ); // 설정된 값 확인
                break;
              }
            }
          }
        }
        flag.value = true;
      }
      for (let h = 0; h < newJournalDetailList.length; h++) {
        console.log("저널폼넘버 :", newJournalForm.value[i].journalNo);
        console.log(
          "newJournalDetailList의 넘버 :",
          newJournalDetailList[h].journalNo
        );
        console.log("newJournalDetailList :", newJournalDetailList);
        console.log(
          "newJournalDetailList[" + h + "] :",
          newJournalDetailList[h]
        );
        if (
          newJournalForm.value[i].journalNo ===
          newJournalDetailList[h].journalNo
        ) {
          console.log("푸시전 : ", JSON.stringify(journalDetailList, null, 2));
          journalDetailList.push(newJournalDetailList[h]);
          console.log("푸시후 : ", JSON.stringify(journalDetailList, null, 2));
        }
      }

      console.log("값검사 :", journalDetailList);

      if (flag.value) {
        newJournalList.push({
          journalNo: newJournalForm.value[i].journalNo,
          accountCode: newJournalForm.value[i].accountCode,
          accountName: newJournalForm.value[i].accountName,
          balanceDivision: newJournalForm.value[i].balanceDivision,
          customerCode: newJournalForm.value[i].customerCode,
          customerName: newJournalForm.value[i].customerName,
          leftDebitPrice: newJournalForm.value[i].leftDebitPrice,
          rightCreditPrice: newJournalForm.value[i].rightCreditPrice,
          //2024.05.24 새로운 분개리스트로 넘어가도록 수정
          newJournalDetailList: journalDetailList,
        });
      } else {
        newJournalList.push({
          journalNo: newJournalForm.value[i].journalNo,
          accountCode: newJournalForm.value[i].accountCode,
          accountName: newJournalForm.value[i].accountName,
          balanceDivision: newJournalForm.value[i].balanceDivision,
          customerCode: newJournalForm.value[i].customerCode,
          customerName: newJournalForm.value[i].customerName,
          leftDebitPrice: newJournalForm.value[i].leftDebitPrice,
          rightCreditPrice: newJournalForm.value[i].rightCreditPrice,
          newJournalDetailList: [],
        });
      }
    }

    //resultSlipForm : 전표 정보
    //newJournalList : 한 전표에 들어가는 분개들 리스트
    //newJournalList의 newJournalDetailList : 한 분개에 들어가는 여러 분개 상세
    const resultSlipForm: any = [
      {
        accountPeriodNo: newSlipForm.value[0].accountPeriodNo,
        slipNo: newSlipForm.value[0].slipNo,
        slipType: newSlipForm.value[0].slipType,
        reportingEmpCode: newSlipForm.value[0].reportingEmpCode,
        reportingEmpName: newSlipForm.value[0].reportingEmpName,
        expenseReport: newSlipForm.value[0].expenseReport,
        postingDate: newSlipForm.value[0].postingDate,
        reportingDate: newSlipForm.value[0].reportingDate,
        deptCode: newSlipForm.value[0].deptCode,
        slipStatus: "",
        journalList: newJournalList,
        noteDetail: toRaw(newNoteDetailForm.value),
      },
    ];

    console.log("추가 폼 :", resultSlipForm[0]);
    // console.log("아직 안 보낸다 :", resultSlipForm[0]);
    const response = await accountStore().CREATE_SLIP(resultSlipForm[0]);

    console.log("추가 되었습니다: ", response);

    isAddSlipSuccessAlertVisible.value = true;
    isConfirmDialogVisible.value = false;

    addSlipSuccess();
  }
};

// 리스트로 DB 저장
const addSlipListToDB = async () => {
  const lengthOfKeys = Object.keys(toRaw(slipJsonData.value)).length; // 엑셀행
  const slipJsonList: any = toRaw(slipJsonData.value);

  const flag = ref("");

  const fileSlipForm = [];
  const fileJournalForm = [];
  const fileJournalDetailForm = [];
  const fileNoteDetailForm = [];

  for (let i = 0; i < lengthOfKeys; i++) {
    if (slipJsonList[`${i}`][0] === "전표") {
      flag.value = "slip";
      continue;
    } else if (slipJsonList[`${i}`][0] === "분개") {
      flag.value = "journal";
      continue;
    } else if (slipJsonList[`${i}`][0] === "분개상세") {
      flag.value = "journalDetail";
      continue;
    } else if (slipJsonList[`${i}`][0] === "어음상세") {
      flag.value = "noteDetail";
      continue;
    }

    // 칼럼 수 = 전표:9, 분개:9, 분개상세:4 고정
    if (flag.value === "slip") {
      fileSlipForm.push({
        slipType: slipJsonList[`${i}`][0],
        postingDate: slipJsonList[`${i}`][1],
        accountPeriodNo: slipJsonList[`${i}`][2],
        deptCode: slipJsonList[`${i}`][3],
        slipNo: slipJsonList[`${i}`][4],
        reportingEmpCode: slipJsonList[`${i}`][5],
        reportingEmpName: slipJsonList[`${i}`][6],
        reportingDate: slipJsonList[`${i}`][7],
        expenseReport: slipJsonList[`${i}`][8],
        slipStatus: "",
      });
    } else if (flag.value === "journal") {
      fileJournalForm.push({
        slipNo: slipJsonList[`${i}`][0],
        journalNo: slipJsonList[`${i}`][1],
        accountCode: slipJsonList[`${i}`][2],
        accountName: slipJsonList[`${i}`][3],
        balanceDivision: slipJsonList[`${i}`][4],
        customerCode: slipJsonList[`${i}`][5],
        customerName: slipJsonList[`${i}`][6],
        leftDebitPrice: slipJsonList[`${i}`][7],
        rightCreditPrice: slipJsonList[`${i}`][8],
      });
    } else if (flag.value === "journalDetail") {
      fileJournalDetailForm.push({
        journalNo: slipJsonList[`${i}`][0],
        accountControlName: slipJsonList[`${i}`][1],
        accountControlType: slipJsonList[`${i}`][2],
        journalDescription: slipJsonList[`${i}`][3],
      });
    } else if (flag.value === "noteDetail") {
      fileNoteDetailForm.push({
        journalNo: slipJsonList[`${i}`][0],
        noteNo: slipJsonList[`${i}`][1],
        noteType: slipJsonList[`${i}`][2],
        accountInnerCode: slipJsonList[`${i}`][3],
        issuanceDate: slipJsonList[`${i}`][4],
        maturityDate: slipJsonList[`${i}`][5],
        drawer: slipJsonList[`${i}`][6],
        endorser: slipJsonList[`${i}`][7],
        drawee: slipJsonList[`${i}`][8],
      });
    }
  }

  // 중간 세팅 완료
  // 백단 보낼 파일세팅 시작

  const fileJournalList = [];
  for (let j = 0; j < fileJournalForm.length; j++) {
    const fileJournalDetailList = [];
    const journalDetailflag = ref(false);
    for (let k = 0; k < fileJournalDetailForm.length; k++) {
      if (fileJournalDetailForm[k].journalNo === fileJournalForm[j].journalNo) {
        fileJournalDetailList.push(fileJournalDetailForm[k]);
        journalDetailflag.value = true;
      }
    }
    if (journalDetailflag.value) {
      fileJournalList.push({
        slipNo: fileJournalForm[j].slipNo,
        journalNo: fileJournalForm[j].journalNo,
        accountCode: fileJournalForm[j].accountCode,
        accountName: fileJournalForm[j].accountName,
        balanceDivision: fileJournalForm[j].balanceDivision,
        customerCode: fileJournalForm[j].customerCode,
        customerName: fileJournalForm[j].customerName,
        leftDebitPrice: fileJournalForm[j].leftDebitPrice,
        rightCreditPrice: fileJournalForm[j].rightCreditPrice,
        journalDetailList: fileJournalDetailList,
      });
    } else {
      fileJournalList.push({
        slipNo: fileJournalForm[j].slipNo,
        journalNo: fileJournalForm[j].journalNo,
        accountCode: fileJournalForm[j].accountCode,
        accountName: fileJournalForm[j].accountName,
        balanceDivision: fileJournalForm[j].balanceDivision,
        customerCode: fileJournalForm[j].customerCode,
        customerName: fileJournalForm[j].customerName,
        leftDebitPrice: fileJournalForm[j].leftDebitPrice,
        rightCreditPrice: fileJournalForm[j].rightCreditPrice,
        journalDetailList: [],
      });
    }
  }

  const fileSlipList = [];
  for (let i = 0; i < fileSlipForm.length; i++) {
    const journalList = [];
    const journalflag = ref(false);
    for (let j = 0; j < fileJournalList.length; j++) {
      if (fileJournalList[j].slipNo === fileSlipForm[i].slipNo) {
        journalList.push(fileJournalList[j]);
        journalflag.value = true;
      }
    }
    if (journalflag.value) {
      fileSlipList.push({
        slipType: fileSlipForm[i].slipType,
        postingDate: fileSlipForm[i].postingDate,
        accountPeriodNo: fileSlipForm[i].accountPeriodNo,
        deptCode: fileSlipForm[i].deptCode,
        slipNo: fileSlipForm[i].slipNo,
        reportingEmpCode: fileSlipForm[i].reportingEmpCode,
        reportingEmpName: fileSlipForm[i].reportingEmpName,
        reportingDate: fileSlipForm[i].reportingDate,
        expenseReport: fileSlipForm[i].expenseReport,
        slipStatus: "",
        journalList: journalList,
        noteDetail: fileNoteDetailForm,
      });
    } else {
      fileSlipList.push({
        slipType: fileSlipForm[i].slipType,
        postingDate: fileSlipForm[i].postingDate,
        accountPeriodNo: fileSlipForm[i].accountPeriodNo,
        deptCode: fileSlipForm[i].deptCode,
        slipNo: fileSlipForm[i].slipNo,
        reportingEmpCode: fileSlipForm[i].reportingEmpCode,
        reportingEmpName: fileSlipForm[i].reportingEmpName,
        reportingDate: fileSlipForm[i].reportingDate,
        expenseReport: fileSlipForm[i].expenseReport,
        slipStatus: "",
        journalList: [],
        noteDetail: fileNoteDetailForm,
      });
    }
  }

  console.log("파일로 전표추가 폼", { slipList: fileSlipList });

  const slipList = { slipList: fileSlipList };

  const response = await accountStore().CREATE_SLIP_FILE(slipList);

  console.log("추가 되었습니다: ", response);

  isAddSlipSuccessAlertVisible.value = true;
  isConfirmDialogVisible.value = false;

  addSlipSuccess();
};

// 파일등록 / 직접입력 체크
const checkFile = () => {
  if (selectedFileName.value.length > 0) addSlipListToDB();
  else addSlipToDB();
};

// 추가시 이동 페이지
const toProofReceipt: RouteLocationRaw = {
  name: "account-account-ReceiptFormPage",
};

const addSlipSuccess = () => {
  setTimeout(() => {
    router.push(toProofReceipt);
  }, 1000);
};

const editItem = (item: any, e: any) => {
  console.log("item", item);
  console.log("e", e);
  console.log("item.editing", item.editing);

  if (item.accountControlType === "SELECT") {
    isCustomerCodeDialogVisible.value = true;

    watch(selectedCustomerCode, (newVal) => {
      if (selectedCustomerCode.value) {
        item.description = newVal;
        console.log(selectedCustomerCode);
      }
    });
  } else if (item.accountControlType === "TEXT") {
    item.description = e.target.value;
  }
};

// 2024.05.23~ 2024.06.11 공사 내용

//2024.05.23 분개 상세 Select 메뉴 불러오기
const fetchJournalDetailSelect = async () => {
  console.log("들어는 옴");
  await accountStore().SEARCH_DETAIL_select();
  console.log("분개상세 셀렉트 왓다!", accountStore().detailSelectList);

  selectList.value = Object.values(accountStore().detailSelectList).map(
    (item: any) => item.accountAssociatesName
  );
};

// 분개행 삭제
const VDataTabledeleteJournalRow = () => {
  console.log("삭제할 분개:", selectedItems.value);

  // 모든 slipNo에 대해 체크된 분개 삭제
  journalMap.value.forEach((journals, slipNo) => {
    // selectedItems에 포함되지 않은 분개만 남김
    const filteredJournals = journals.filter(
      (journal) => !selectedItems.value.includes(journal.journalNo)
    );

    // 만약 필터링 후에도 분개가 남아 있으면 유지, 없으면 삭제
    if (filteredJournals.length > 0) {
      journalMap.value.set(slipNo, filteredJournals);
    } else {
      journalMap.value.delete(slipNo);
    }
  });

  // 선택된 분개 초기화
  selectedItems.value = [];

  // noteDetailCard 숨김
  isNoteDetailCardVisible.value = false;

  console.log("삭제 후 journalMap:", journalMap.value);
};

// 2024.05.24 분개 상세 저장
// 분개 상세에서 입력받은 특이사항들을 newDescriptionList에 담는다.
// newDescriptionList는 DB로 보내기전 newJournalDetailList에 담겨서 보내진다.
const updateNewDetailForm = async () => {
  // 중복 방지 체크용 변수
  let check = false;

  try {
    //newDescriptionList가 이전에 세팅된 적이 있다면 업데이트된 분개상세를 덮어씌운다.
    //그런다음 check를 true로 바꾸며 아래 if문을 무시한다.
    for (let i = 0; i < newDescriptionList.length; i++) {
      if (newDescriptionList[i].journalNo === newDescriptionAccount.journalNo) {
        newDescriptionList[i] = {
          ...newDescriptionAccount,
          option: newDescription.value,
        };
        check = true;
        break;
      }
    }

    //**위에서 check가 true가 되면 무시당하는 if문**
    //true가 아니라는건 새로운 분개상세가 추가되어야 한다는 뜻이다.
    //따라서 push를 이용해 입력받은 데이터를 newDescriptionList에 추가한다.
    //여기서 newDescription은 분개상세의 내용을 담고있는 JSON이다.
    //따라서 계정에 따라 특이사항이 여러개면 배열이 담길수도, 아니면 단일값이 담길수도 있다.
    if (check === false)
      newDescriptionList.push({
        ...newDescriptionAccount,
        option: newDescription.value,
      });

    console.log("분개상세 추가", newDescriptionList);

    //newDescriptionList에 잘 추가했다면 newDescription는 리셋시켜서 사용자가 다시 입력할 수 있게 한다.
    newDescription.value = {
      text: "",
      calendar: null,
      select: "",
      search: "",
    };

    // newDescriptionAccount.value가 무슨 일이 있어도 항상 객체로 존재하도록 보장한다.
    // 만약 newDescriptionAccount.value가 null이나 undefined가 되더라도 뒤에 {}때문에 객체로 존재하게 된다.
    newDescriptionAccount.value = newDescriptionAccount.value || {};

    // newDescriptionAccount.value가 재입력을 받을수 있게 내용을 초기화한다.
    Object.assign(newDescriptionAccount.value, {
      journalNo: "",
      accountName: "",
      option: "",
    });
  } catch (error) {
    console.error(error);
  }
};

//2024.05.22 분개 상세 메뉴 초기화
const resetVisible = () => {
  isDeptVisible.value = false;
  isTextVisible.value = false;
  isCalendarVisible.value = false;
  isSelectVisible.value = false;
};

//2024. 06. 10 모달 완성 - 분개상세에 모달이 오면 사용되는 함수
const deptCodeSelected = ({ selected, row }: any) => {
  console.log("여기부서왔는지부터 보자", row.internalItem.columns.deptName);
  newDescription.value.search = row.internalItem.columns.deptName;
  console.log("Description 들어갔냐2", newDescription.value.search);
};
// 여기까지 2024.05.23~ 2024.06.11 공사 내용 끝
</script>

<template>
  <p class="text-2xl mb-6">전표 추가</p>
  <div
    class="app-user-search-filter d-flex align-center flex-wrap gap-4 justify-end"
  >
    <!-- 전표 파일 추가 -->
    <div>
      <VBtn color="primary" variant="outlined" @click="refInputEl?.click()">
        <VIcon start icon="tabler-link" size="22" />
        <span class="d-none d-sm-block">파일추가</span>
      </VBtn>
      <input
        ref="refInputEl"
        type="file"
        id="fileInput"
        hidden
        accept=".xls, .xlsx"
        :key="fileKey"
        @change="onChange"
      />
      <span class="file-input-label">{{ selectedFileName }}</span>
    </div>
    <!-- 전표 수정 Dialog -->
    <EditAddSlipModal
      v-model:isEditSlip="isEditSlip"
      :defenseSlip="selectedSlips.length !== 1"
      @submit="updateSlipForm"
      :selectSlip="selectedSlipData"
    />
    <!-- 전표 추가 Dialog -->
    <AddSlipModal
      v-model:isDialogVisible="isSlipDialogVisible"
      :defenseSlip="openSlipButton"
      @submit="updateNewSlipForm"
    />
    <!-- 분개 추가 Dialog -->
    <AddJournalModal
      v-model:isDialogVisible="isJournalDialogVisible"
      :defenseJournal="!openJournalButton"
      @submit="updateNewJournalForm"
    />

    <!-- 분개상세 추가 Dialog -->
    <AddJournalDetailModal
      v-model="isJournalDetailDialogVisible"
      :selectedNo="rowJournalNo"
      @submit="updatenewJournalModalForm"
      @dialogClose="
        isJournalDetailDialogVisible = !isJournalDetailDialogVisible
      "
    />
    <VBtn
      color="primary"
      @click="isConfirmDialogVisible = !isConfirmDialogVisible"
      :disabled="!isMatchingDebitCredit"
    >
      <VIcon start icon="tabler-stack-push" size="22" />
      체크한 전표와 분개 저장
    </VBtn>
  </div>

  <div>
    <div class="d-flex align-center justify-between">
      <h1>전표</h1>
      <div class="d-flex gap-1 ml-auto">
        <!-- <VBtn
          textalign="right"
          color="primary"
          :disabled="selectedSlips.length !== 1"
          @click="handleAddJournal"
        >
          <VIcon start icon="tabler-plus" size="22" />
          분개 추가
        </VBtn> -->

        <VBtn
          textalign="right"
          color="primary"
          :disabled="selectedSlips.length === 0"
          @click="VDataTabledeleteSlipRow"
        >
          <VIcon start icon="tabler-trash" size="22" />
          선택한 전표 삭제
        </VBtn>
      </div>
    </div>

    <!-- 전표테이블 -->
    <!-- 2025.03.18 오상섭 -->
    <VDataTable
      :headers="slipField"
      :items="newSlipForm"
      item-value="slipNo"
      v-model="selectedSlips"
      show-select
    />
    <!-- 전표테이블 -->
    <div class="d-flex align-center justify-between">
      <h1>분개</h1>
      <VBtn
        textalign="right"
        color="primary"
        class="ml-auto"
        @click="VDataTabledeleteJournalRow"
      >
        <VIcon start icon="tabler-trash" size="22" />
        선택한 분개 삭제
      </VBtn>
    </div>
    <!--분개 테이블 -->
    <VDataTable
      :headers="journalField"
      :items="newJournalForm"
      item-value="journalNo"
      @click:row="isdetail"
      v-model="selectedItems"
      show-select
    />
    <!-- 어음상세 입력 카드 -->
    <div class="text-2xl mb-2" style="margin: 25px 0 0 0">
      <!-- 어음상세 -->
      <VCard v-if="isNoteDetailCardVisible" title="어음상세" class="mb-6">
        <VCardText>
          <VRow>
            <VCol cols="12" sm="6" md="4">
              <AppTextField v-model="rowJournalNo" label="분개번호" disabled />
            </VCol>
            <VCol cols="12" sm="6" md="4">
              <AppTextField
                v-model="noteNo"
                label="어음번호"
                placeholder="123-12345678-12345678-1"
                :rules="noteRules"
              />
            </VCol>
            <VCol cols="12" sm="6" md="4">
              <AppSelect v-model="noteType" :items="options" label="어음유형" />
            </VCol>
            <VCol cols="12" sm="6" md="4">
              <AppTextField
                v-model="accountInnerCode"
                label="계정코드"
                disabled
              />
            </VCol>
            <VCol cols="12" sm="6" md="4">
              <AppTextField
                v-model="issuanceDate"
                label="발행일자"
                type="date"
              />
            </VCol>
            <VCol cols="12" sm="6" md="4">
              <AppTextField
                v-model="maturityDate"
                label="만기일자"
                type="date"
              />
            </VCol>
            <VCol cols="12" sm="6" md="4">
              <AppTextField v-model="drawer" label="발행인" />
            </VCol>
            <VCol cols="12" sm="6" md="4">
              <AppTextField v-model="endorser" label="배서인" />
            </VCol>
            <VCol cols="12" sm="6" md="4">
              <AppSelect
                v-model="drawee"
                :items="draweeList"
                label="수금사원"
              />
            </VCol>
          </VRow>
        </VCardText>
        <VCardText class="d-flex justify-end flex-wrap gap-3">
          <VBtn color="primary" @click="updateNewNoteDetailForm">
            <VIcon start icon="tabler-database-plus" size="22" />
            저장
          </VBtn>
        </VCardText>
      </VCard>
    </div>

    <!-- 여기부터 공사중입니다 -->

    <!-- 분개 상세 입력 카드 -->
    <!-- 각 컴포넌트 타입은 계정관리코드 테이블과 연결되어 해당 컴포넌트가 필요할때만 보여지고 이용되게 설계되있다. -->

    <div class="d-flex align-center justify-between">
      <h1>분개 상세</h1>
      <VBtn
        textalign="right"
        color="primary"
        class="ml-auto"
        @click="TemporarySave"
      >
        <VIcon start icon="tabler-database-plus" size="22" />
        분개상세저장
      </VBtn>
    </div>
    <!--여기바꿔야함 items를 20250311-->
    <VDataTable
      :headers="journalDetailField"
      :items="journalDetailList"
      item-value="accountControlCode"
    >
      <!-- 분개번호 (그냥 값 표시) -->

      <!-- 상세내용 칼럼 -->

      <template #item.description="{ item }">
        <v-text-field
          @dblclick="editItem(item, $event)"
          v-model="item.description"
          class="clickable-text"
          @blur="editItem(item, $event)"
        >
        </v-text-field>
      </template>
    </VDataTable>

    <div
      v-if="isCodeDetailCardVisible"
      class="text-2xl mb-2"
      style="margin: 25px 0 0 0"
    >
      <h4>분개 상세</h4>
      <VCard title="분개상세" class="mb-6">
        <VCardText>
          <VRow>
            <VCol v-if="isCodeDetailCardVisible" cols="12" sm="6" md="4">
              <AppTextField
                v-model="newDescriptionAccount.journalNo"
                label="분개일련번호"
                disabled
              />
            </VCol>
            <VCol v-if="isCodeDetailCardVisible" cols="12" sm="6" md="4">
              <AppTextField
                v-model="newDescriptionAccount.accountName"
                label="계정과목"
                disabled
              />
            </VCol>
            <VCol v-if="isTextVisible" cols="12" sm="6" md="4">
              <AppTextField v-model="newDescription.text" :label="textName" />
            </VCol>
            <VCol v-if="isCalendarVisible" cols="12" sm="6" md="4">
              <AppDateTimePicker
                v-model="newDescription.calendar"
                :label="calendarName"
                placeholder="Select date"
                append-inner-icon="tabler-calendar-filled"
              />
            </VCol>
            <VCol v-if="isSelectVisible" cols="12" sm="6" md="4">
              <AppSelect
                v-model="newDescription.select"
                :items="selectList"
                :label="selectName"
              />
            </VCol>
            <VCol v-if="isDeptVisible" cols="12" sm="6" md="4">
              <AppTextField
                v-model="newDescription.search"
                :label="searchName"
                @click="isDeptDialogVisible = !isDeptDialogVisible"
              />
            </VCol>
          </VRow>
        </VCardText>
        <VCardText class="d-flex justify-end flex-wrap gap-3">
          <VBtn color="primary" @click="updateNewDetailForm">
            <VIcon start icon="tabler-database-plus" size="22" />
            저장
          </VBtn>
        </VCardText>
      </VCard>
    </div>

    <AddCustomerCodeModal
      v-model="isCustomerCodeDialogVisible"
      @selected="customerCodeSelected"
      @dialogClose="isCustomerCodeDialogVisible = !isCustomerCodeDialogVisible"
    />

    <AddDeptCodeModal
      v-model="isDeptDialogVisible"
      @selected="deptCodeSelected"
      @dialogClose="isDeptDialogVisible = !isDeptDialogVisible"
    />

    <div
      v-if="isNotCodeDetailCardVisible"
      class="text-2xl mb-2"
      style="margin: 25px 0 0 0"
    >
      <h4>분개 상세</h4>
      <VCard class="mb-3">
        &nbsp;&nbsp;&nbsp;&nbsp;해당 계정과목은 분개 상세 입력을 필요로 하지
        않습니다.
      </VCard>
    </div>
    <!-- 여기까지 공사중입니다 -->
  </div>
  <!-- 전표 등록 확인 Dialog -->
  <VDialog v-model="isConfirmDialogVisible" width="400">
    <!-- Dialog close btn -->
    <DialogCloseBtn @click="isConfirmDialogVisible = !isConfirmDialogVisible" />

    <VCard>
      <VCol style="text-align: center; margin-top: 10px">
        <!-- Dialog Content -->
        <VAvatar variant="outlined" size="120" color="success" rounded="pill">
          <VIcon size="90" icon="tabler-exclamation-mark" />
        </VAvatar>
      </VCol>

      <VCardText style="text-align: center">
        전표를 추가 하시겠습니까?
      </VCardText>
      <VCardText style="text-align: center">
        <VBtn color="success" @click="slipSave">
          <VIcon start icon="tabler-checkbox" size="22" />
          확인
        </VBtn>
        <VBtn
          color="blue-grey"
          variant="tonal"
          style="margin-left: 10px"
          @click="isConfirmDialogVisible = !isConfirmDialogVisible"
        >
          <VIcon start icon="tabler-x" size="22" />
          취소
        </VBtn>
      </VCardText>
    </VCard>
  </VDialog>
  <!-- 파일형식 오류 스낵바 -->
  <VSnackbar
    v-model="isFileUploadFailedSnackBarVisible"
    location="top"
    color="error"
    :timeout="1500"
  >
    <VIcon start icon="tabler-alert-circle-filled" />
    파일형식을 .xlsx / .xls 로 지정해주세요.
  </VSnackbar>
  <!-- 어음상세 저장 스낵바 -->
  <VSnackbar
    v-model="isNoteDetailSuccessAlertVisible"
    location="bottom"
    color="blue-grey"
    :timeout="1500"
  >
    <VIcon start icon="tabler-circle-check-filled" />
    어음상세 저장완료.
  </VSnackbar>
  <!-- 어음상세 작성요청 스낵바 -->
  <VSnackbar
    v-model="isRequestNoteDetailAlertVisible"
    location="top"
    color="blue-grey"
    :timeout="1500"
  >
    <VIcon start icon="tabler-alert-circle-filled" />
    어음상세를 입력해주세요.
  </VSnackbar>
  <!-- 모두 입력 요청 스낵바 -->
  <VSnackbar
    v-model="isRequestAllAlertVisible"
    location="top"
    color="blue-grey"
    :timeout="1500"
  >
    <VIcon start icon="tabler-alert-circle-filled" />
    모두 입력해주세요.
  </VSnackbar>
  <!-- 전표 추가 알림 스낵바 -->
  <VSnackbar
    v-model="isAddSlipSuccessAlertVisible"
    location="top"
    color="success"
    :timeout="1500"
  >
    <VIcon start icon="tabler-circle-check-filled" />
    전표 추가완료
  </VSnackbar>
</template>

<style>
.justify-end {
  justify-content: flex-end;
}

/* Style the file input label */
.file-input-label {
  margin-left: 10px;
}
</style>
