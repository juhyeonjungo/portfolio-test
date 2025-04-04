package kr.co.seoulit.erp.account.base.to;

import lombok.Data;
import lombok.EqualsAndHashCode;
 
import kr.co.seoulit.common.to.BaseTO;


@EqualsAndHashCode(callSuper=false)
@Data
//=====================================  2020-08-25 계정별 원장    ====================================
//2025 03 14 수정
public class LedgerByAccountBean extends BaseTO{

	private String approvalDate;       // 등록일자
	private String expenseReport;      // 적요
	private String customerCode;       // 거래처명
	private String customerName;       // 거래처코드
	private String leftDebtorPrice;    // 차변금액
	private String rightCreditsPrice;  // 대변금액
	private String balance;  // 잔액

}
