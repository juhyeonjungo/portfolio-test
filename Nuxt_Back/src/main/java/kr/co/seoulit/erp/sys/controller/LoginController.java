package kr.co.seoulit.erp.sys.controller;


import kr.co.seoulit.erp.sys.serviceFacade.BaseServiceFacade;
import kr.co.seoulit.erp.sys.to.LoginEmpBean;
import kr.co.seoulit.erp.sys.to.LoginTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/sys/*")
@CrossOrigin(value = "*", exposedHeaders = { "Authorization" })
public class LoginController {


    @Autowired
    BaseServiceFacade baseServiceFacade;

    @PostMapping("login")
    public Map<String,Object> login(@RequestBody LoginTo loginData){

        System.out.println("logindata"+loginData);
        Map<String, Object> user = new HashMap<>();
        List list = new ArrayList();
        List<LoginEmpBean.AbilityRule> abilityRules = new ArrayList<>();

        LoginEmpBean loginEmpBean=baseServiceFacade.findLogin(loginData);

        LoginEmpBean.AbilityRule abilityRule = loginEmpBean.new AbilityRule("mange","all");
        abilityRules.add(abilityRule);

        loginEmpBean.setUsername(loginEmpBean.getFullName());
        loginEmpBean.setAbilityRules(abilityRules);
        loginEmpBean.setRole("admin");
        loginEmpBean.setId(1);
        loginEmpBean.setAvatar("http://localhost:3000/images/avatars/avatar-1.png");

        list.add(loginEmpBean);





        List<String> userTokens = Arrays.asList(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MX0.fhc3wykrAnRpcKApKhXiahxaOe8PSHatad31NuIZ0Zg",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mn0.cat2xMrZLn0FwicdGtZNzL7ifDTAKWB0k1RurSWjdnw",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6M30.PGOfMaZA_T9W05vMj5FYXG5d47soSPJD1WuxeUfw4L4",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NH0.d_9aq2tpeA9-qpqO0X4AmW6gU2UpWkXwc04UJYFWiZE",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NX0.ocO77FbjOSU1-JQ_BilEZq2G_M8bCiB10KYqtfkv1ss",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Nn0.YgQILRqZy8oefhTZgJJfiEzLmhxQT_Bd2510OvrrwB8",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6N30.KH9RmOWIYv_HONxajg7xBIJXHEUvSdcBygFtS2if8Jk",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6OH0.shrp-oMHkVAkiMkv_aIvSx3k6Jk-X7TrH5UeufChz_g",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6OX0.9JD1MR3ZkwHzhl4mOHH6lGG8hOVNZqDNH6UkFzjCqSE",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTB9.txWLuN4QT5PqTtgHmlOiNerIu5Do51PpYOiZutkyXYg"
        );
        user.put("users", list);
        user.put("userTokens", userTokens);


        System.out.println("users"+user);

        System.out.println(user);



       return  user;
    }






}
