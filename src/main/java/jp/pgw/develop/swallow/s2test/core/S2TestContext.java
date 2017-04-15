package jp.pgw.develop.swallow.s2test.core;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.unit.annotation.PublishedTestContext;

@PublishedTestContext
public interface S2TestContext {

    void register(Class<?> componentClass);

    void register(Class<?> componentClass, String componentName);

    void register(Object component);

    void register(Object component, String componentName);

    void register(ComponentDef componentDef);

    void override(Class<?> componentClass);

    void override(Class<?> componentClass, String componentName);

    void override(Object component, String componentName);

    void include(String path);

    <T> T getComponent(Class<? extends T> componentKey);

    Object getComponent(Object componentKey);

    boolean hasComponentDef(Object componentKey);

    ComponentDef getComponentDef(Object componentKey);

    String getTestClassPackagePath();

}
