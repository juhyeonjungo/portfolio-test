package kr.co.seoulit.erp.account.slip.servicefacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.seoulit.erp.account.funds.repository.NoteRepository;
import kr.co.seoulit.erp.account.funds.to.NoteBean;
import kr.co.seoulit.erp.account.slip.dao.JournalDAO;
import kr.co.seoulit.erp.account.slip.dao.JournalDetailDAO;
import kr.co.seoulit.erp.account.slip.dao.NewJournalDetailDAO;
import kr.co.seoulit.erp.account.slip.dao.SlipDAO;
import kr.co.seoulit.erp.account.slip.repository.JournalDetailRepository;
import kr.co.seoulit.erp.account.slip.repository.NewJournalDetailRepository;
import kr.co.seoulit.erp.account.slip.repository.ReceiptReposiotry;
import kr.co.seoulit.erp.account.slip.repository.SlipReposiotry;
import kr.co.seoulit.erp.account.slip.to.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.seoulit.erp.account.slip.applicationservice.SlipApplicationService;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
@Service
public class SlipServiceFacadeImpl implements SlipServiceFacade {

	private final SlipDAO slipDAO;
	private final SlipReposiotry slipReposiotry;
	private final JournalDAO journalDAO;
	private final NoteRepository noterepository;
	private final NewJournalDetailDAO newJournalDetailDAO;
	private final NewJournalDetailRepository newJournalDetailRepository;
	private final ReceiptReposiotry receiptReposiotry;

	@Autowired
	private SlipApplicationService slipApplicationService;
    @Autowired
    private JournalDetailRepository journalDetailRepository;
    @Autowired
    private JournalDetailDAO journalDetailDAO;

	//전표 전체조회
	@Override
	public ArrayList<SlipBean> findSlipList() {
		return slipApplicationService.findSlipList();
	}

	//승인 / 반려 전표 조회
	@Override
	public ArrayList<SlipBean> findStatusSlipList(String status) {
		return slipApplicationService.findStatusSlipList(status);
	}

	//미결 전표 조회
	@Override
	public ArrayList<SlipBean> findUnProSlipList() {
		return slipApplicationService.findUnProSlipList();
	}

	//전표 한개조회
	@Override
	public SlipBean findSlipBySlipNo(String slipNo) {
		return slipApplicationService.findSlipBySlipNo(slipNo);
	}

	//전표 한개등록
	@Override
	public String registerSlip(SlipBean slipBean) {
		System.out.println("여기로 왔네?;;  2025 - 03 - 12 그리웟다 >> SLIPBEN"+slipBean);

		StringBuffer slipNo = new StringBuffer();
		String slipNoDate = slipBean.getReportingDate().replace("-", "");
		slipNo.append(slipNoDate);
		slipNo.append("SLIP"); // 20231116SLIP

		ArrayList<JournalBean> journalList = slipBean.getJournalList();

		// 이렇게 하면 앞 전표를 삭제했을시 계속 덮어쓰게 됨
//		String code = "0000" + (slipDAO.selectSlipCount(slipNoDate) + 1) + ""
		//여기서 전표번호랑 분개번호 만드는곳 결국 앞단에서 오늘 날짜받아와서 그걸 기준으로 만듬

		String lastNo = slipDAO.selectSlipMaxNo(slipNoDate);
		if (lastNo == null) {
			lastNo = "00000";
		}
		int length = lastNo.length();

		String code = "0000" + (Integer.parseInt(lastNo.substring(length - 5)) + 1) + "";
		slipNo.append(code.substring(code.length() - 5)); // 20231116SLIP00001
		slipBean.setSlipNo(slipNo.toString());

		String year = slipBean.getReportingDate().substring(0, 4);
		String periodNo = (Integer.parseInt(year) - 2019) + "";
		slipBean.setAccountPeriodNo(periodNo);

		slipReposiotry.save(slipBean);


		for (JournalBean journalBean : journalList) {
			String SlipNo = slipBean.getSlipNo();
			String sampleJournalNo = journalBean.getJournalNo();
			String journalNo = journalDAO.selectJournalName(SlipNo);
			journalBean.setSlipNo(SlipNo);
			journalBean.setJournalNo(journalNo);
			journalBean.setAccountInnerCode(journalBean.getAccountCode());
			journalDAO.insertJournal(journalBean);

			for (JournalDetailBean journalDetailBean : journalBean.getJournalDetailList()) {
				String newJournalDetailNo = journalDetailDAO.getMaxJournalDetailNo();
				journalDetailBean.setJournalDetailNo(newJournalDetailNo);
				journalDetailBean.setJournalNo(journalBean.getJournalNo());
				journalDetailRepository.save(journalDetailBean);

//				ArrayList<NoteBean> noteList = slipBean.getNoteDetail();
//			// 계정과목에 어음이 존재시
//			if (noteList.size() != 0) {
//				for (NoteBean noteBean : noteList) {
//					if (noteBean.getJournalNo().equals(sampleJournalNo)) {
//						System.out.println("저장합니다");
//						noteBean.setJournalNo(journalNo);
//						noteBean.setNoteType(noteBean.getNoteType().substring(noteBean.getNoteType().length() - 2, noteBean.getNoteType().length())); // 뒤에서 2자리
//						noterepository.save(noteBean);
//						break;
//					}
//				}
//			}


			}
		}

//			for(NewJournalDetailBean newJournalDetailBean : journalBean.getNewJournalDetailList()) {
//				String newJournalDetailNo = newJournalDetailDAO.getMaxJournalDetailNo();
//				newJournalDetailBean.setJournalDetailNo(newJournalDetailNo);
//				newJournalDetailBean.setJournalNo(journalBean.getJournalNo());
//				newJournalDetailRepository.save(newJournalDetailBean);
//			}
//		}

			// 지출증빙 테이블과 연동
			ReceiptBean receiptBean = new ReceiptBean();

			receiptBean.setSlipNo(slipBean.getSlipNo());
			receiptBean.setAccountPeriodNo(slipBean.getAccountPeriodNo());
			receiptBean.setReportingDate(slipBean.getReportingDate());
			receiptBean.setExpenseReport(slipBean.getExpenseReport());

			receiptReposiotry.save(receiptBean);

			return slipNo.toString();
		}



	//전표 리스트등록
	@Override
	public String registerSlipList(ArrayList<SlipBean> slipList) {
		return slipApplicationService.registerSlipList(slipList);
	}

	//전표 리스트삭제
	@Override
	public void deleteSlip(ArrayList<SlipBean> slipList) {
		slipApplicationService.deleteSlip(slipList);
	}

	//잔표 리스트승인
	@Override
	public void approveSlip(ArrayList<SlipBean> slipList) {
		slipApplicationService.approveSlip(slipList);
	}

	//전표수정
	@Override
	public void updateSlip(SlipBean slipBean) {
		slipApplicationService.updateSlip(slipBean);
	}

	//분개 전체조회
	@Override
	public ArrayList<JournalBean> findJournalListBySlipNo(String slipNo) {
		return slipApplicationService.findJournalListBySlipNo(slipNo);
	}

////////////////////////////////////////////////////////////////////////

	//지출증빙 전체조회
	@Override
	public ArrayList<ReceiptBean> findReceiptList() {
		return slipApplicationService.findReceiptList();
	}

	//지출증빙 한개조회
	@Override
	public ReceiptBean findReceiptBySlipNo(String slipNo) {
		return slipApplicationService.findReceiptBySlipNo(slipNo);
	}

	//지출증빙 증빙등록
	@Override
	public void registerReceipt(String slipNo, String type, MultipartFile file) {
		slipApplicationService.registerReceipt(slipNo, type, file);
	}

	//지출증빙 증빙삭제
	@Override
	public void deleteReceiptFile(String slipNo) {
		slipApplicationService.deleteReceiptFile(slipNo);
	}

	//지출증빙 증빙완료
	@Override
	public void proofReceipt(ArrayList<ReceiptBean> receiptList) {
		slipApplicationService.proofReceipt(receiptList);
	}



////////////////////////////////////////////////////////////////////////


	@Override
	public String addSlip(Map<String, SlipBean> batchArray) {
		return slipApplicationService.addSlip(batchArray);
	}




	@Override
	public ArrayList<SlipBean> findSlipDataList(String slipDate) {
		return slipApplicationService.findSlipDataList(slipDate);
	}

	@Override
	public ArrayList<SlipBean> findRangedSlipList(HashMap<String, Object> map) {
		return slipApplicationService.findRangedSlipList(map);
	}



	@Override
	public String hrAddSlip(Map<String, ArrayList<SlipBean>> batchArray) {
		return slipApplicationService.hrAddSlip(batchArray);
	}

	//분개


	@Override
	public ArrayList<JournalBean> findRangedJournalList(String fromDate, String toDate) {

		return slipApplicationService.findRangedJournalList(fromDate, toDate);
	}

	@Override
	public ArrayList<JournalBean> findSingleJournalList(String slipNo) {
		return slipApplicationService.findSingleJournalList(slipNo);
	}

	//  분개삭제 생성 2020-09-07
	@Override
	public void deleteJournalRow(String slipNo, String journalNo) {
		slipApplicationService.deleteJournalRow(journalNo);

	}

	//  분개 update 2020-09-12
	@Override
	public void updateJournalList(HashMap<String, ArrayList<JournalBean>> journalList) {

		slipApplicationService.updateJournalList(journalList);
	}



	@Override
	public ArrayList<JournalDoubleBean> findJournalDoubleList(String fromDate, String toDate) {
		// TODO Auto-generated method stub
		return slipApplicationService.findJournalDoubleList(fromDate, toDate);
	}

	//분개상세


	@Override
	public void addJournalDetailList(String journalNo) {

		slipApplicationService.addJournalDetailList(journalNo);

	}

	@Override
	public void editJournalDetail(JournalDetailBean journalDetailBean) {

		slipApplicationService.editJournalDetail(journalDetailBean);

	}



	@Override
	public ArrayList<JournalDetailBean> getJournalDetailList(String journalNo) {
		System.out.println("나오나?" + journalNo);
		return slipApplicationService.getJournalDetailList(journalNo);
	}

}
