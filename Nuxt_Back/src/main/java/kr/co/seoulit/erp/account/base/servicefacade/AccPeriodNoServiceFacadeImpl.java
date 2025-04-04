package kr.co.seoulit.erp.account.base.servicefacade;

import kr.co.seoulit.erp.account.base.applicationservice.AccPeriodNoApplicationService;
import kr.co.seoulit.erp.account.base.to.CustomerBean;
import kr.co.seoulit.erp.account.base.to.PeriodNoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class AccPeriodNoServiceFacadeImpl implements AccPeriodNoServiceFacade {

    @Autowired
    private AccPeriodNoApplicationService accPeriodNoApplicationService;

    @Override
    public List<PeriodNoBean> getPeriodNoList() {
        List<PeriodNoBean> list=  accPeriodNoApplicationService.getPeriodNoList();
        System.out.println(123);
        for (PeriodNoBean periodNoBean : list) {
            System.out.println(periodNoBean);
        }
        return list;
    }

    @Override
    public void addPeriodNoList(HashMap<String, ArrayList<PeriodNoBean>> periodNoList) {
        accPeriodNoApplicationService.addPeriodNoList(periodNoList);
    }

    @Override
    public void updatePeriodNoList(HashMap<String, ArrayList<PeriodNoBean>> periodNoList){
        accPeriodNoApplicationService.updatePeriodNoList(periodNoList);
    }

    @Override
    public void deletePeriodNo(int periodNo){
        accPeriodNoApplicationService.deletePeriodNo(periodNo);

    }


}


