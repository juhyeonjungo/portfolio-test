package kr.co.seoulit.erp.account.base.servicefacade;

import kr.co.seoulit.erp.account.base.to.CustomerBean;
import kr.co.seoulit.erp.account.base.to.PeriodNoBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface AccPeriodNoServiceFacade {

     List<PeriodNoBean> getPeriodNoList();
    void addPeriodNoList(HashMap<String, ArrayList<PeriodNoBean>> periodNoList);
    void updatePeriodNoList(HashMap<String, ArrayList<PeriodNoBean>> periodNoList);
    void deletePeriodNo(int periodNo);
}
