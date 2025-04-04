package kr.co.seoulit.erp.account.base.servicefacade;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.seoulit.erp.account.base.applicationservice.AccountApplicationService;
import kr.co.seoulit.erp.account.base.to.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.seoulit.erp.account.base.applicationservice.AccCodeListApplicationService;


@Service
public class AccBaseServiceFacadeImpl implements AccBaseServiceFacade {

	@Autowired
    private AccCodeListApplicationService codeListApplicationService;
    @Autowired
    private AccountApplicationService accountApplicationService;

    //민상

    @Override
    public ArrayList<AccountControlBean> getControllerCodeList() {
        return codeListApplicationService.getControllerCodeList();
    }

    @Override
    public ArrayList<DetailCodeBean> getDetailCodeList(HashMap<String, Object> param) {
        return codeListApplicationService.getDetailCodeList(param);
    }

    @Override
    public ArrayList<CodeBean> findCodeList() {
        return codeListApplicationService.findCodeList();
    }
    @Override
    public void batchCodeProcess(ArrayList<CodeBean> codeList, ArrayList<DetailCodeBean> codeList2) {

            codeListApplicationService.batchCodeProcess(codeList, codeList2);

    }


    @Override
	public ArrayList<SlipIreportBean> getSlipIreportData(String slipNo) {
		// TODO Auto-generated method stub
		return null;
	}

    //20240523 공사중
    @Override
    public ArrayList<FinancialAccountAssociatesBean> getFinancialAccountAssociatesList() {
        return accountApplicationService.getFinancialAccountAssociatesList();
    }
}
