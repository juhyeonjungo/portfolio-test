<script lang="ts" setup>
import { VDataTable } from "vuetify/labs/VDataTable";
import { statementStore } from "@/store/account/statement/";

const emits = defineEmits(["selected", "cancelModal", "resultList"]);

const periodDatalist = ref([]);
const selectedRow = ref("");

const isPeriodDataDialogVisible = ref(false);

const periodData = [
  { title: "기수일련번호", key: "accountPeriodNo", align: "center" },
  { title: "회계연도", key: "fiscalYear" },
  { title: "시작일자", key: "periodStartDate" },
  { title: "종료일자", key: "periodEndDate" },
];

//결산 전 조회 버튼으로 날짜 조회하는 함수 1-1
const fetchPeriodDataList = async () => {
  await statementStore().SEARCH_PERIOD_DATA_LIST();
  periodDatalist.value = statementStore().periodDataList;
};

//결산 전 조회 버튼으로 날짜 조회하는 함수 1
const setPeriod = () => {
  fetchPeriodDataList(); // 1-1
  isPeriodDataDialogVisible.value = true;
};

const onRowSelected = (selected: any, row: any) => {
  console.log(row);
  console.log(row.internalItem.index);
  selectedRow.value = row.internalItem.index;
  console.log("나와라😍", selectedRow.value);
  emits("selected", selectedRow.value);  //emit으로 부모쪽에 전달
};

const resultList = () => {
  emits("resultList");
  isPeriodDataDialogVisible.value = false;
};
</script>

<template>
  <VBtn color="primary" style="margin-right: 10px" @click="setPeriod">
    <VIcon start icon="tabler-search" size="22" />
    결산 전 조회
  </VBtn>
  <VDialog v-model="isPeriodDataDialogVisible" max-width="600">
    <VCard>
      <VCardText>
        <VRow>
          <VCol cols="12">
            <VCardText>
              <VDataTable :headers="periodData" :items="periodDatalist" @click:row="onRowSelected" />
            </VCardText>
          </VCol>
        </VRow>
      </VCardText>

      <VCardText class="d-flex justify-end flex-wrap gap-3">
        <VBtn variant="tonal" color="secondary" @click="isPeriodDataDialogVisible = !isPeriodDataDialogVisible">
          Cancel
        </VBtn>
        <VBtn @click="resultList"> OK </VBtn>
      </VCardText>
    </VCard>
  </VDialog>
</template>
