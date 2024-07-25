package com.cafe.JWT;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cafe.repo.UserRepo;
import com.google.common.base.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerUserDetailService implements UserDetailsService{
	
	@Autowired
	private UserRepo userRepo;
	
	private com.cafe.entity.User user;
	

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.info("Inside loadUserByUsername", email);
		 user = userRepo.findByEmail(email);
		if(user == null)
			throw new UsernameNotFoundException("User not found with that email");
		
		 return new User(user.getEmail(),user.getPassword(),new ArrayList<>());
	}
	
	public com.cafe.entity.User getUser(){
		return user;
	}

}
