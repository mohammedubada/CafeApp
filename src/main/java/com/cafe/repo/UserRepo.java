package com.cafe.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cafe.entity.User;
import com.cafe.wrapper.UserWrapper;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer>{

	User findByEmail(@Param("email") String email);
	
	List<UserWrapper> getAllUser();

	//below 2 @ is necessary to update anything
	@Transactional
	@Modifying
	Integer updateStatus(@Param("status") String status,@Param("id") Integer id);

	List<String> getAllAdmin();
	

}
