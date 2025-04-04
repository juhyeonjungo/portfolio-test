<script setup lang="ts">
import { VDataTable } from "vuetify/labs/VDataTable";
import { baseStore } from "@/store/account/base";

import AddPeriodModal from "@/components/account/base/accountPeriodNo/AddPeriodModal.vue";
import UpdateCustomerModal from "@/components/account/base/customerManager/UpdateCustomerModal.vue";

const selectedCustomer = ref([]);

const accountCustomerList = ref([]);
const addDialogVisible = ref(false);
const updateDialogVisible = ref(false);
const updateErrorAlert = ref(false);
const deleteErrorAlert = ref(false);
const addSuccessAlert = ref(false);
const updateSuccessAlert = ref(false);
const deleteSuccessAlert = ref(false);
//정주현 
const accountPeriodNoList=ref([]);
const maxPeriodNo =ref();

const isDeleteDialogVisibie = ref(false);
const isDeleteSuccessVisible = ref(false);

const headers = [
  { title: "기수번호", key: "accountPeriodNo" },
  { title: "기수시작일", key: "periodStartDate" },
  { title: "기수종료일", key: "periodEndDate" },
  { title: "사업장코드", key: "workplaceCode" }

];
//정주현
const 함수임 = () => {
if (accountPeriodNoList.value.length === 0) {
    maxPeriodNo.value = null;
    return;
  }

  const maxItem = accountPeriodNoList.value.reduce((max, item) => {
    const current = Number(item.accountPeriodNo);
    const maxVal = Number(max.accountPeriodNo);
    return current > maxVal ? item : max;
  });

  maxPeriodNo.value = Number(maxItem.accountPeriodNo);
}
//정주현
// onMounted(async () => {
//   accountCustomerList.value = await baseStore().SEARCH_CUSTOMER_CODE();
// });

//로우의 값이 바뀔 때 마다 baseStore().selectedRow의 값 업데이트 하고 리렌더
watch(selectedCustomer, async (selectedValue: any) => {
  baseStore().selectedRow = toRaw(selectedValue[0]);
});

const confirm = async (value: any) => {
  if (value === "ADD") {
    addDialogVisible.value = false;
    addSuccessAlert.value = true;
  } else if (value === "UPDATE") {
    updateDialogVisible.value = false;
    updateSuccessAlert.value = true;
  }
  await baseStore().SEARCH_PERIOD_NO();
  accountPeriodNoList.value = baseStore().accountPeriodNoList;
};
//보기용 버튼 함수
const btnEvent = () =>{
  함수임()
  console.log('selectedCustomer 확인',selectedCustomer.value[0])
  console.log('selectedRow 확인',baseStore().selectedRow)
  console.log('accountPeriodNoList 확인', accountPeriodNoList.value)
  console.log('maxPeriodNo 확인', maxPeriodNo.value)
}
const rerenderTable = () => {
  // shallow copy (참조 변경 → Vue가 리렌더 트리거)
  accountPeriodNoList.value = [...accountPeriodNoList.value];
};
const cancelBtn = () => {
  addDialogVisible.value = false;
  updateDialogVisible.value = false;
};

const searchPeriodNoCode = async () => {
  await baseStore().SEARCH_PERIOD_NO();
  accountPeriodNoList.value = baseStore().accountPeriodNoList;
};

const openAddPeriodNoModal = async () => {
 await searchPeriodNoCode();     // ✅ 이게 끝나야 다음 줄 실행됨
  함수임();                       // ✅ searchPeriodNoCode 끝난 후 실행됨
  addDialogVisible.value = true;  // ✅ 그 다음에 실행됨
};

const openUpdatePeriodNoModal = () => {
   updateDialogVisible.value = true;
};

const deletePeriodNoCode = async () => {
  isDeleteDialogVisibie.value = false;

  const accountPeriodNo = selectedCustomer.value[0].accountPeriodNo;    

  await baseStore().DELETE_PERIOD_NO(accountPeriodNo);

  await searchPeriodNoCode();

  selectedCustomer.value = [];

  deleteSuccessAlert.value = true;
};

const deleteCustomerCheck = () => {
  if (selectedCustomer.value.length === 0) 
    deleteErrorAlert.value = true;
  else isDeleteDialogVisibie.value = true;
}
</script>

<template>
  <p class="text-2xl mb-6">기수 관리</p>

  <VSnackbar
    v-model="updateErrorAlert"
    :timeout="2000"
    color="error"
    location="center"
  >
    <div align="center" style="color: white">
      <VIcon start icon="tabler-alert-circle-filled" />수정할 거래처를
      선택해주세요.
    </div>
  </VSnackbar>
  <VSnackbar
    v-model="deleteErrorAlert"
    :timeout="2000"
    color="error"
    location="center"
  >
    <div align="center" style="color: white">
      <VIcon start icon="tabler-alert-circle-filled" />삭제할 거래처를
      선택해주세요.
    </div>
  </VSnackbar>
  <VSnackbar
    v-model="addSuccessAlert"
    :timeout="2000"
    color="success"
    location="center"
  >
    <div align="center" style="color: white">
      <VIcon start icon="tabler-circle-check-filled" />추가 되었습니다.
    </div>
  </VSnackbar>
  <VSnackbar
    v-model="updateSuccessAlert"
    :timeout="2000"
    color="success"
    location="center"
  >
    <div align="center" style="color: white">
      <VIcon start icon="tabler-circle-check-filled" />수정 되었습니다.
    </div>
  </VSnackbar>
  <VSnackbar
    v-model="deleteSuccessAlert"
    :timeout="2000"
    color="success"
    location="center"
  >
    <div align="center" style="color: white">
      <VIcon start icon="tabler-circle-check-filled" />삭제 되었습니다.
    </div>
  </VSnackbar>

  <VBtn color="primary" class="btn" @click="searchPeriodNoCode">
    <VIcon start icon="tabler-search" />조회
  </VBtn>
  <VBtn color="primary" class="btn" @click="openAddPeriodNoModal">
    <VIcon start icon="tabler-playlist-add" />추가
  </VBtn>
  <VBtn
    color="primary"
    class="btn"
    @click="openUpdatePeriodNoModal"
    :disabled="selectedCustomer.length === 0"
    >
    <VIcon start icon="tabler-user-edit" />
      수정
  </VBtn>
  <VBtn color="primary" class="btn" @click="deleteCustomerCheck" :disabled="selectedCustomer.length === 0">
    <VIcon start icon="tabler-trash" />삭제
    
  </VBtn>


  
  <VDataTable
    v-model="selectedCustomer"
    :headers="headers"
    :items="accountPeriodNoList"
    :items-per-page="15"
    select-strategy="single"
    return-object
    show-select
  />

  <AddPeriodModal
    v-model="addDialogVisible"
    @confirm="confirm('ADD')"
    @cancelBtn="cancelBtn"
    :max-period-no="maxPeriodNo"
  />
  <UpdatePeriodModal
    v-model="updateDialogVisible"
    @confirm="confirm('UPDATE')"
    @cancelBtn="cancelBtn"
  />
      <!-- 거래처 삭제 확인 Dialog -->
      <VDialog v-model="isDeleteDialogVisibie" width="400">
      <!-- Dialog close btn -->
      <DialogCloseBtn @click="isDeleteDialogVisibie = !isDeleteDialogVisibie" />

      <!-- Dialog Content -->
      <VCard title="거래처삭제">
        <VCardText> 해당 거래처를 삭제하시겠습니까? </VCardText>
        <VCardText class="d-flex justify-end">
          <VBtn
            color="blue-grey"
            variant="tonal"
            style="margin-left: 10px"
            @click="isDeleteDialogVisibie = false"
          >
            <VIcon start icon="tabler-x" size="22" />
            취소
          </VBtn>
          <VBtn style="margin-left: 10px" @click="deletePeriodNoCode">
            <VIcon start icon="tabler-trash" size="22" />
            삭제
          </VBtn>
        </VCardText>
      </VCard>
    </VDialog>
    <button @click = 'btnEvent'>selectedCustomer 확인용 버튼</button>
</template>

<style>
.btn {
  margin-right: 15px;
  margin-bottom: 15px;
}
</style>
