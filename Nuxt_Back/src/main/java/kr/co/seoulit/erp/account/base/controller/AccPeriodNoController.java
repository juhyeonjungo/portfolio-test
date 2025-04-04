package kr.co.seoulit.erp.account.base.controller;

import kr.co.seoulit.erp.account.base.servicefacade.AccPeriodNoServiceFacade;
import kr.co.seoulit.erp.account.base.servicefacade.AccountServiceFacade;
import kr.co.seoulit.erp.account.base.to.CustomerBean;
import kr.co.seoulit.erp.account.base.to.PeriodNoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;


@CrossOrigin("*")
@RestController
@RequestMapping("/acc/base")
public class AccPeriodNoController {


    //====================================== 2023-03-21  기수 관리  컨트롤러 생성 =====================================

    @Autowired
    private AccPeriodNoServiceFacade accPeriodNoServiceFacade;


    //기수 조회
    @GetMapping(value = "/periodNo")
    public HashMap<String, Object> getCustomerList() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("accountPeriodNoList", accPeriodNoServiceFacade.getPeriodNoList());
        return map;
    }


    //기수 추가
    @RequestMapping(value = "/addPeriodNo", method = RequestMethod.POST)
    public void addCustomer(@RequestBody HashMap<String, ArrayList<PeriodNoBean>> periodNoList) {

        accPeriodNoServiceFacade.addPeriodNoList(periodNoList);
    }

    //기수  수정
    @RequestMapping(value="/updatePeriodNo", method = RequestMethod.PUT)
    public void updateCustomer(@RequestBody HashMap<String,ArrayList<PeriodNoBean>> periodNoList) {
        accPeriodNoServiceFacade.updatePeriodNoList(periodNoList);
    }

    //기수 삭제
    @RequestMapping(value="/deletePeriodNo", method = RequestMethod.DELETE)
    public void deleteNormalCustomer(@RequestParam int accountPeriodNo) {
        System.out.println("정주현"+accountPeriodNo);
        accPeriodNoServiceFacade.deletePeriodNo(accountPeriodNo);   //  { accountPeriodNo : 6    }
    }



}

