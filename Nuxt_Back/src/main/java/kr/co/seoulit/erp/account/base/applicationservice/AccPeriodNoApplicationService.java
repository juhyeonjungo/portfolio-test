package kr.co.seoulit.erp.account.base.applicationservice;

import kr.co.seoulit.erp.account.base.to.PeriodNoBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface AccPeriodNoApplicationService {
     List<PeriodNoBean> getPeriodNoList();
    void addPeriodNoList(HashMap<String, ArrayList<PeriodNoBean>> periodNoList);
    void updatePeriodNoList(HashMap<String, ArrayList<PeriodNoBean>> periodNoList);
    void deletePeriodNo(int periodNo);

}
