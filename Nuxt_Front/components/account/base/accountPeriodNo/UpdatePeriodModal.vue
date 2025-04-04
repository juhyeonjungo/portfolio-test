<script setup lang="ts">
import { baseStore } from "@/store/account/base";

const emits = defineEmits(["confirm", "cancelBtn"]);
const updateErrorAlert = ref(false);


const confirm = (UPDATE: any) => {
  emits("confirm", UPDATE);
};

const cancelBtn = () => {
  emits("cancelBtn");
};

const updateCustomerData = async () => {
  const updatePeriodInfo = {
    periodNoList: [
      {
        workplaceCode: baseStore().selectedRow.workplaceCode,
        periodStartDate: baseStore().selectedRow.periodStartDate,
        accountPeriodNo: baseStore().selectedRow.accountPeriodNo,
        periodEndDate: baseStore().selectedRow.periodEndDate,
      },
    ],
  };

  await baseStore().UPDATE_PERIOD_NO(updatePeriodInfo);

  if (baseStore().selectedRow.accountPeriodNo === "")
    updateErrorAlert.value = true;
  else confirm("UPDATE");
};

const rules = (v) => {
  return !!v || "필수 항목입니다.";
};
</script>

<template>
  <VDialog width="600">
    <VSnackbar
      v-model="updateErrorAlert"
      :timeout="2000"
      color="error"
      location="center"
    >
      <div align="center" style="color: white">
        <VIcon start icon="tabler-alert-circle-filled" />값을 입력해주세요.
      </div>
    </VSnackbar>

    <VCard>
      <div style="margin-left: 10px; margin-right: 10px">
        <VCardTitle style="margin-top: 5px"> 거래처 수정 </VCardTitle>

        <VCol>
          <VTextField
            v-model="baseStore().selectedRow.accountPeriodNo"
            label="기수번호"
            disabled
          />
        </VCol>
        <VCol>
          <VTextField
            v-model="baseStore().selectedRow.periodStartDate"
            label="기수시작일"
          />
        </VCol>
        <VCol>
          <VTextField
            v-model="baseStore().selectedRow.periodEndDate"
            label="기수종료일"
            :rules="[rules]"
          />
        </VCol>
        <VCol>
          <VTextField
            v-model="baseStore().selectedRow.workplaceCode"
            label="사업장코드"
          />
        </VCol>
      </div>

      <VCardText class="d-flex justify-end">
        <VBtn style="margin-right: 15px" @click="cancelBtn">
          <VIcon start icon="tabler-arrow-back" />취소
        </VBtn>
        <VBtn @click="updateCustomerData">
          <VIcon start icon="tabler-text-plus" />확인
        </VBtn>
      </VCardText>
    </VCard>
  </VDialog>
</template>
