package com.cafe.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.cafe.entity.User;
import java.util.Optional;
//import org.apache.el.stream.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cafe.JWT.CustomerUserDetailService;
import com.cafe.JWT.JwtFilter;
import com.cafe.JWT.JwtUtils;
import com.cafe.constants.CafeConstants;
import com.cafe.repo.UserRepo;
import com.cafe.utils.CafeUtils;
import com.cafe.utils.EmailUtils;
import com.cafe.wrapper.UserWrapper;
import com.google.common.base.Strings;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepo userRepo;

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	CustomerUserDetailService customerUserDetailService;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Autowired
	EmailUtils emailUtils;
	
	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		log.info("Inside signup {}",requestMap);
		try{
			if(validateSignUpMap(requestMap)) {
				User user = userRepo.findByEmail(requestMap.get("email")); 
				if(Objects.isNull(user)) {
					userRepo.save(getUserFromMap(requestMap));
				return CafeUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
			}else {
				return CafeUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
			}
		}else {
			return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
		}
		}catch (Exception e) {
			e.printStackTrace();		
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private boolean validateSignUpMap(Map<String, String> requestMap) {
		if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email") && requestMap.containsKey("password")) {
			return true;
		}
		return false;
	}
	
	private User getUserFromMap(Map<String, String> requestMap) {
		User user = new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setStatus("false");
		user.setRole("user");
		return user;
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		log.info("Inside login");
		try {
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
			if(auth.isAuthenticated()) {
				if(customerUserDetailService.getUser().getStatus().equalsIgnoreCase("true")) {
					return new ResponseEntity<String>("{\"token\":\"" + jwtUtils.generateToken(customerUserDetailService.getUser().getEmail(),
						customerUserDetailService.getUser().getRole()) + "\"}", HttpStatus.OK);
				}else {
					return new ResponseEntity<String>("{\"message\":\""+"wait for admin approval"+"\"}",HttpStatus.BAD_REQUEST);
				}
			}
		} catch (Exception e) {
			log.error("{}",e);
		}
		return new ResponseEntity<String>("{\"message\":\""+"wrong credentials"+"\"}",HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			if(jwtFilter.isAdmin()) {
				return new ResponseEntity<>(userRepo.getAllUser(),HttpStatus.OK);
			}else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);//line 128 check if it is same
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				Optional<User> userDB = userRepo.findById(Integer.parseInt(requestMap.get("id")));
				if(!userDB.isEmpty()) {
					userRepo.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
					sendMailToAllAdmin(requestMap.get("status"), userDB.get().getEmail(), userRepo.getAllAdmin());
					return CafeUtils.getResponseEntity("User status updated successfully", HttpStatus.OK);
				}else {
					return	CafeUtils.getResponseEntity("User doesnt exist", HttpStatus.OK);			
				}
		 		
			}else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
		allAdmin.remove(jwtFilter.getCurrentUser());
		if(status!= null && status.equalsIgnoreCase("true")) {
			emailUtils.sendMessage(jwtFilter.getCurrentUser(), "account approved", "USER:- " + user + " \n is approved by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
		}else {
			emailUtils.sendMessage(jwtFilter.getCurrentUser(), "account disabled", "USER:- " + user + " \n is disabled by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
		}
	}

	@Override
	public ResponseEntity<String> checkToken() {
		return CafeUtils.getResponseEntity("true", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			User user = userRepo.findByEmail(jwtFilter.getCurrentUser());
			if (!user.equals(null)){
				if (user.getPassword().equals(requestMap.get("oldPassword"))) {
					user.setPassword(requestMap.get("newPassword"));
					userRepo.save(user);	
					return CafeUtils.getResponseEntity("Password upadated successfully", HttpStatus.OK);
				}
				return CafeUtils.getResponseEntity("Incorrect Password", HttpStatus.BAD_REQUEST);
			}
			return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		try {
			User user = userRepo.findByEmail(requestMap.get("email"));
			if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) 
				emailUtils.forgotMail(user.getEmail(), "Credentials by Cafe Management System", user.getPassword());
			return CafeUtils.getResponseEntity("Check your mail for credentials", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();		
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
