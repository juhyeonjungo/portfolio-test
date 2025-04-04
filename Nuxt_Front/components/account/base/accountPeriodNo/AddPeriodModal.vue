<script setup lang="ts">
import { baseStore } from "@/store/account/base";

const emits = defineEmits(["confirm", "cancelBtn"]);
const accountPeriodNo = ref("");
const periodStartDate = ref("");
const periodEndDate = ref("");
const workplaceCode = ref("");
//
const props = defineProps<{
  maxPeriodNo:number
}>();
const isFormValid = computed(() => {
  return (
    periodStartDate.value.trim() !== "" &&
    periodEndDate.value.trim() !== "" &&
    workplaceCode.value.trim() !== ""
  );
});
//
const addErrorAlert = ref(false);
const confirm = (ADD) => {
  emits("confirm", ADD);
};

const cancelBtn = () => {
  emits("cancelBtn");
};

const addCustomerData = async () => {
  const addPeriodInfo = {
    periodNoList: [
      {
        accountPeriodNo: accountPeriodNo.value,
        periodStartDate: periodStartDate.value,
        periodEndDate: periodEndDate.value,
        workplaceCode: workplaceCode.value,
      },
    ],
  };

  await baseStore().ADD_PERIOD_NO(addPeriodInfo);

confirm("ADD");
};

const rules = (v) => {
  return !!v || "필수 항목입니다.";
};
</script>

<template>
  <VDialog width="600">
    <VSnackbar
      v-model="addErrorAlert"
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
        <VCardTitle style="margin-top: 5px"> 거래처 추가 </VCardTitle>

        <VCol>
          <VTextField
            v-model="accountPeriodNo"
            :label="`${props.maxPeriodNo+1}`"
            :rules="[rules]"
            disabled
          />
        </VCol>
        <VCol>
          <VTextField
            v-model="periodStartDate"
            label="기수시작일"
            placeholder="기수시작일"
          />
        </VCol>
        <VCol>
          <VTextField
            v-model="periodEndDate"
            label="기수종료일"
            placeholder="기수종료일"
          />
        </VCol>
        <VCol>
          <VTextField
            v-model="workplaceCode"
            label="사업장코드"
            placeholder="사업장코드"
            :rules="[rules]"
          />
        </VCol>
      </div>

      <VCardText class="d-flex justify-end">
        <VBtn style="margin-right: 15px" @click="cancelBtn">
          <VIcon start icon="tabler-arrow-back" />취소
        </VBtn>
<VBtn @click="addCustomerData" :disabled="!isFormValid">
  <VIcon start icon="tabler-text-plus" />확인
</VBtn>
      </VCardText>
    </VCard>
  </VDialog>
</template>
