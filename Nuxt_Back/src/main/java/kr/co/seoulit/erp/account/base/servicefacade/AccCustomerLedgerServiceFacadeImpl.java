package kr.co.seoulit.erp.account.base.servicefacade;



import kr.co.seoulit.erp.account.base.applicationservice.AccCustomerLedgerApplicationService;
import kr.co.seoulit.erp.account.base.applicationservice.AccountApplicationService;
import kr.co.seoulit.erp.account.base.to.CustomerJournalBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AccCustomerLedgerServiceFacadeImpl implements AccCustomerLedgerServiceFacade {

    @Autowired
    private AccCustomerLedgerApplicationService accCustomerLedgerApplicationService;


    public ArrayList<CustomerJournalBean> findCustomerJournalList(String fromDate, String toDate, String customerCodes){


        return  accCustomerLedgerApplicationService.findCustomerJournalList(fromDate,toDate,customerCodes);
    }


}
//import kr.co.seoulit.erp.account.slip.to.JournalDoubleBean;
//
//public interface AccCustomerLedgerServiceFacade {
//
//
//    public ArrayList<JournalDoubleBean> findCoustomerJounalList(String fromDate, String toDate);
//
//}
