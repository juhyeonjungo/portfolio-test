package kr.co.seoulit.erp.account.base.servicefacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.co.seoulit.erp.account.base.to.*;
import kr.co.seoulit.erp.account.slip.dao.JournalDAO;
import kr.co.seoulit.erp.account.slip.to.GeneralAccountLedgerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.seoulit.erp.account.base.applicationservice.AccountApplicationService;

@Service
public class AccountServiceFacadeImpl implements AccountServiceFacade{
 
   @Autowired
    private AccountApplicationService accountApplicationService;
   @Autowired
	private JournalDAO journalDAO;

	//2024-05-27 어플임플 거치치않고 바로 구현. 어플임플은 서서히 없애야 함
	@Override
	public ArrayList<GeneralAccountLedgerBean> findGeneralAccountLedgerList(String accountCode, String fromDate, String toDate) {
			// TODO Auto-generated method stub
		HashMap<String, String> map = new HashMap<>();
		map.put("accountCode", accountCode);
		map.put("fromDate", fromDate);
		map.put("toDate", toDate);
		return journalDAO.selectGeneralAccountLedgerList(map);
	}

	//2024-05-28
	@Override
	public ArrayList<GeneralAccountLedgerBean> findGeneralAccountLedgerListDaily(String accountName, String fromDate, String toDate) {
		// TODO Auto-generated method stub
		HashMap<String, String> map = new HashMap<>();
		map.put("accountName",  accountName );
		map.put("fromDate", fromDate);
		map.put("toDate", toDate);
		return journalDAO.selectGeneralAccountLedgerListDaily(map);
	}

    @Override
    public ArrayList<AccountBean> findParentAccountList() {
           
        return accountApplicationService.findParentAccountList();
    }

    @Override
    public ArrayList<AccountBean> findDetailAccountList(String code) {
       
        return accountApplicationService.findDetailAccountList(code);
    }

    @Override
    public void updateAccount(AccountBean accountBean) {
      
            accountApplicationService.updateAccount(accountBean);         
    }

    public String findPeriodNo(String toDay) {
    	return accountApplicationService.selectPeriodNo(toDay);
    }
    
    @Override
    public ArrayList<AccountBean> getAccountListByName(String accountName) {
         
        return accountApplicationService.getAccountListByName(accountName);
    }

    @Override
    public ArrayList<AccountControlBean> getAccountControlList(String accountCode) {

        return accountApplicationService.getAccountControlList(accountCode);
            
    }

	//2024-05-22
	@Override
	public ArrayList<AccountControlCodeBean> getAccountControlCodeList(String accountInnerCode) {

		return accountApplicationService.getAccountControlCodeList(accountInnerCode);

	}
	@Override
	public List<AccountCodeBean> getAccountList() {
		return accountApplicationService.getAccountList();
	}
	
	 //========================================2020-09-19  코드 조회 시작 ================================================
		@Override
	    public List<AccountBean> getAccount(String accountCode,String accountName) {
	   
	        return accountApplicationService.getAccount(accountCode ,accountName);
	    }
		//========================================2020-09-19  코드 조회 끝 ================================================
		
		
    //=====================================  2020-08-25 계정별 원장    시작 ====================================
	@Override
	public List<LedgerByAccountBean> getLedgerbyAccountInfo(String accountName, String startDate, String endDate) {
		
		return accountApplicationService.getLedgerbyAccountInfo(accountName, startDate,endDate);
	}
	//=====================================  2020-08-25 계정별 원장    끝 =======================================
	
    //=====================================  2020-09-01 거래처관리      시작 ======================================
	@Override
	public List<CustomerBean> getCustomerList(){
		return accountApplicationService.getCustomerList();
	}
	@Override
	public void deleteNormalCustomer(String customerCode ) {
		 accountApplicationService.deleteNormalCustomer(customerCode);
	}
	@Override
	public void addCustomer(HashMap<String, ArrayList<CustomerBean>> customerList) {
		accountApplicationService.addCustomer(customerList);
	}
	@Override
	public void updateCustomer(HashMap<String, ArrayList<CustomerBean>> customerList) {
		accountApplicationService.updateCustomer(customerList);
	}
//	@Override
//	public void batchCustomerProcess(HashMap<String, ArrayList<CustomerBean>> customerList) {
//		accountApplicationService.batchCustomerProcess(customerList);
//	}
	//=====================================  2020-09-01 거래처관리     끝 ========================================

	@Override
	public ArrayList<AccountBean> findDetailBudgetList(String code) {
		// TODO Auto-generated method stub
		return accountApplicationService.findDetailBudgetList(code);
	}

	@Override
	public ArrayList<AccountBean> findParentBudgetList() {
		// TODO Auto-generated method stub
		return accountApplicationService.findParentBudgetList();
	}

	@Override
	public ArrayList<PeriodBean> findAccountPeriodList() {
		// TODO Auto-generated method stub
		return accountApplicationService.findAccountPeriodList();
	}
	
	//=====================================  2020-11-25 계정과목관리  유길현  시작  ======================================
	// 계정과목관리 삭제
	@Override
	public void deleteAccountList(String accountInnerCode) {
		accountApplicationService.deleteAccountList(accountInnerCode);
	}
		
	// 계정과목관리 저장
	@Override
	public void batchAccountList(HashMap<String, ArrayList<AccountBean>> accountList) {
		accountApplicationService.batchAccountList(accountList);
	}

	//계정과목가져오기
	@Override
	public List<Account> getAccountCodeList() {
		return accountApplicationService.getAccountCodeList();
	}

	@Override
	public void registerAccountCode(AccountBean accountBean) {
		accountApplicationService.registerAccountCode(accountBean);
	}
	//=====================================  2020-11-25 계정과목관리  유길현  끝  ======================================
}