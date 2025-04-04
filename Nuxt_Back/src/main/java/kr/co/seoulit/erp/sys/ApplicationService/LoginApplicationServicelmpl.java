package kr.co.seoulit.erp.sys.ApplicationService;

import kr.co.seoulit.erp.sys.dao.LoginDao;
import kr.co.seoulit.erp.sys.to.LoginEmpBean;
import kr.co.seoulit.erp.sys.to.LoginTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
public class LoginApplicationServicelmpl implements LoginApplicationService {


    @Autowired
    LoginDao loginDao;


    @Override
    public LoginEmpBean getLoginEmpBean(LoginTo loginData){

        LoginEmpBean loginEmpBean=loginDao.selectLogin(loginData);

        System.out.println(loginEmpBean);

        return loginEmpBean;

    };




}
