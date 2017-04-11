package jp.pgw.develop.swallow.s2test.mock.runners;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.ejb.EJB;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.junit.DefaultTestFinishedEvent;
import org.mockito.internal.junit.MockitoTestListener;
import org.mockito.internal.util.Supplier;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.container.impl.S2ContainerBehavior;
import org.seasar.framework.convention.NamingConvention;
import org.seasar.framework.convention.impl.NamingConventionImpl;
import org.seasar.framework.env.Env;
import org.seasar.framework.unit.annotation.PublishedTestContext;
import org.seasar.framework.unit.impl.ConventionTestIntrospector;
import org.seasar.framework.util.DisposableUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;

import jp.pgw.develop.swallow.s2test.annotations.S2ContextConfiguration;
import jp.pgw.develop.swallow.s2test.annotations.S2DatabaseConfiguration;
import jp.pgw.develop.swallow.s2test.core.S2InternalTestContext;
import jp.pgw.develop.swallow.s2test.core.S2Test;
import jp.pgw.develop.swallow.s2test.core.S2TestContextImpl;
import jp.pgw.develop.swallow.s2test.database.jdbc.DataSourceProxy;
import jp.pgw.develop.swallow.s2test.database.jdbc.XADataSourceProxy;
import jp.pgw.develop.swallow.s2test.mock.statements.after.S2RunUnbindFields;
import jp.pgw.develop.swallow.s2test.mock.statements.before.S2RunBindFields;
import jp.pgw.develop.swallow.s2test.mock.statements.before.S2RunInitContext;
import jp.pgw.develop.swallow.s2test.mock.statements.post.S2RunPostProcess;

public class S2InternalRunner extends BlockJUnit4ClassRunner {

	public Object target;

	protected final Class<?> testClass;

	protected final Supplier<MockitoTestListener> listenerSupplier;

	protected final ConventionTestIntrospector introspector;

	protected final String configFile;

	protected final boolean enableCommit;

	protected MockitoTestListener mockitoTestListener;

	protected List<Field> boundFields = CollectionsUtil.newArrayList();

	protected S2InternalTestContext testContext;

	public S2InternalRunner(Class<?> klass, final Supplier<MockitoTestListener> listenerSupplier)
			throws InitializationError {
		super(klass);
		this.testClass = klass;
		this.listenerSupplier = listenerSupplier;
		introspector = new ConventionTestIntrospector();
		introspector.init();
		final S2ContextConfiguration contextConfig = testClass.getDeclaredAnnotation(S2ContextConfiguration.class);
		if (contextConfig == null || contextConfig.value() == null || contextConfig.value().length() == 0) {
			configFile = null;
		} else {
			configFile = contextConfig.value();
		}
		final S2DatabaseConfiguration transactionConfig = testClass
				.getDeclaredAnnotation(S2DatabaseConfiguration.class);
		if (transactionConfig == null) {
			enableCommit = false;
		} else {
			enableCommit = transactionConfig.enableCommit();
		}
	}

	public void initContainer(FrameworkMethod method) {
		boundFields = CollectionsUtil.newArrayList();
		Env.setFilePath(S2Test.ENV_PATH);
		Env.setValueIfAbsent(S2Test.ENV_VALUE);
		testContext = setUpTestContext(method.getMethod());
	}

	public void initContext() {
		testContext.initContainer();
	}

	public void bindFields() {
		for (Class<?> clazz = target.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			final Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; ++i) {
				bindField(fields[i]);
			}
		}
	}

	public void unbindFields() throws Exception {
		for (final Field field : boundFields) {
			try {
				field.set(target, null);
			} catch (IllegalArgumentException e) {
				System.err.println(e);
			} catch (IllegalAccessException e) {
				System.err.println(e);
			}
		}
	}

	public void destroyContext() {
		testContext.destroyContainer();
	}

	public void dispose() {
		boundFields = null;
		testContext = null;
		DisposableUtil.dispose();
		S2ContainerBehavior.setProvider(new S2ContainerBehavior.DefaultProvider());
	}

	@Override
	protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
		// get new test listener and add add it to the framework
		mockitoTestListener = listenerSupplier.get();
		Mockito.framework().addListener(mockitoTestListener);

		// init annotated mocks before tests
		MockitoAnnotations.initMocks(target);
		this.target = target;
		return super.withBefores(method, target, new S2RunInitContext(new S2RunBindFields(statement, this), this));
	}

	@Override
	protected Statement withAfters(FrameworkMethod method, Object target, Statement statement) {
		return super.withAfters(method, target, new S2RunUnbindFields(statement, this));
	}

	@Override
	protected Statement methodBlock(FrameworkMethod method) {
		return new S2RunPostProcess(super.methodBlock(method), this, method);
	}

	@Override
	public void run(final RunNotifier notifier) {
		RunListener listener = new RunListener() {
			Throwable failure;

			@Override
			public void testFailure(Failure failure) throws Exception {
				this.failure = failure.getException();
			}

			@Override
			public void testFinished(Description description) throws Exception {
				super.testFinished(description);
				try {
					if (mockitoTestListener != null) {
						Mockito.framework().removeListener(mockitoTestListener);
						mockitoTestListener.testFinished(
								new DefaultTestFinishedEvent(target, description.getMethodName(), failure));
					}
					Mockito.validateMockitoUsage();
				} catch (Throwable t) {
					// In order to produce clean exception to the user
					// we need to fire test failure with the right
					// description
					// Otherwise JUnit framework will report failure
					// with some generic test name
					notifier.fireTestFailure(new Failure(description, t));
				}
			}
		};

		notifier.addListener(listener);
		super.run(notifier);
	}

	protected S2Container createRootContainer(Method method) {
		final String rootDicon = introspector.getRootDicon(testClass, method);
		if (StringUtil.isEmpty(rootDicon)) {
			if (StringUtil.isEmpty(configFile)) {
				return S2ContainerFactory.create();
			} else {
				return S2ContainerFactory.create(configFile);
			}
		}
		S2Container container = S2ContainerFactory.create(rootDicon);
		if (StringUtil.isNotEmpty(configFile)) {
			S2ContainerFactory.include(container, configFile);
		}
		return container;
	}

	protected S2InternalTestContext setUpTestContext(Method method) {
		final S2Container container = createRootContainer(method);
		container.register(S2TestContextImpl.class);
		SingletonS2ContainerFactory.setContainer(container);
		S2InternalTestContext testContext = S2InternalTestContext.class
				.cast(container.getComponent(S2InternalTestContext.class));
		testContext.setTestClass(testClass);
		testContext.setTestMethod(method);
		if (!testContext.hasComponentDef(NamingConvention.class)
				&& introspector.isRegisterNamingConvention(testClass, method)) {
			final NamingConvention namingConvention = new NamingConventionImpl();
			testContext.register(namingConvention);
			testContext.setNamingConvention(namingConvention);
		}
		if (container.hasComponentDef(XADataSource.class)) {
			final ComponentDef def = container.getComponentDef(XADataSource.class);
			testContext
			.override(new XADataSourceProxy(XADataSource.class.cast(container.getComponent(XADataSource.class)),
					enableCommit), def.getComponentName());
		}
		if (container.hasComponentDef(DataSource.class)) {
			final ComponentDef def = container.getComponentDef(DataSource.class);
			testContext.override(
					new DataSourceProxy(DataSource.class.cast(container.getComponent(DataSource.class)), enableCommit),
					def.getComponentName());
		}

		for (Class<?> clazz = testClass; clazz != Object.class; clazz = clazz.getSuperclass()) {

			final Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; ++i) {
				final Field field = fields[i];
				final Class<?> fieldClass = field.getType();
				if (isAutoBindable(field) && fieldClass.isAssignableFrom(testContext.getClass())
						&& fieldClass.isAnnotationPresent(PublishedTestContext.class)) {
					field.setAccessible(true);
					if (ReflectionUtil.getValue(field, target) != null) {
						continue;
					}
					bindField(field, testContext);
				}
			}
		}
		return testContext;
	}

	protected boolean isAutoBindable(final Field field) {
		final int modifiers = field.getModifiers();
		return !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers) && !field.getType().isPrimitive();
	}

	protected void bindField(final Field field) {
		if (isAutoBindable(field)) {
			field.setAccessible(true);
			if (ReflectionUtil.getValue(field, target) != null) {
				return;
			}
			final String name = resolveComponentName(field);
			Object component = null;
			if (testContext.hasComponentDef(name)) {
				component = testContext.getComponent(name);
				if (component != null) {
					Class<?> componentClass = component.getClass();
					if (!field.getType().isAssignableFrom(componentClass)) {
						component = null;
					}
				}
			}
			if (component == null && testContext.hasComponentDef(field.getType())) {
				component = testContext.getComponent(field.getType());
			}
			if (component != null) {
				bindField(field, component);
			}
		}
	}

	protected void bindField(final Field field, final Object object) {
		ReflectionUtil.setValue(field, target, object);
		boundFields.add(field);
	}

	protected String resolveComponentName(final Field filed) {
		if (testContext.isEjb3Enabled()) {
			final EJB ejb = filed.getAnnotation(EJB.class);
			if (ejb != null) {
				if (!StringUtil.isEmpty(ejb.beanName())) {
					return ejb.beanName();
				} else if (!StringUtil.isEmpty(ejb.name())) {
					return ejb.name();
				}
			}
		}
		return normalizeName(filed.getName());
	}

	protected String normalizeName(final String name) {
		return StringUtil.replace(name, "_", "");
	}

}