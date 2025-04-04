package kr.co.seoulit.erp.sys.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class LoginEmpBean {
     private int id;
     private String fullName;   // ✅ fullName 추가
     private String username;   // ✅ username 추가
     private String avatar;     // ✅ avatar 추가
     private String email;
     private String role;       // ✅ role 추가
     private String password;   // ✅ password 추가 (보안상 해싱 필요)

     private List<LoginEmpBean.AbilityRule> abilityRules; // ✅ abilityRules 추가

     @Data
     @AllArgsConstructor
     public class AbilityRule {
          private String action;
          private String subject;
     }

}

