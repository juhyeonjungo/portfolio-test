<script setup lang="ts">
import { VDataTable } from "vuetify/labs/VDataTable";
import { baseStore } from "@/store/account/base";
import AccountCodeModal from "@/components/account/base/accountLedger/AccountCodeModal.vue";  //20240527

const accountCode = ref(""); //20240527
const accountName = ref(""); //20240527
const startDate = ref("");
const endDate = ref("");
const generalAccountLedgerList = ref([]);

const dialogVisible = ref(false); //20240527
const noDataAlert = ref(false);
const dateErrorAlert = ref(false);

const content = ref(null);
let html2pdf: any;


//내가새로만들거라 안써요 2025-03-14 
// const headers = [
//   { title: "등록일자", sortable: false, key: "reportingDate" },
//   { title: "차변금액", key: "leftDebitPrice" },
//   { title: "대변금액", key: "rightCreditPrice" },
//   { title: "잔액", key: "totalBalance" },
// ];

const headers = [
  { title: "등록일자", sortable: false, key: "approvalDate"},
  { title: "계정명", sortable: false, key: "accountName"},
  { title: "차변금액", key: "leftDebtorPrice" },
  { title: "대변금액", key: "rightCreditsPrice" },
  { title: "적요", key: "expenseReport"},
  { title: "잔액", key: "balance" },

];

//20240527--
const openAccountCodeModal = () => {
  dialogVisible.value = true;
};

const closeDialog = () => {
  dialogVisible.value = false;
};

const onSelected = ({ selected, row }: any) => {
  accountCode.value = row.internalItem.columns.accountInnerCode;
  accountName.value = row.internalItem.columns.accountName;
};
//--20240527

// 월별 조회 --기존에 있던 서치저널 함수 일부변경
const monthlySearchJournal = async () => {
  console.log(accountCode.value, startDate.value, endDate.value);
  await baseStore().SEARCH_GENERAL_ACCOUNT_LEDGER_LIST({ accountCode, startDate, endDate });

  generalAccountLedgerList.value = baseStore().generalAccountLedgerList;
  if (startDate.value === "" || endDate.value === "")
    dateErrorAlert.value = true;
  else if (baseStore().generalAccountLedgerList === "")
    noDataAlert.value = true;
};

// 일별 조회 --2024 -03-14 월별조회 없애버림  위에는 죽은코드 
const dailySearchJournal = async () => {
  console.log(accountName.value, startDate.value, endDate.value);
  await baseStore().SEARCH_GENERAL_ACCOUNT_LEDGER_LIST_Daily({ accountName, startDate, endDate });

  generalAccountLedgerList.value = baseStore().generalAccountLedgerList;
  if (startDate.value === "" || endDate.value === "")
    dateErrorAlert.value = true;
  else if (baseStore().generalAccountLedgerList === "")
    noDataAlert.value = true;
};

// 파일 다운로드
const downloadPDF = async () => {
  if (!html2pdf) html2pdf = await import("html2pdf.js");

  html2pdf.default(content.value);
};


const clearAccountName = ()=>{ 
 
accountName.value="";
   
}
</script>

<template>
  <VSnackbar v-model="dateErrorAlert" :timeout="2000" color="error" location="center">
    <div align="center" style="color: white">
      <VIcon start icon="tabler-alert-circle-filled" />날짜를 입력해주세요.
    </div>
  </VSnackbar>
  <VSnackbar v-model="noDataAlert" :timeout="2000" color="error" location="center">
    <div align="center" style="color: white">
      <VIcon start icon="tabler-alert-circle-filled" />데이터가 없습니다.
    </div>
  </VSnackbar>

  <p class="text-2xl mb-6">총계정원장</p>
  <VContainer>
    <VRow>
      <VCol md="3" gap="10">
        <VTextField v-model="accountName" :value="accountName" label="계정조회" persistent-placeholder
          @click="openAccountCodeModal"  clearable
   @click:clear="clearAccountName"  />
      </VCol>
      <VCol md="2">
        <VTextField v-model="startDate" label="시작일" type="date" style="min-width: 150px;" />
      </VCol>
      <VCol md="5">
        <VTextField v-model="endDate" label="종료일" type="date" style="max-width: 150px;" />
      </VCol>
      <VCol md="1">
        <VBtn color="primary" @click="dailySearchJournal">조회</VBtn>
      </VCol>
      <!-- <VCol md="1">
        <VBtn color="primary" @click="monthlySearchJournal">월별 조회</VBtn>
      </VCol> -->
      <VCol md="1">
        <VBtn color="primary" @click="downloadPDF">출력</VBtn>
      </VCol>
    </VRow>
  </VContainer>

  <div ref="content">
    <VCard>
      <VCardText>
        <!-- <VDataTable :headers="headers" :items="generalAccountLedgerList" :items-per-page="10" /> -->

                <VDataTable :headers="headers" :items="generalAccountLedgerList" :items-per-page="10" />

      </VCardText>
    </VCard>
  </div>

  <AccountCodeModal v-model="dialogVisible" @selected="onSelected" @closeDialog="closeDialog" />
</template>






<!-- 여기부터 원래 소스
<template>
  <VSnackbar
    v-model="dateErrorAlert"
    :timeout="2000"
    color="error"
    location="center"
  >
    <div align="center" style="color: white">
      <VIcon start icon="tabler-alert-circle-filled" />날짜를 입력해주세요.
    </div>
  </VSnackbar>
  <VSnackbar
    v-model="noDataAlert"
    :timeout="2000"
    color="error"
    location="center"
  >
    <div align="center" style="color: white">
      <VIcon start icon="tabler-alert-circle-filled" />데이터가 없습니다.
    </div>
  </VSnackbar>

  <p class="text-2xl mb-6">총계정원장</p>
  <VContainer>
    <VRow>
      <VCol md="4">
        <VForm>
          <VTextField v-model="startDate" label="시작일" type="date" />
        </VForm>
      </VCol>
      <VCol md="4">
        <VForm>
          <VTextField v-model="endDate" label="종료일" type="date" />
        </VForm>
      </VCol>
      <VCol>
        <VBtn color="primary" style="margin-right: 15px" @click="searchJournal">
          <VIcon start icon="tabler-search" />조회
        </VBtn>
        <VBtn color="primary" @click="downloadPDF">
          <VIcon start icon="tabler-note" />출력
        </VBtn>
      </VCol>
    </VRow>
  </VContainer>

  <div ref="content">
    <VCard>
      <VCardText>
        <VDataTable
          :headers="headers"
          :items="generalAccountLedgerList"
          :items-per-page="10"
        />
      </VCardText>
    </VCard>
  </div>
</template> -->
