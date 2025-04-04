package kr.co.seoulit.erp.account.statement.controller;

import java.util.ArrayList;
import java.util.HashMap;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.seoulit.erp.account.statement.to.IncomeStatementTestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import kr.co.seoulit.erp.account.statement.servicefacade.StatementServiceFacade;

@Tag(name = "손익계산서", description="손익계산서 REST API")
@CrossOrigin("*")
@OpenAPIDefinition
@RestController
@RequestMapping("/acc/statement")
public class IncomeStatementController {

	@Autowired
	private StatementServiceFacade statementServiceFacade;

//================================ 손익계산서 컨드롤러  ====================================

//	@GetMapping("/incomeStatements")
//	public ResponseEntity<HashMap<String, Object>> handleRequestInternal(@RequestParam("searchDate") String toDate) {
//
//		HashMap<String, Object> param = new HashMap<>();
//		try {
//
//			HashMap<String, Object> result = statementServiceFacade.getIncomeStatement(toDate);
//
//			param.put("incomeList",result.get("RESULT"));
//			return ResponseEntity.ok(param);
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//		}
//	}

	@Operation(summary = "손익계산서 결산 조회")
	@GetMapping("/incomeStatements")
	public ArrayList<IncomeStatementTestBean> handleRequestInternal(@RequestParam("accountPeriodNo") @Parameter(description="회계 기수 번호") String accountPeriodNo) {
		ArrayList<IncomeStatementTestBean> incomeList=new ArrayList<>();
		System.out.println("test   :" +accountPeriodNo);
		try {
			incomeList = statementServiceFacade.getIncomeStatement(accountPeriodNo);

		    System.out.println(incomeList.get(0).getTotalAmount());
			System.out.println("test   :" + incomeList.get(0).getAccountInnerCode());
			System.out.println("test   :" + incomeList.get(0).getLev());
			System.out.println("test   :" + incomeList.get(0).getAccountName());
			System.out.println("test   :" + incomeList.get(0).getPeriodNo());
		}catch (Exception e){
//        map.put("errorCode", -1);
//        map.put("errorMsg", e.getMessage());
		}

		return incomeList;
	}


}
