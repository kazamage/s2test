package jp.pgw.develop.swallow.s2test.core;

import java.lang.reflect.Method;

import org.seasar.framework.container.AspectDef;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.InstanceDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.container.deployer.InstanceDefFactory;
import org.seasar.framework.container.factory.S2ContainerFactory;
import org.seasar.framework.container.factory.TigerAnnotationHandler;
import org.seasar.framework.convention.NamingConvention;
import org.seasar.framework.message.MessageResourceBundleFactory;
import org.seasar.framework.util.ResourceUtil;

public class S2TestContextImpl implements S2InternalTestContext {

	protected final TigerAnnotationHandler handler = new TigerAnnotationHandler();

	protected S2Container override;

	protected S2Container base;

	protected S2Container container;

	protected Class<?> testClass;

	protected Method testMethod;

	protected NamingConvention namingConvention;

	protected boolean ejb3Enabled = false;

	protected boolean jtaEnabled = false;

	protected boolean containerInitialized;

	@InitMethod
	public void init() throws Throwable {
	}

	@DestroyMethod
	public void destroy() {
		MessageResourceBundleFactory.clear();
	}

	@Binding(bindingType = BindingType.MUST)
	public void setContainer(final S2Container container) {
		base = container.getRoot();
		override = S2ContainerFactory.create();
	}

	@Binding(bindingType = BindingType.MAY)
	public void setEjb3Enabled(boolean ejb3Enabled) {
		this.ejb3Enabled = ejb3Enabled;
	}

	@Binding(bindingType = BindingType.MAY)
	public void setJtaEnabled(boolean jtaEnabled) {
		this.jtaEnabled = jtaEnabled;
	}

	@Override
	public void setTestClass(final Class<?> testClass) {
		this.testClass = testClass;
	}

	@Override
	public void setTestMethod(final Method testMethod) {
		this.testMethod = testMethod;
	}

	@Override
	@Binding(bindingType = BindingType.NONE)
	public void setNamingConvention(NamingConvention namingConvention) {
		this.namingConvention = namingConvention;
	}

	@Override
	public void initContainer() {
		container = S2ContainerFactory.create();
		concatDef(override, container);
		concatDef(base, container);
		container.init();
		base = null;
		override = null;
		containerInitialized = true;
	}

	protected void concatDef(S2Container src, S2Container dest) {
		for (int i = 0, size = src.getComponentDefSize(); i < size; i++) {
			final ComponentDef def = src.getComponentDef(i);
			final String componentName = def.getComponentName();
			if (componentName != null && dest.hasComponentDef(def.getComponentName()) || dest.hasComponentDef(def.getComponentClass())) {
				continue;
			}
			def.setContainer(dest);
			dest.register(def);
		}
	}

	@Override
	public void destroyContainer() {
		container.destroy();
		container = null;
		containerInitialized = false;
	}

	protected S2Container getCurrent() {
		return container == null ? base : container;
	}

	@Override
	public void include(final String path) {
		final String convertedPath = ResourceUtil.convertPath(path, testClass);
		S2ContainerFactory.include(getCurrent(), convertedPath);
	}

	@Override
	public S2Container getContainer() {
		return getCurrent();
	}

	@Override
	public <T> T getComponent(final Class<? extends T> componentKey) {
		return componentKey.cast(getCurrent().getComponent(componentKey));
	}

	@Override
	public Object getComponent(final Object componentKey) {
		return getCurrent().getComponent(componentKey);
	}

	@Override
	public boolean hasComponentDef(final Object componentKey) {
		return getCurrent().hasComponentDef(componentKey);
	}

	@Override
	public ComponentDef getComponentDef(final int index) {
		return getCurrent().getComponentDef(index);
	}

	@Override
	public ComponentDef getComponentDef(final Object componentKey) {
		return getCurrent().getComponentDef(componentKey);
	}

	@Override
	public String getTestClassPackagePath() {
		return testClass.getName().replace('.', '/').replaceFirst("/[^/]+$", "");
	}

	@Override
	public void addAspecDef(final Object componentKey, final AspectDef aspectDef) {
		if (containerInitialized) {
			throw new IllegalStateException();
		}
		getCurrent().getComponentDef(componentKey).addAspectDef(0, aspectDef);
	}

	@Override
	public boolean isEjb3Enabled() {
		return ejb3Enabled;
	}

	@Override
	public boolean isJtaEnabled() {
		return jtaEnabled;
	}

	@Override
	public void register(final Object component) {
		register(base, component);
	}

	@Override
	public void register(final Object component, String componentName) {
		register(base, component, componentName);
	}

	@Override
	public void register(Class<?> componentClass, InstanceDef instanceDef) {
		register(base, componentClass, instanceDef);
	}

	@Override
	public void register(final Class<?> componentClass, final String componentName) {
		register(base, componentClass, componentName, InstanceDefFactory.SINGLETON);
	}

	@Override
	public void register(final Class<?> componentClass, final String componentName, InstanceDef instanceDef) {
		register(base, componentClass, componentName, instanceDef);
	}

	@Override
	public void register(final Class<?> componentClass) {
		register(base, componentClass);
	}

	@Override
	public void register(final ComponentDef componentDef) {
		register(base, componentDef);
	}

	@Override
	public void override(Class<?> componentClass) {
		register(override, componentClass);
	}

	@Override
	public void override(Class<?> componentClass, InstanceDef instanceDef) {
		register(override, componentClass, instanceDef);
	}

	@Override
	public void override(Class<?> componentClass, String componentName) {
		register(override, componentClass, componentName);
	}

	@Override
	public void override(Class<?> componentClass, String componentName, InstanceDef instanceDef) {
		register(override, componentClass, componentName, instanceDef);
	}

	@Override
	public void override(Object component) {
		register(override, component);
	}

	@Override
	public void override(Object component, String componentName) {
		register(override, component, componentName);
	}

	@Override
	public void override(ComponentDef componentDef) {
		register(override, componentDef);
	}

	protected void register(S2Container container, final Object component) {
		container.register(component);
	}

	protected void register(S2Container container, final Object component, String componentName) {
		container.register(component, componentName);
	}

	protected void register(S2Container container, Class<?> componentClass, InstanceDef instanceDef) {
		if (namingConvention == null) {
			register(container, componentClass, null, instanceDef);
		} else {
			register(container, componentClass, namingConvention.fromClassNameToComponentName(componentClass.getName()),
					instanceDef);
		}
	}

	protected void register(S2Container container, final Class<?> componentClass, final String componentName) {
		register(container, componentClass, componentName, InstanceDefFactory.SINGLETON);
	}

	protected void register(S2Container container, final Class<?> componentClass, final String componentName,
			InstanceDef instanceDef) {
		final ComponentDef cd = handler.createComponentDef(componentClass, instanceDef);
		cd.setComponentName(componentName);
		handler.appendDI(cd);
		handler.appendAspect(cd);
		handler.appendInterType(cd);
		handler.appendInitMethod(cd);
		handler.appendDestroyMethod(cd);
		container.register(cd);
	}

	protected void register(S2Container container, final Class<?> componentClass) {
		if (namingConvention == null) {
			register(container, componentClass, (String) null);
		} else {
			register(container, componentClass,
					namingConvention.fromClassNameToComponentName(componentClass.getName()));
		}
	}

	protected void register(S2Container container, final ComponentDef componentDef) {
		container.register(componentDef);
	}

}
