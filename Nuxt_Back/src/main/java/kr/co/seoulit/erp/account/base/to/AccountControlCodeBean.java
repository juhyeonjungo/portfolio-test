package kr.co.seoulit.erp.account.base.to;

import kr.co.seoulit.common.to.BaseTO;
import lombok.*;

@EqualsAndHashCode(callSuper=false)
@Data
public class AccountControlCodeBean extends BaseTO{
    private String accountControlCode;
    private String accountControlName;
    private String accountControlType;
    private String accountCode;
}
