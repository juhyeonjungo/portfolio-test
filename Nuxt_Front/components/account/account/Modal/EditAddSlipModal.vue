<script setup lang="ts">
import AddDeptCodeModal from "@/components/account/account/Modal/AddDeptCodeModal.vue";

const propz = defineProps(["defenseSlip", "isEditSlip", "selectSlip"]);
const emit = defineEmits(["submit", "update:isEditSlip"]);

//isEditSlip  isDialogVisible
const selectedSlip = ref({});
watch(
  () => propz.selectSlip, // prop 값이 변경될 때 감지
  (newValue) => {
    if (newValue && newValue.length > 0) {
      selectedSlip.value = JSON.parse(JSON.stringify(newValue[0]));
      // 깊은 복사하여 독립적으로 관리
    }
  },
  { immediate: true, deep: true }
);
// Dialog 데이터칼럼
const accountPeriodNo = ref(propz.selectSlip?.[0]?.accountPeriodNo || "");
const slipNo = ref(propz.selectSlip?.[0]?.slipNo || "");
const slipType = ref(propz.selectSlip?.[0]?.slipType || "");
const reportingEmpCode = ref(propz.selectSlip?.[0]?.reportingEmpCode || "");
const reportingEmpName = ref(propz.selectSlip?.[0]?.reportingEmpName || "");
const deptCode = ref(propz.selectSlip?.[0]?.deptCode || "");
const deptName = ref(propz.selectSlip?.[0]?.deptName || "");
const expenseReport = ref(propz.selectSlip?.[0]?.expenseReport || "");
const postingDate = ref(propz.selectSlip?.[0]?.postingDate || "");
const reportingDate = ref(propz.selectSlip?.[0]?.reportingDate || "");
const slipStatus = ref(propz.selectSlip?.[0]?.slipStatus || "");
const status = ref(propz.selectSlip?.[0]?.status || "");

// const accountPeriodNo = ref("");
// const slipNo = ref("") ;
// const slipType = ref( "");
// const reportingEmpCode = ref("" );
// const reportingEmpName = ref( "");
// const deptCode = ref("");
// const deptName = ref("");
// const expenseReport = ref("");
// const postingDate = ref("");
// const reportingDate = ref("");
// const slipStatus = ref( "");
// const status = ref("");

const isDeptCodeDialogVisible = ref(false);

const isDeptSnackBarVisible = ref(false);
const isExpenseReportSnackBarVisible = ref(false);
const isPostingDateSnackBarVisible = ref(false);
const isReportingDateSnackBarVisible = ref(false);

const deptCodeSelected = ({ selected, row }: any) => {
  deptCode.value = row.internalItem.columns.deptCode;
  deptName.value = row.internalItem.columns.deptName;
};

// 오늘 날짜로 세팅
const setToday = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0"); // 월은 0부터 시작하므로 1을 더하고, 두 자리로 표현
  const day = String(today.getDate()).padStart(2, "0"); // 날짜를 두 자리로 표현

  postingDate.value = `${year}-${month}-${day}`;
  reportingDate.value = `${year}-${month}-${day}`;
};

const resetData = () => {
  accountPeriodNo.value = "";
  slipNo.value = "";
  slipType.value = "";
  reportingEmpCode.value = "";
  reportingEmpName.value = "";
  deptCode.value = "";
  deptName.value = "";
  expenseReport.value = "";
  postingDate.value = "";
  reportingDate.value = "";
};

const dialogModelValueUpdate = (val: boolean) => {
  emit("update:isEditSlip", val);
  console.log(val);
};

const formSubmit = () => {
  if (!selectedSlip.value.deptCode) {
    isDeptSnackBarVisible.value = true;
  } else if (!selectedSlip.value.expenseReport) {
    isExpenseReportSnackBarVisible.value = true;
  } else if (!selectedSlip.value.postingDate) {
    isPostingDateSnackBarVisible.value = true;
  } else if (!selectedSlip.value.reportingDate) {
    isReportingDateSnackBarVisible.value = true;
  } else {
    emit("submit", { ...selectedSlip.value }); // 부모 컴포넌트에 변경된 데이터 전달
    dialogModelValueUpdate(false); // 다이얼로그 닫기
  }
};
</script>


<template>
  <VDialog
    :model-value="propz.isEditSlip"
    max-width="600"
    @update:model-value="dialogModelValueUpdate"
  >
    <!-- Dialog Activator -->
    <template #activator="{ props }">
      <VBtn v-bind="props" :disabled="defenseSlip">
        <VIcon start icon="tabler-pencil-plus" size="22" />
        전표수정
      </VBtn>
    </template>

    <!-- Dialog close btn -->
    <DialogCloseBtn @click="dialogModelValueUpdate(false)" />

    <VCard title="전표 수정">
      <!-- <VBtn @click="()=>{console.log(propz.selectSlip);console.log(propz.selectSlip[0].accountPeriodNo); }">내가원하는거</Vbtn> -->
      <VCardText>
        <VRow>
          <VCol cols="12" sm="6" md="4">
            <AppTextField
              v-model="selectedSlip.accountPeriodNo"
              label="기수"
              disabled
            />
          </VCol>
          <VCol cols="12" sm="6" md="4">
            <AppTextField
              v-model="selectedSlip.slipNo"
              label="전표일렬번호"
              disabled
            />
          </VCol>
          <VCol cols="12" sm="6" md="4">
            <AppTextField
              v-model="selectedSlip.slipType"
              label="전표유형"
              disabled
            />
          </VCol>
          <VCol cols="12" sm="6" md="4">
            <AppTextField
              v-model="selectedSlip.reportingEmpCode"
              label="작성자코드"
              disabled
            />
          </VCol>
          <VCol cols="12" sm="6" md="4">
            <AppTextField
              v-model="selectedSlip.deptCode"
              label="부서코드"
              @click="isDeptCodeDialogVisible = !isDeptCodeDialogVisible"
            />
          </VCol>
          <VCol cols="12" sm="6" md="4">
            <AppTextField
              v-model="selectedSlip.expenseReport"
              label="품의내역"
              type="text"
            />
          </VCol>
          <VCol cols="12" sm="6" md="4">
            <AppTextField v-model="postingDate" label="사용일자" type="date" />
          </VCol>
          <VCol cols="12" sm="6" md="4">
            <AppTextField
              v-model="selectedSlip.reportingDate"
              label="작성일자"
              type="date"
            />
          </VCol>
        </VRow>
      </VCardText>

      <VCardText class="d-flex justify-end flex-wrap gap-3">
        <VBtn variant="outlined" @click="setToday">
          <VIcon start icon="tabler-calendar" />
          Today
        </VBtn>
        <VBtn
          variant="tonal"
          color="secondary"
          @click="dialogModelValueUpdate(false)"
        >
          <VIcon start icon="tabler-x" size="22" />
          취소
        </VBtn>
        <VBtn type="submit" @click="formSubmit">
          <VIcon start icon="tabler-pencil-plus" size="22" />
          전표수정
        </VBtn>
      </VCardText>
    </VCard>

    <AddDeptCodeModal
      v-model="isDeptCodeDialogVisible"
      @selected="deptCodeSelected"
      @dialogClose="isDeptCodeDialogVisible = !isDeptCodeDialogVisible"
    />
  </VDialog>
  <!-- 부서 선택 요청 스낵바 -->
  <VSnackbar
    v-model="isDeptSnackBarVisible"
    location="top"
    color="blue-grey"
    :timeout="1500"
  >
    <VIcon start icon="tabler-alert-circle-filled" />
    사용부서를 선택해주세요.
  </VSnackbar>
  <!-- 품의내역 작성 요청 스낵바 -->
  <VSnackbar
    v-model="isExpenseReportSnackBarVisible"
    location="top"
    color="blue-grey"
    :timeout="1500"
  >
    <VIcon start icon="tabler-alert-circle-filled" />
    품의내역을 입력해주세요.
  </VSnackbar>
  <!-- 사용일자 선택 요청 스낵바 -->
  <VSnackbar
    v-model="isPostingDateSnackBarVisible"
    location="top"
    color="blue-grey"
    :timeout="1500"
  >
    <VIcon start icon="tabler-alert-circle-filled" />
    사용일자를 선택해주세요.
  </VSnackbar>
  <!-- 작성일자 선택 요청 스낵바 -->
  <VSnackbar
    v-model="isReportingDateSnackBarVisible"
    location="top"
    color="blue-grey"
    :timeout="1500"
  >
    <VIcon start icon="tabler-alert-circle-filled" />
    작성일자를 선택해주세요.
  </VSnackbar>
</template>
