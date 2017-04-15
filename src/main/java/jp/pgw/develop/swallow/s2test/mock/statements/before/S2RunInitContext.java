package jp.pgw.develop.swallow.s2test.mock.statements.before;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import jp.pgw.develop.swallow.s2test.database.rules.S2DatabaseTester;
import jp.pgw.develop.swallow.s2test.mock.runners.S2InternalRunner;

public class S2RunInitContext extends Statement {

    protected final Statement statement;

    protected final S2InternalRunner runner;

    protected final FrameworkMethod method;

    public S2RunInitContext(final Statement statement, final S2InternalRunner runner, final FrameworkMethod method) {
        this.statement = statement;
        this.runner = runner;
        this.method = method;
    }

    @Override
    public void evaluate() throws Throwable {
        runner.initContext();
        final S2DatabaseTester tester = runner.getDatabaseTester();
        if (tester == null) {
            statement.evaluate();
        } else {
            tester.apply(statement, runner.getMethodDescription(method)).evaluate();
        }
    }

}
