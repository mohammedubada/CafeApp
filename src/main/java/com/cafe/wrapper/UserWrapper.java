package com.cafe.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWrapper {

	private Integer id;
	
	private String name;
	
	private String contactNumber;
	
	private String email;

	private String status;
}
