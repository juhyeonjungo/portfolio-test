package kr.co.seoulit.erp.account.statement.to;

import kr.co.seoulit.common.to.BaseTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

//2024-06-01 재작성
@EqualsAndHashCode(callSuper=false)
@Data
public class IncomeStatementBean extends BaseTO{
    private long accountPeriodNo;    //  추가
    private long lev;   //  추가
    private String accountInnerCode;
    private String accountName;
    private long cTotal1;
    private long cTotal2;
    private long pTotal1;
    private long pTotal2;
}