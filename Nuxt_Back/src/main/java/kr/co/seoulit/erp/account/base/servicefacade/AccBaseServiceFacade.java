package kr.co.seoulit.erp.account.base.servicefacade;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.seoulit.erp.account.base.to.*;

public interface AccBaseServiceFacade {

    public ArrayList<DetailCodeBean> getDetailCodeList(HashMap<String, Object> param);

    public ArrayList<CodeBean> findCodeList();

    public ArrayList<SlipIreportBean> getSlipIreportData(String slipNo);
    
    public void batchCodeProcess(ArrayList<CodeBean> codeList, ArrayList<DetailCodeBean> codeList2);

    //20240522
    public ArrayList<AccountControlBean> getControllerCodeList();

    //20240523
    public ArrayList<FinancialAccountAssociatesBean> getFinancialAccountAssociatesList();
}
