package kr.co.seoulit.erp.sys.dao;

import kr.co.seoulit.erp.sys.to.LoginEmpBean;
import kr.co.seoulit.erp.sys.to.LoginTo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginDao {

    public LoginEmpBean selectLogin(LoginTo loginData);



}
