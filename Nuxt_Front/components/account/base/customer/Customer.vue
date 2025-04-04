<script setup lang="ts">
import { VDataTable } from "vuetify/lib/labs/components.mjs";
import { accountStore } from "@/store/account/account";
import { baseStore } from "@/store/account/base";

const fromDate = ref("");
const toDate = ref("");
const journalList = ref([]);
const customerJournalList=ref([]);
//        <!-- 2025.03.17 정주현 거래처별 원장 수정 -->
const customerCode = ref("");

const customerCodeSelected = ({ selected, row }: any) => {
  customerCode.value = row.internalItem.columns.customerCode;
  customerName.value = row.internalItem.columns.customerName;
};

//        <!--2025.03.17 정주현 거래처별 원장 수정-- >
const content = ref(null);
let html2pdf: any;
const isCustomerCodeDialogVisible = ref<Boolean>(false);


const columns = [
  { title: "", key: "&nbsp", sortable: false },
  {
    title: "전표번호",
    key: "slipNo",
  },
  {
    title: "승인일자",
    key: "approvalDate",
  },
   {
    title: "기수일련번호",
    key: "accountPeriodNo",
  },
  {
    title: "계정과목",
    key: "accountName",
  },
  {
    title: "복식부기",
    key: "balanceDivision",
  },
  {
    title: "거래처",
    key: "customerCode",
  },
  {
    title: "차변금액",
    key: "leftDebtorPrice",
  },
  {
    title: "대변금액",
    key: "rightCreditsPrice",
  },
  {
    title: "적요",
    key: "expenseReport",
  },

];

// 분개 전체 조회
const fetchJournal = async () => {
  const startDate = fromDate.value;
  const endDate = toDate.value;

  await accountStore().SEARCH_JOURNAL({ startDate, endDate});
  journalList.value = accountStore().journalList;

  if (journalList.value.length === 0) {
    alert("해당범위에는 데이터가 존재하지않습니다.");
    journalList.value = [];
  }
};
//        <!--2025.03.17 정주현 거래처별 원장 수정-- >
// 거래처별 분개 조회

const fetchCustomerJournal = async ()=>{
  const startDate = fromDate.value;
  const endDate = toDate.value;
  const customerCodes  =customerCode.value;
  await baseStore().SEARCH_CUSTOMER_JOURNAL({ startDate, endDate, customerCodes });
  customerJournalList.value=baseStore().customerJournalList;
   if (customerJournalList.value.length === 0) {
    alert("해당범위에는 데이터가 존재하지않습니다.");
    customerJournalList.value = [];
  }
//예외처리 만들어야됨

}
//        <!--2025.03.17 정주현 거래처별 원장 수정-- >


// 파일 다운로드
const downloadPDF = async () => {
  if (!html2pdf) html2pdf = await import("html2pdf.js");

  html2pdf.default(content.value);
};
</script>

<template>
  <p class="text-2xl mb-6">거래처별원장</p>
  <div style="font-family: 'Franklin Gothic Demi'">
    <div class="custom-search">
      <VRow>
        <VCol md="3">
          <VForm>
            <VFormGroup>
              <label>시작일</label>
              <AppDateTimePicker v-model="fromDate" title="시작일" placeholder="Select date"
                append-inner-icon="tabler-calendar-filled" />
            </VFormGroup>
          </VForm>
        </VCol>
        <VCol md="3">
          <VForm>
            <VFormGroup>
              <label>종료일</label>
              <AppDateTimePicker v-model="toDate" title="종료일" placeholder="Select date"
                append-inner-icon="tabler-calendar-filled" />
            </VFormGroup>
          </VForm>
        </VCol>
        <!-- 2025.03.17 정주현 거래처별 원장 수정 -->
        <VCol cols="12" sm="6" md="4">
          <AppTextField v-model="customerCode" label="거래처코드" @click="
            isCustomerCodeDialogVisible = !isCustomerCodeDialogVisible
            " />
        </VCol>
        
            <AddCustomerCodeModal
      v-model="isCustomerCodeDialogVisible"
      @selected="customerCodeSelected"
      @dialogClose="isCustomerCodeDialogVisible = !isCustomerCodeDialogVisible"
    />
            <!-- 2025.03.17 정주현 거래처별 원장 수정 -->
        <VCol style="margin-top: 23px">
          <VBtn color="primary" style="margin-right: 15px" @click="fetchCustomerJournal">
            <VIcon start icon="tabler-search" size="22" />
            조회
          </VBtn>
          <VBtn color="primary" @click="downloadPDF">
            <VIcon start icon="tabler-note" size="22" />
            출력
          </VBtn>
        </VCol>
      </VRow>
    </div>
    <div ref="content" style="margin-top: 20px">
      <VCard>
        <VCardText>
         <VDataTable :headers="columns" :items="customerJournalList" :items-per-page="15" style="font-size: 14px;"/>
        </VCardText>
      </VCard>
    </div>
  </div>
</template>
