package jp.pgw.develop.swallow.s2test.mock.runners;

import java.lang.reflect.InvocationTargetException;

import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.junit.MockitoTestListener;
import org.mockito.internal.runners.InternalRunner;
import org.mockito.internal.runners.RunnerFactory;
import org.mockito.internal.util.Supplier;

public class S2RunnerFactory extends RunnerFactory {

    @Override
    public InternalRunner create(final Class<?> testClass, final Supplier<MockitoTestListener> listenerSupplier)
            throws InvocationTargetException {
        try {
            return new S2Runner(testClass, listenerSupplier);
        } catch (final Throwable t) {
            throw new MockitoException(
                    "\n" + "\n" + "MockitoRunner can only be used with JUnit 4.5 or higher.\n"
                            + "You can upgrade your JUnit version or write your own Runner (please consider contributing your runner to the Mockito community).\n"
                            + "Bear in mind that you can still enjoy all features of the framework without using runners (they are completely optional).\n"
                            + "If you get this error despite using JUnit 4.5 or higher then please report this error to the mockito mailing list.\n",
                    t);
        }
    }

}
