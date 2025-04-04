package kr.co.seoulit.erp.account.base.applicationservice;

import kr.co.seoulit.erp.account.base.to.AccountControlBean;
import kr.co.seoulit.erp.account.base.to.CodeBean;
import kr.co.seoulit.erp.account.base.to.CustomerJournalBean;
import kr.co.seoulit.erp.account.base.to.DetailCodeBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

public interface AccCustomerLedgerApplicationService {

	public ArrayList<CustomerJournalBean> findCustomerJournalList(String fromDate, String toDate , String customerCodes);
}
