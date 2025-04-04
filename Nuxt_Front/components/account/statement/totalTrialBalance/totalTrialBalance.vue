<script setup lang="ts">
import { VDataTable } from "vuetify/labs/VDataTable";
import { statementStore } from "@/store/account/statement";
import TotalModal from "@/components/account/statement/TotalTrialBalance/TotalModal.vue";

const isDialogVisible = ref(false);
const selectedRow = ref("");
const periodData = ref([]);
const earlyTotaltrialList = ref([]);
const totaltrialList = ref([]);

const content = ref(null);
let html2pdf: any;

const periodStartDate = ref("");

const earlyColumns = [
  { title: "차변잔액", sortable: false, key: "debitsSumBalance" },
  { title: "차변합계", key: "debitsSum" },
  { title: "계정과목", key: "accountName" },
  { title: "대변합계", key: "creditsSum" },
  { title: "대변잔액", key: "creditsSumBalance" },
];

const columns = [
  { title: "차변잔액", sortable: false, key: "debitsSumBalance" },
  { title: "차변합계", key: "debitsSum" },
  { title: "계정과목", key: "accountName" },
  { title: "대변합계", key: "creditsSum" },
  { title: "대변잔액", key: "creditsSumBalance" },
];

//호출 된적 없는 함수라 주석처리함 - 삭제해도 무방할듯? 
// const onRowSelected = (selectedrow: any) => {
//   console.log("번호", selectedrow);
//   selectedRow.value = selectedrow.value;
// };

// 합계잔액시산표 데이터 저장 및 조회
const searchTotalStatementList = async () => {
  const payload = { accountPeriodNo: selectedRow.value + 1 };

  console.log("payload", payload);
  await statementStore().SEARCH_EARLY_TOTAL_TRIAL_BALANCE_STATEMENT(payload);
  await statementStore().SEARCH_EARLY_TOTAL_TRIAL_BALANCE_STATEMENT_LIST(
    payload
  );
  earlyTotaltrialList.value = statementStore().earlyTotaltrialList;
};

// 합계잔액시산표 결산 및 조회
const excuteStatement = async () => {
  if (selectedRow.value == "0") periodStartDate.value = "2020";
  else if (selectedRow.value == "1") periodStartDate.value = "2021";
  else if (selectedRow.value == "2") periodStartDate.value = "2022";
  else if (selectedRow.value == "3") periodStartDate.value = "2023";

  const payload = {
    callResult: "Y",
    accountPeriodNo: selectedRow.value + 1,
    periodStartDate: periodStartDate.value,
  };

  await statementStore().SEARCH_STATEMENT_LIST(payload);

  const payload1 = {
    callResult: "SEARCH",
    accountPeriodNo: selectedRow.value + 1,
  };

  await statementStore().SEARCH_TOTAL_TRIAL_BALANCE_LIST(payload1);
  totaltrialList.value = statementStore().totaltrialList;
};

// 합계잔액시산표 결산 후 수정
const earlyStatement = async () => {
  const payload = {
    callResult: "Y",
    accountPeriodNo: selectedRow.value + 1,
    periodStartDate: periodStartDate.value,
  };

  await statementStore().SEARCH_TOTAL_TRIAL_BALANCE_STATEMENT(payload);
  await statementStore().SEARCH_TOTAL_TRIAL_BALANCE_LIST(payload);
  totaltrialList.value = statementStore().totaltrialList;
};

// 합계잔액시산표 결산취소
const cancelStatement = async () => {
  const payload = { callResult: "N", accountPeriodNo: selectedRow.value + 1 };

  await statementStore().SEARCH_CANCEL_STATEMENT(payload);
  await statementStore().SEARCH_TOTAL_TRIAL_BALANCE_LIST(payload);
  totaltrialList.value = statementStore().totaltrialList;
};

// 파일 다운로드
const downloadPDF = async () => {
  if (!html2pdf) html2pdf = await import("html2pdf.js");

  html2pdf.default(content.value);
};

const getRowStyle = (lev, accountName) => {
  if (lev === 3 && accountName === '합계') {
    return "font-weight: bold; font-size: 1.3em; background-color: yellow;"; // Example style for '합계' row
  }
  switch (lev) {
    case 1:
      return "font-weight: bold; font-size: 1.2em;";
    case 2:
      return "font-weight: bold; font-size: 1.1em;";
    case 3:
      return "font-style: italic;";
    default:
      return "";
  }
};

const getIndentedName = (accountName, lev) => {
  let indent = "";
  switch (lev) {
    case 2:
      indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"; // 6 spaces
      break;
    case 3:
      indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"; // 12 spaces
      break;
    default:
      indent = "";
  }
  return indent + accountName;
};

const getRowClass = (item) => {
  if (item.lev === 3 && item.accountName === '합계') {
    return "special-total";
  }

  switch (item.lev) {
    case 1:
      return "tier-zero";
    case 2:
      return "blue4";
    case 3:
      return "blue3";
    default:
      return "";
  }
};
</script>

<template>
  <p class="text-2xl mb-6">합계잔액시산표</p>
  <div ref="content" class="demo-space-x">
    <TotalModal v-model="isDialogVisible" :period-data="periodData" @selected="selectedRow = $event"
      @resultList="searchTotalStatementList" />

    <VBtn color="primary" @click="downloadPDF">
      <VIcon start icon="tabler-file-type-pdf" size="22" />출력
    </VBtn>
    <v-table>
      <thead>
        <tr class="h">
          <th>차변잔액</th>
          <th>차변합계</th>
          <th>계정과목</th>
          <th>대변합계</th>
          <th>대변잔액</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in earlyTotaltrialList" :class="getRowClass(item)"
          :style="getRowStyle(item.lev, item.accountName)">
          <td>{{ item.debitsSumBalance }}</td>
          <td>{{ item.debitsSum }}</td>
          <td><span :style="getRowStyle(item.lev)" v-html="getIndentedName(item.accountName, item.lev)"></span></td>
          <td>{{ item.creditsSum }}</td>
          <td>{{ item.creditsSumBalance }}</td>
        </tr>
      </tbody>
    </v-table>

    <div ref="content" class="demo-space-x app-user-search-filter d-flex align-center flex-wrap gap-4 justify-start">
      <VCol>
        <VBtn color="primary" style="margin-right: 15px" @click="excuteStatement">
          <VIcon start icon="tabler-report-money" size="22" />결산
        </VBtn>
        <!-- <VBtn
          color="primary"
          style="margin-right: 15px"
          @click="earlyStatement"
        >
          <VIcon start icon="tabler-file-pencil" size="26" />결산 후 수정
        </VBtn> -->
        <VBtn color="primary" style="margin-right: 15px" @click="cancelStatement">
          <VIcon start icon="tabler-file-off" size="24" />결산취소
        </VBtn>
        <VBtn color="primary" @click="downloadPDF">
          <VIcon start icon="tabler-file-type-pdf" size="22" />출력
        </VBtn>
      </VCol>
    </div>

    <VDataTable :headers="columns" :items-per-page="10" :items="totaltrialList" />
  </div>
</template>


<style>
tr.tier-zero {
  background-color: rgb(170, 221, 226);
}

tr.blue3 {
  background-color: rgb(255, 255, 255);
}

tr.blue4 {
  background-color: rgb(221, 236, 238);
}

tr.special-total {
  background-color: yellow;
  /* Example color for '합계' row */
  font-weight: bold;
}

tr.h {
  color: white;
  font-weight: bold;
  font-size: 1.5em;
}
</style>