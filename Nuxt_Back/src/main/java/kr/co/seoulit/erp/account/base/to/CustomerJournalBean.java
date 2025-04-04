package kr.co.seoulit.erp.account.base.to;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class CustomerJournalBean {
	private String slipNo;
	private String approvalDate;
	private String accountPeriodNo;
	private String accountName;
	private String balanceDivision;
	private String customerCode;
	private String leftDebtorPrice;
	private String rightCreditsPrice;
	private String expenseReport;
}

