package jp.pgw.develop.swallow.s2test.mock.runners;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.internal.junit.MockitoTestListener;
import org.mockito.internal.runners.InternalRunner;
import org.mockito.internal.util.Supplier;

public class S2Runner implements InternalRunner {

	protected final S2InternalRunner runner;

	public S2Runner(final Class<?> testClass, final Supplier<MockitoTestListener> listenerSupplier)
			throws InitializationError {
		runner = new S2InternalRunner(testClass, listenerSupplier);
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
