package kr.co.seoulit.erp.account.statement.dao;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.seoulit.erp.account.statement.to.IncomeStatementTestBean;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IncomeStatementDAO {
    public ArrayList<IncomeStatementTestBean> callIncomeStatement(String toDate);
    public ArrayList<IncomeStatementTestBean> getIncomeStatementResult();
}
