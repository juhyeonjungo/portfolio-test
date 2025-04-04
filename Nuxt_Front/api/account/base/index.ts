import axios from "axios";

const BASE_URL = "http://localhost:8282/acc";

// 계정코드 전체조회
function searchAccountCodeList() {
  return axios.get(`${BASE_URL}/base/accountCodes`);
}

// 계정코드 조건조회
function getAccountCode({ accountInnerCode, accountName }: any) {
  return axios.get(`${BASE_URL}/base/accountCodes/${accountInnerCode}`, {
    params: {
      accountName,
    },
  });
}

// 계정별원장
function getAccountLedger({ accountName, startDate, endDate }: any) {
  return axios.get(`${BASE_URL}/base/accounts/${accountName.value}`, {
    params: {
      startDate: startDate.value,
      endDate: endDate.value,
    },
  });
}

// 총계정원장 2024-05-27 수정
function getGeneralAccountLedger({ accountCode, startDate, endDate }: any) {
  console.log("getGeneralAccountLedger", accountCode.value, startDate.value, endDate.value);
  return axios.get(`${BASE_URL}/base/accounts`, {
    params: {
      accountCode: accountCode.value,
      startDate: startDate.value,
      endDate: endDate.value,
    },
  });
}

// 2024-05-28 추가 : 총계정원장 - 일자별
function getGeneralAccountLedgerDaily({ accountName, startDate, endDate }: any) {
  console.log("일단여기까지옴옴 03-14")
  console.log("getGeneralAccountLedger", accountName.value, startDate.value, endDate.value);
  return axios.get(`${BASE_URL}/base/accountsDaily`, {
    params: {
      accountName: accountName.value,
      startDate: startDate.value,
      endDate: endDate.value,
    },
  });
}

// 일(월)계표
function getDetailTrialBalance({ startDate, endDate }: any) {
  return axios.get(`${BASE_URL}/statement/detailTrialBalances`, {
    params: {
      startDate: startDate.value,
      endDate: endDate.value,
    },
  });
}

//거래처별원장 
// 2025-03-17 정주현
function getSearchCustomerJournal({ startDate, endDate, customerCodes }: any) {
  return axios.get(`${BASE_URL}/base/customerLedger`, {
    params: {
      startDate,
      endDate,
      customerCodes
    },
  });
}


// 현금출납장
function getCashJournal({ startDate, endDate }: any) {
  return axios.get(`${BASE_URL}/statement/cashJournals`, {
    params: {
      startDate: startDate.value,
      endDate: endDate.value,
    },
  });
}

// 거래처 관리 //

// 거래처 조회
function searchCustomerList() {
  return axios.get(`${BASE_URL}/base/customers`);
}

// 거래처 추가
function addCustomerCode(addCustomerInfo: any) {
  return axios.post(`${BASE_URL}/base/addCustomer`, addCustomerInfo);
}

// 거래처 수정
function updateCustomerCode(updateCustomerInfo: any) {
  return axios.put(`${BASE_URL}/base/updateCustomer`, updateCustomerInfo);
}

// 거래처 삭제
function deleteCustomerCode(customerCode: string) {
  return axios.delete(`${BASE_URL}/base/deleteCustomer`, {
    params: {
      customerCode,
    },
  });
}

// 기수 관리  정주현 //

//기수 조회 
function searchPeriodNoList() {
  return axios.get(`${BASE_URL}/base/periodNo`);
}

//기수 추가
function addPeriodNo(addPeriodInfo: any) {
  return axios.post(`${BASE_URL}/base/addPeriodNo`, addPeriodInfo);
}

//기수 수정
function updatePeriodNo(updatePeriodInfo: any) {
  console.log(updatePeriodInfo);
  return axios.put(`${BASE_URL}/base/updatePeriodNo`, updatePeriodInfo);
}
//기수 삭제
function deletePeriodNo(accountPeriodNo: string) {
  return axios.delete(`${BASE_URL}/base/deletePeriodNo`, {
    params: {
      accountPeriodNo,
    },
  });
}

export {
  searchAccountCodeList,
  getAccountCode,
  getAccountLedger,
  getGeneralAccountLedger,
  getGeneralAccountLedgerDaily,
  getDetailTrialBalance,
  getCashJournal,
  searchCustomerList,
  addCustomerCode,
  updateCustomerCode,
  deleteCustomerCode,
  getSearchCustomerJournal,
  searchPeriodNoList,
  addPeriodNo,
  updatePeriodNo,
  deletePeriodNo
};
