package kr.co.seoulit.erp.sys.dao;

import java.util.List;

import kr.co.seoulit.erp.sys.to.LoginEmpBean;
import org.apache.ibatis.annotations.Mapper;

import kr.co.seoulit.erp.sys.to.AuthorityTo;

@Mapper
public interface AuthorityDao {

	public List<AuthorityTo> selectAuthorityList();


	public List<AuthorityTo> selectMenuAuthorityList(String authorityCode);
}

