package jp.pgw.develop.swallow.s2test.mock.statements.before;

import org.junit.runners.model.Statement;

import jp.pgw.develop.swallow.s2test.mock.runners.S2InternalRunner;

public class S2RunBindFields extends Statement {

	protected final Statement statement;

	protected final S2InternalRunner runner;

	public S2RunBindFields(final Statement statement, S2InternalRunner runner) {
		this.statement = statement;
		this.runner = runner;
	}

	@Override
	public void evaluate() throws Throwable {
		runner.bindFields();
		statement.evaluate();
	}

}
