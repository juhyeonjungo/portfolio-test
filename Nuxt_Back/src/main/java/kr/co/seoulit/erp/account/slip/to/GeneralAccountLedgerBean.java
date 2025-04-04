package kr.co.seoulit.erp.account.slip.to;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class GeneralAccountLedgerBean {

    private String approvalDate;
    private String accountName;
    private String leftDebtorPrice;
    private String rightCreditsPrice;
    private String expenseReport;
    private String  balance;


}
