package kr.co.seoulit.erp.account.base.controller;

import java.util.ArrayList;

import kr.co.seoulit.erp.account.base.servicefacade.AccCustomerLedgerServiceFacade;
import kr.co.seoulit.erp.account.base.to.CustomerJournalBean;
import kr.co.seoulit.erp.account.slip.servicefacade.SlipServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kr.co.seoulit.erp.account.slip.to.JournalDoubleBean;

@CrossOrigin("*")
@RestController
@RequestMapping("/acc/base")
public class AccCustomerLedgerController {

    @Autowired
    private AccCustomerLedgerServiceFacade accCustomerLedgerServiceFacade;


    // 분개장 조회 (승인된 전표만)
    @GetMapping( "/customerLedger")
    public ResponseEntity<ArrayList<CustomerJournalBean>> findCustomerJournalList(
            @RequestParam("startDate") String fromDate,
            @RequestParam("endDate") String toDate,
            @RequestParam("customerCodes") String customerCodes
            ) {

        ArrayList<CustomerJournalBean> customerJournalList = accCustomerLedgerServiceFacade.findCustomerJournalList(fromDate, toDate,customerCodes);
        if(customerJournalList.size()==1){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(customerJournalList);

    }


//{customerJournalList:[CustomerJournalBean,CustomerJournalBean,CustomerJournalBean,CustomerJournalBean,CustomerJournalBean]}



}
