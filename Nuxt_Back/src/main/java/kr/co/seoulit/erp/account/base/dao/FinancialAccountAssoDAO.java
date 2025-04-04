package kr.co.seoulit.erp.account.base.dao;

import kr.co.seoulit.erp.account.base.to.FinancialAccountAssociatesBean;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface FinancialAccountAssoDAO {
	public ArrayList<FinancialAccountAssociatesBean> selectFinancialAccountAssociatesList();
}
