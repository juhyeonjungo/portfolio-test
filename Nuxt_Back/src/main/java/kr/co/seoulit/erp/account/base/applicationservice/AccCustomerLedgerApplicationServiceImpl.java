package kr.co.seoulit.erp.account.base.applicationservice;

import kr.co.seoulit.erp.account.base.dao.AccCustomerDAO;
import kr.co.seoulit.erp.account.base.to.CustomerJournalBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import kr.co.seoulit.erp.account.base.dao.AccCustomerJournalDAO;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class AccCustomerLedgerApplicationServiceImpl implements AccCustomerLedgerApplicationService{

    @Autowired
    private AccCustomerJournalDAO accCustomerJournalDAO;

    //분개장 조회
    @Override
    public ArrayList<CustomerJournalBean> findCustomerJournalList(String fromDate, String toDate ,String customerCodes) {
        HashMap<String, String> map = new HashMap<>();
        map.put("fromDate", fromDate);
        map.put("toDate", toDate);
        map.put("customerCodes", customerCodes);
        return accCustomerJournalDAO.findCustomerJournalList(map);
    }

}
