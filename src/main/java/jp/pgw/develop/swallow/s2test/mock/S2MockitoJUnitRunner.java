package jp.pgw.develop.swallow.s2test.mock;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.mockito.internal.runners.InternalRunner;
import org.mockito.internal.runners.StrictRunner;

import jp.pgw.develop.swallow.s2test.mock.runners.S2RunnerFactory;

public class S2MockitoJUnitRunner extends Runner implements Filterable {

    public static class Silent extends S2MockitoJUnitRunner {
        public Silent(final Class<?> klass) throws Exception {
            super(new S2RunnerFactory().create(klass), klass);
        }
    }

    public static class Strict extends S2MockitoJUnitRunner {
        public Strict(final Class<?> klass) throws Exception {
            super(new StrictRunner(new S2RunnerFactory().createStrict(klass), klass), klass);
        }
    }

    public static class StrictStubs extends S2MockitoJUnitRunner {
        public StrictStubs(final Class<?> klass) throws Exception {
            super(new StrictRunner(new S2RunnerFactory().createStrictStubs(klass), klass), klass);
        }
    }

    private final InternalRunner runner;

    public S2MockitoJUnitRunner(final Class<?> klass) throws Exception {
        this(new StrictRunner(new S2RunnerFactory().createStrict(klass), klass), klass);
    }

    S2MockitoJUnitRunner(final InternalRunner runner, final Class<?> klass) throws Exception {
        this.runner = runner;
    }

    @Override
    public void run(final RunNotifier notifier) {
        runner.run(notifier);
    }

    @Override
    public Description getDescription() {
        return runner.getDescription();
    }

    @Override
    public void filter(final Filter filter) throws NoTestsRemainException {
        runner.filter(filter);
    }

}
