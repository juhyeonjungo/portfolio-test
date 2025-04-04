package kr.co.seoulit.erp.account.base.to;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class PeriodNoBean {


    private String accountPeriodNo;
    private String periodStartDate;
    private String periodEndDate;
    private String workplaceCode;

}
