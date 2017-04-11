package jp.pgw.develop.swallow.s2test.core;

import java.lang.reflect.Method;

import org.seasar.framework.container.AspectDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.convention.NamingConvention;

public interface S2InternalTestContext extends S2TestContext {

	void setTestClass(Class<?> testClass);

	void setTestMethod(Method testMethod);

	void setNamingConvention(NamingConvention namingConvention);

	void initContainer();

	void destroyContainer();

	S2Container getContainer();

	void addAspecDef(Object componentKey, AspectDef aspectDef);

	boolean isEjb3Enabled();

	boolean isJtaEnabled();

}
