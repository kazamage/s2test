package jp.pgw.develop.swallow.sample;

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
