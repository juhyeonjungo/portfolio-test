package kr.co.seoulit.erp.account.slip.repository;

import kr.co.seoulit.erp.account.slip.to.NewJournalDetailBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface NewJournalDetailRepository extends JpaRepository<NewJournalDetailBean,Integer> {
// 2024/05/24 뭐가 뭔지 모르겠는데 안쓸거 같아서 일단 폐기
//    ArrayList<JournalDetailBean> findByJournalNo(String journalNo);
//
//    @Query("DELETE FROM JournalDetailBean journalDetailBean WHERE journalDetailBean.journalNo = :journalNo")
//    void deleteByJournalNo(@Param("journalNo") String journalNo);
}
