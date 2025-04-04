<script setup lang="ts">
import { VDataTable } from "vuetify/labs/VDataTable";
import { statementStore } from "@/store/account/statement";
import StatementModal from "@/components/account/statement/modal/StatementModal.vue";

const isDialogVisible = ref(false);
const financialPositionList = ref([]);
const selectedRow = ref("");
const periodData = ref([]);
const content = ref(null);
let html2pdf: any;

const financial = [
  { title: "당기", sortable: false, key: "1" },
  { title: "당기 세부금액", key: "balanceDetail" },
  { title: "당기 합계금액", key: "balanceSummary" },
  { title: "과목", key: "accountName" },
  { title: "전기 세부금액", key: "preBalanceDetail" },
  { title: "전기 합계금액", key: "preBalanceSummary" },
  { title: "전기", key: "2" },
];

const searchFinancialPosition = async () => {
  const payload = {
    accountPeriodNo: selectedRow.value + 1,
    earlyNo: selectedRow.value,
  };

  console.log("payload", payload);
  await statementStore().SEARCH_FINANCIAL_POSITION_LIST(payload);
  financialPositionList.value = statementStore().financialPositionList;
  console.log("재무😁😎😍", financialPositionList.value);
};

// 파일 다운로드
const downloadPDF = async () => {
  if (!html2pdf) html2pdf = await import("html2pdf.js");

  html2pdf.default(content.value);
};

const getRowStyle = (lev) => {
  switch (lev) {
    case 0:
      return "font-weight: bold; font-size: 1.3em; ";
    case 1:
      return "font-weight: bold; font-size: 1.3em; ";
    case 2:
      return "font-weight: bold; font-size: 1.2em;";
    case 3:
      return "font-weight: bold; font-size: 1.1em;";
    case 4:
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
    case 4:
      indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"; // 18 spaces
      break;
    default:
      indent = "";
  }
  return indent + accountName;
};

const getRowClass = (item) => {
  switch (item.accountName) {
    case "자산":
      return "tier-blue";
    case "부채":
      return "tier-red";
    case "자본":
      return "tier-green";
    default:
      return "";
  }
};
</script>

<template>
  <p class="text-2xl mb-6">재무상태표</p>
  <div ref="content" class="demo-space-x app-user-search-filter d-flex align-center flex-wrap gap-4 justify-start">
    <!-- <VBtn color="primary" > 결산 </VBtn> -->
    <StatementModal v-model="isDialogVisible" :period-data="periodData" @selected="selectedRow = $event"
      @resultList="searchFinancialPosition" />

    <VBtn color="primary" @click="downloadPDF">
      <VIcon start icon="tabler-file-type-pdf" size="22" />
      출력
    </VBtn>

    <v-table>
      <thead>
        <tr class="h">
          <th>계정명</th>
          <th>당기 세부금액</th>
          <th>당기 합계금액</th>
          <th>전기 세부금액</th>
          <th>전기 합계금액</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in financialPositionList" :class="getRowClass(item)" :style="getRowStyle(item.lev)">
          <td><span :style="getRowStyle(item.lev)" v-html="getIndentedName(item.accountName, item.lev)"></span></td>
          <td v-if="item.ctotal1 !== 0">{{ item.ctotal1 }}</td>
          <td v-else></td>
          <td v-if="item.ctotal2 !== 0">{{ item.ctotal2 }}</td>
          <td v-else></td>
          <td v-if="item.ptotal1 !== 0">{{ item.ptotal1 }}</td>
          <td v-else></td>
          <td v-if="item.ptotal2 !== 0">{{ item.ptotal2 }}</td>
          <td v-else></td>
        </tr>
      </tbody>
    </v-table>
  </div>
</template>

<style>
tr.tier-blue {
  background-color: rgb(170, 221, 226);
}

tr.tier-red {
  background-color: rgb(235, 201, 198);
}

tr.tier-green {
  background-color: rgb(214, 240, 219);
}



tr.h {
  color: white;
  font-weight: bold;
  font-size: 1.5em;
}
</style>