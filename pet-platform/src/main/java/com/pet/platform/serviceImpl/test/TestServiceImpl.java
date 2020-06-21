package com.pet.platform.serviceImpl.test;

import com.pet.common.service.test.TestService;
import com.pet.platform.dao.test.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

	@Autowired
	private TestDao dao;

	@Override
	public void test() {
		System.out.println(dao.getLocalDate());
	}
}
