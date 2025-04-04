package kr.co.seoulit.erp.account.base.applicationservice;

import kr.co.seoulit.erp.account.base.dao.AccPeriodNoDAO;
import kr.co.seoulit.erp.account.base.to.PeriodNoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AccPeriodNoApplicationServiceImpl implements AccPeriodNoApplicationService {
    @Autowired
    private AccPeriodNoDAO accPeriodNoDAO;

    @Override
    public List<PeriodNoBean> getPeriodNoList(){
        return accPeriodNoDAO.selectPeriodNoList();
    }
    
    @Override
 public void addPeriodNoList(HashMap<String, ArrayList<PeriodNoBean>> periodNoList){
        ArrayList<PeriodNoBean> list = periodNoList.get("periodNoList");

        if (list != null && !list.isEmpty()) {
            PeriodNoBean bean = list.get(0);  // 첫 번째 객체 꺼냄

            // DAO에 직접 넘김
            accPeriodNoDAO.addPeriodNoList(bean);
        }
    }
  @Override
    public void updatePeriodNoList(HashMap<String, ArrayList<PeriodNoBean>> periodNoList){
      ArrayList<PeriodNoBean> list = periodNoList.get("periodNoList");
      System.out.println("정주현");
      if (list != null && !list.isEmpty()) {
          PeriodNoBean bean = list.get(0);  // 첫 번째 객체 꺼냄
          System.out.println("정주현     "+bean.getAccountPeriodNo());
          System.out.println("정주현     "+bean.getPeriodEndDate());
          System.out.println("정주현     "+bean.getPeriodStartDate());
          System.out.println("정주현     "+bean.getWorkplaceCode());


          // DAO에 직접 넘김
          accPeriodNoDAO.updatePeriodNoList(bean);
      }

  }
  @Override
    public void deletePeriodNo(int periodNo){
      accPeriodNoDAO.deletePeriodNo(periodNo);
  }


}
