package kr.co.seoulit.erp.account.base.servicefacade;
import java.util.ArrayList;

import kr.co.seoulit.erp.account.base.to.CustomerJournalBean;
import kr.co.seoulit.erp.account.slip.to.JournalDoubleBean;

public interface AccCustomerLedgerServiceFacade {


    public ArrayList<CustomerJournalBean> findCustomerJournalList(String fromDate, String toDate , String customerCodes);

}
