package jp.pgw.develop.swallow.s2test.core;

import java.lang.reflect.Method;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.container.deployer.InstanceDefFactory;
import org.seasar.framework.container.factory.S2ContainerFactory;
import org.seasar.framework.container.factory.TigerAnnotationHandler;
import org.seasar.framework.container.impl.ThreadSafeS2ContainerImpl;
import org.seasar.framework.convention.NamingConvention;
import org.seasar.framework.message.MessageResourceBundleFactory;
import org.seasar.framework.util.ResourceUtil;

public class S2TestContextImpl implements S2InternalTestContext {

    protected final TigerAnnotationHandler handler = new TigerAnnotationHandler();

    protected S2Container container;

    protected S2Container override;

    protected Class<?> testClass;

    protected Method testMethod;

    protected NamingConvention namingConvention;

    protected boolean ejb3Enabled = true;

    protected boolean jtaEnabled = true;

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
        this.container = container.getRoot();
        override = new ThreadSafeS2ContainerImpl();
    }

    @Binding(bindingType = BindingType.MAY)
    public void setEjb3Enabled(final boolean ejb3Enabled) {
        this.ejb3Enabled = ejb3Enabled;
    }

    @Binding(bindingType = BindingType.MAY)
    public void setJtaEnabled(final boolean jtaEnabled) {
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
    public void setNamingConvention(final NamingConvention namingConvention) {
        this.namingConvention = namingConvention;
    }

    @Override
    public void initContainer() {
        if (override.getComponentDefSize() > 0 || override.getChildSize() > 0) {
            container = S2Tests.override(container, override);
        }
        S2Tests.showContainer(container);
        container.init();
        override = null;
        containerInitialized = true;
    }

    @Override
    public void destroyContainer() {
        container.destroy();
        container = null;
        override = null;
        containerInitialized = false;
    }

    @Override
    public void include(final String path) {
        final String convertedPath = ResourceUtil.convertPath(path, testClass);
        S2ContainerFactory.include(container, convertedPath);
    }

    @Override
    public S2Container getContainer() {
        return container;
    }

    @Override
    public <T> T getComponent(final Class<? extends T> componentKey) {
        return componentKey.cast(container.getComponent(componentKey));
    }

    @Override
    public Object getComponent(final Object componentKey) {
        return container.getComponent(componentKey);
    }

    @Override
    public boolean hasComponentDef(final Object componentKey) {
        return container.hasComponentDef(componentKey);
    }

    @Override
    public ComponentDef getComponentDef(final Object componentKey) {
        return container.getComponentDef(componentKey);
    }

    @Override
    public String getTestClassPackagePath() {
        return testClass.getName().replace('.', '/').replaceFirst("/[^/]+$", "");
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
        container.register(component);
    }

    @Override
    public void register(final Object component, final String componentName) {
        register(container, component, componentName);
    }

    @Override
    public void register(final Class<?> componentClass, final String componentName) {
        register(container, componentClass, componentName);
    }

    @Override
    public void register(final Class<?> componentClass) {
        register(container, componentClass);
    }

    @Override
    public void register(final ComponentDef componentDef) {
        container.register(componentDef);
    }

    @Override
    public void override(final Class<?> componentClass) {
        register(override, componentClass);
    }

    @Override
    public void override(final Class<?> componentClass, final String componentName) {
        register(override, componentClass, componentName);
    }

    @Override
    public void override(final Object component, final String componentName) {
        register(override, component, componentName);
    }

    protected void register(final S2Container container, final Object component, final String componentName) {
        container.register(component, componentName);
    }

    protected void register(final S2Container container, final Class<?> componentClass) {
        if (namingConvention == null) {
            register(container, componentClass, null);
        } else {
            register(container, componentClass,
                    namingConvention.fromClassNameToComponentName(componentClass.getName()));
        }
    }

    protected void register(final S2Container container, final Class<?> componentClass, final String componentName) {
        final ComponentDef cd = handler.createComponentDef(componentClass, InstanceDefFactory.SINGLETON);
        cd.setComponentName(componentName);
        handler.appendDI(cd);
        handler.appendAspect(cd);
        handler.appendInterType(cd);
        handler.appendInitMethod(cd);
        handler.appendDestroyMethod(cd);
        container.register(cd);
    }

}
