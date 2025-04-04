package kr.co.seoulit.erp.account.base.applicationservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.co.seoulit.erp.account.base.to.*;

public interface AccountApplicationService {

//	//총계정원장
//   ArrayList<GeneralAccountLedgerBean> findGeneralAccountLedgerList(String fromDate, String toDate);


	public ArrayList<AccountBean> findParentAccountList();

	public ArrayList<AccountBean> findDetailAccountList(String code);

	public void updateAccount(AccountBean accountBean);

	ArrayList<AccountBean> getAccountListByName(String accountName);

	ArrayList<AccountControlBean> getAccountControlList(String accountCode);

	//2024-05-22
	public ArrayList<AccountControlCodeBean> getAccountControlCodeList(String accountInnerCode);

	//2024-05-23
	public ArrayList<FinancialAccountAssociatesBean> getFinancialAccountAssociatesList();

	public String selectPeriodNo(String toDay);

	public List<AccountCodeBean> getAccountList();

	public List<AccountBean> getAccount(String accountCode, String accountName);

	public List<LedgerByAccountBean> getLedgerbyAccountInfo(String accountCode, String startDate, String endDate);

	public List<CustomerBean> getCustomerList();

	public void deleteNormalCustomer(String customerCode);


	public void addCustomer(HashMap<String, ArrayList<CustomerBean>> customerList);
	public void updateCustomer(HashMap<String, ArrayList<CustomerBean>> customerList);
//	public void batchCustomerProcess(HashMap<String, ArrayList<CustomerBean>> customerList);

	public ArrayList<AccountBean> findDetailBudgetList(String code);

	public ArrayList<AccountBean> findParentBudgetList();

	public ArrayList<PeriodBean> findAccountPeriodList();

	// ===================================== 2020-11-25 계정과목관리 유길현 시작
	// ====================================
	public void deleteAccountList(String accountInnerCode); // 계정과목관리 삭제

	public void batchAccountList(HashMap<String, ArrayList<AccountBean>> accountList); // 계정과목관리 저장
	// ===================================== 2020-11-25 계정과목관리 유길현 끝
	// ====================================

	public Account selectAccountName(String accountInnerCode);

	List<Account> getAccountCodeList();

	void registerAccountCode(AccountBean accountBean);
}
