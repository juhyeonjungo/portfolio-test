package kr.co.seoulit.erp.account.base.dao;


import kr.co.seoulit.erp.account.base.to.PeriodNoBean;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface AccPeriodNoDAO {

    List<PeriodNoBean> selectPeriodNoList();

    void addPeriodNoList(PeriodNoBean bean);
    void updatePeriodNoList(PeriodNoBean bean);
    void deletePeriodNo(int bean);
}
