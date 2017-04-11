package jp.pgw.develop.swallow.s2test.mock.statements.post;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import jp.pgw.develop.swallow.s2test.mock.runners.S2InternalRunner;

public class S2RunPostProcess extends Statement {

	protected final Statement statement;

	protected final S2InternalRunner runner;

	protected final FrameworkMethod method;

	public S2RunPostProcess(final Statement statement, S2InternalRunner runner, FrameworkMethod method) {
		this.statement = statement;
		this.runner = runner;
		this.method = method;
	}

	@Override
	public void evaluate() throws Throwable {
		runner.initContainer(method);
		try {
			statement.evaluate();
		} finally {
			runner.dispose();
		}
	}

}
