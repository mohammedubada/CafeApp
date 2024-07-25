package com.cafe.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafe.constants.CafeConstants;
import com.cafe.entity.Bill;
import com.cafe.service.BillService;
import com.cafe.utils.CafeUtils;

@RestController
@RequestMapping("/bill")
public class BillController {

	@Autowired
	BillService billService;
	
	@PostMapping("/generateReoprt")
	public ResponseEntity<String> generateReoprt(@RequestBody Map<String, Object> requestMap){
		try {
			return billService.generateReoprt(requestMap);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/getBills")
	public ResponseEntity<List<Bill>> getBills() {  
		try {
			return billService.getBills();
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return null;
	}
	
	@PostMapping("/getPdf")
	public ResponseEntity<byte[]> getPdf(@RequestBody Map<String,Object> requestMap){
		try {
			return billService.getPdf(requestMap);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return null;
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteBill(@PathVariable Integer id){
		try {
			billService.deleteBill(id);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
