package jp.pgw.develop.swallow.s2test.mock.statements.after;

import org.junit.runners.model.Statement;

import jp.pgw.develop.swallow.s2test.mock.runners.S2InternalRunner;

public class S2RunUnbindFields extends Statement {

    protected final Statement statement;

    protected final S2InternalRunner runner;

    public S2RunUnbindFields(final Statement statement, final S2InternalRunner runner) {
        this.statement = statement;
        this.runner = runner;
    }

    @Override
    public void evaluate() throws Throwable {
        try {
            statement.evaluate();
        } finally {
            runner.unbindFields();
        }
    }

}
