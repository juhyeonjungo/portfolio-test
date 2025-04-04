package kr.co.seoulit.erp.account.slip.to;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.seoulit.common.to.BaseTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper=false)
@Data
@Entity
@RequiredArgsConstructor
@Table(name="NEW_JOURNAL_DETAIL")
public class NewJournalDetailBean extends BaseTO{
    @Id
    private String journalDetailNo;
    private String journalNo;
    private String accountControlCode;
    private String description;
}
