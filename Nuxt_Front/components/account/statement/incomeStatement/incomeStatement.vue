<script setup lang="ts">
import { ref } from "vue";
import { statementStore } from "@/store/account/statement";
import StatementModal from "@/components/account/statement/modal/StatementModal.vue";
const unionMerged = computed(() => {
  const result = new Map();

  // 1. 당기 먼저 넣기
  incomeList.value.forEach(curr => {
    result.set(curr.accountInnerCode, {
      accountInnerCode: curr.accountInnerCode,
      accountName: curr.accountName,
      lev: curr.lev,
      currentSummary: curr.totalAmount ?? 0,  // 🔁 수정: incomeSummary → totalAmount
      previousSummary: 0,
    });
  });

  // 2. 전기 데이터 병합
  incomeList2.value.forEach(prev => {
    const key = prev.accountInnerCode;
    if (result.has(key)) {
      result.get(key).previousSummary = prev.totalAmount ?? 0;  // 🔁 수정
    } else {
      result.set(key, {
        accountInnerCode: prev.accountInnerCode,
        accountName: prev.accountName,
        lev: prev.lev,
        currentSummary: 0,
        previousSummary: prev.totalAmount ?? 0,  // 🔁 수정
      });
    }
  });

  return Array.from(result.values());
});


const isDialogVisible = ref(false);
const incomeList = ref([]);
const incomeList2 = ref([]);
const selectedRow = ref();
const periodData = ref([]);
const content = ref(null);
const unionArray = ref([]);
let html2pdf: any;
const headers =  [
  { title: '계정코드', key: 'accountInnerCode' },
  { title: '계정명', key: 'accountName' },
  { title: '상위 계정코드', key: 'parentAccountCode' },
  { title: '금액', key: 'income' },
  { title: '누적금액', key: 'incomeSummary' },
  { title: '기수번호', key: 'periodNo' },
  ,
] as any
const searchIncomeStatement = async () => {

  await getCurrentPeriod();
    
  await  getPriorPeriod();
  console.log("당기", incomeList.value);
  console.log("전기: ", incomeList2.value);
  unionArray.value = [...incomeList2.value, ...incomeList.value]
   console.log("unionArray: ", unionArray.value);
};

const getCurrentPeriod = async () => {
    const payload = {
    accountPeriodNo: selectedRow.value 
  };
    await statementStore().SEARCH_INCOME_STATEMENT_LIST(payload);
    incomeList.value = statementStore().incomeStatement; 
}
const 함수임 = ()=>{
  console.log('함수임');
}

const  getPriorPeriod = async () => {
  const payload2 = {
    accountPeriodNo: (selectedRow.value - 1)
  };
  await statementStore().SEARCH_INCOME_STATEMENT_LIST(payload2);
  incomeList2.value = statementStore().incomeStatement;
}

// 파일 다운로드
const downloadPDF = async () => {
  if (!html2pdf) html2pdf = await import("html2pdf.js");

  html2pdf.default(content.value);
};

const getRowStyle = (lev) => {
  switch (lev) {
    case 0:
      return "font-weight: bold; font-size: 1.3em; ";
    case 2:
      return "font-weight: bold; font-size: 1.1em;";
    case 3:
      return "font-style: italic;";
    case 4:
      return "font-weight: bold; font-size: 1.2em;";
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
  switch (item.lev) {
    case 0:
      return "tier-zero";
    case 1:
      return "blue1";
    case 4:
      return "blue4";
    default:
      return "";
  }
};
</script>

<template>
  
  <p class="text-2xl mb-6">손익계산서</p>


   <div>    <!-- <VBtn color="primary" > 결산 </VBtn>-->
    <StatementModal v-model="isDialogVisible" :period-data="periodData" @selected="selectedRow = $event"
      @resultList="searchIncomeStatement" />

    <VBtn color="primary" @click="downloadPDF">
      출력
    </VBtn></div>

<div ref="content" class="demo-space-x app-user-search-filter d-flex align-center flex-wrap gap-4 justify-start" >
  
<v-table>
  <thead>
    <tr class="h">
      <th>계정명</th>
      <th>당기 합계금액</th>
      <th>전기 합계금액</th>
    </tr>
  </thead>
  <tbody>
    <tr v-for="item in unionMerged" :key="item.accountInnerCode" :style="getRowStyle(item.lev)"  :class="getRowClass(item)">
      <td><span v-html="getIndentedName(item.accountName, item.lev)"></span></td>
      <td>{{ item.currentSummary.toLocaleString() }}</td>
      <td>{{ item.previousSummary.toLocaleString() }}</td>
    </tr>
  </tbody>
</v-table>
    
  </div>
</template>

<style>
tr.tier-zero {
  

}

tr.blue1 {
        
  color: aquamarine;

   
  font-weight: bold;
  border: 2px solid rgb(252, 4, 4)d8;
   border-left: 6px solid #7b1fa2;
  font-size: 1.5em;
}

tr.blue4 {

}

tr.h {
  /* font-weight: bold;
  font-size: 1.5em; */
}
</style>