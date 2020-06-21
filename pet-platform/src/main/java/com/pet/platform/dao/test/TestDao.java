package com.pet.platform.dao.test;

import com.pet.platform.mapper.test.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestDao {

	@Autowired
	private TestMapper mapper;

	public String getLocalDate(){
		return mapper.getLocalDate();
	}

}
