package com.cafe.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafe.entity.Category;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Integer>{

	//List<Category> getAllCategory();
}
