package jp.pgw.develop.swallow.sample.target.impl;

import jp.pgw.develop.swallow.sample.service.DBService;
import jp.pgw.develop.swallow.sample.service.Service;
import jp.pgw.develop.swallow.sample.target.TestTarget;

public class TestTargetImpl implements TestTarget {

	Service service;

	DBService dbService;

	@Override
	public void exec() {
		try {
			System.out.println(service.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println(dbService.query());
		dbService.insert();
		System.out.println(dbService.query());
	}

	public void setDbService(DBService dbService) {
		this.dbService = dbService;
	}

	public void setService(Service service) {
		this.service = service;
	}

}
