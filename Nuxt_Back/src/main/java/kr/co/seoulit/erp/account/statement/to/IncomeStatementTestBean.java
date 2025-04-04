package kr.co.seoulit.erp.account.statement.to;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class IncomeStatementTestBean {
    private String accountInnerCode;    //  추가
    private String accountName;   //  추가
    private long totalAmount;
    private String periodNo;
    private long lev;

}
