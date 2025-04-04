package kr.co.seoulit.erp.account.slip.dao;

import kr.co.seoulit.erp.account.slip.to.JournalDetailBean;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface NewJournalDetailDAO {

    ArrayList<JournalDetailBean> selectJournalDetailList(String journalNo);

//	public JournalDetailBean selectJournalDetail(String journalDetailNo);

    //  최신 분개상세 번호 조회
    public String getMaxJournalDetailNo();

   public void deleteJournalDetailByJournalNo(String journalNo);

   public void updateJournalDetail(JournalDetailBean journalDetailBean);

   public void insertJournalDetailList(String journalNo);

}
