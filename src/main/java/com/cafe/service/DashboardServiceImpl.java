package com.cafe.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cafe.repo.BillRepo;
import com.cafe.repo.CategoryRepo;
import com.cafe.repo.ProductRepo;

@Service
public class DashboardServiceImpl implements DashboardService{

	@Autowired
	CategoryRepo categoryRepo;
	
	@Autowired
	ProductRepo productRepo;
	
	@Autowired
	BillRepo billRepo;
	
	@Override
	public ResponseEntity<Map<String, Object>> getCount() {
		Map<String, Object> map = new HashMap<>();
		map.put("category", categoryRepo.count());
		map.put("product", productRepo.count());
		map.put("bill", billRepo.count());
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

}
