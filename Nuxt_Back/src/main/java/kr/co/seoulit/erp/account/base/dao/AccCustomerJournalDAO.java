package kr.co.seoulit.erp.account.base.dao;

import kr.co.seoulit.erp.account.base.to.CustomerJournalBean;
import org.apache.ibatis.annotations.Mapper;


import java.util.ArrayList;
import java.util.HashMap;
@Mapper
public interface AccCustomerJournalDAO {

    public ArrayList<CustomerJournalBean> findCustomerJournalList(HashMap<String, String> map);
}
