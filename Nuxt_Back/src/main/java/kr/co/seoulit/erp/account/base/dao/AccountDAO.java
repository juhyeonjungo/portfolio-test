package kr.co.seoulit.erp.account.base.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.co.seoulit.erp.account.base.to.*;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountDAO {
    
    public ArrayList<AccountBean> selectDetailAccountList(String code);

    public ArrayList<AccountBean> selectParentAccountList();

    public void updateAccount(AccountBean accountBean);
    
    public String selectPeriodNo(String toDay);

    ArrayList<AccountBean> selectAccountListByName(String accountName);

    ArrayList<AccountControlBean> selectAccountControlList(String accountCode);

    ArrayList<AccountControlCodeBean> selectAccountControlCodeList(String accountInnerCode);

	public List<AccountCodeBean> getAccountList();

	 //========================================코드조회 시작 ================================================
    public List<AccountBean> selectAccount(HashMap<String, Object> param);

    //=====================================  계정별 원장====================================
    public List<LedgerByAccountBean> getLedgerbyAccountInfo(HashMap<String,Object> param); //계정별원장

	public ArrayList<AccountBean> selectDetailBudgetList(String code);

	public ArrayList<AccountBean> selectParentBudgetList();

	public ArrayList<PeriodBean> selectAccountPeriodList();
	public ArrayList<AccountCodeBean> selectCurrentAssetList();
	//=====================================  계정과목관리   ====================================
    public void deleteAccountList(String accountInnerCode); // 계정관리목록 삭제
    public void insertAccountList(AccountBean bean); // 계정관리목록 추가
    public void updateAccountList(AccountBean bean);
    //===================================== 계정과목관리   ====================================


}