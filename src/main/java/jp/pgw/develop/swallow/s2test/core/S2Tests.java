package jp.pgw.develop.swallow.s2test.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.impl.ThreadSafeS2ContainerImpl;
import org.seasar.framework.container.util.S2ContainerUtil;
import org.seasar.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class S2Tests {

    static final Logger log = LoggerFactory.getLogger(S2Tests.class);

    public static final String ENV_PATH = "env.txt";

    public static final String ENV_VALUE = "it";

    public static S2Container override(final S2Container container, final S2Container override) {
        synchronized (container.getRoot()) {
            final Map<S2Container, S2Container> cache = new HashMap<S2Container, S2Container>();
            final S2Container newContainer = overrideInternal(container, override, cache);
            setRoot(newContainer, newContainer);
            setDescendant(newContainer, newContainer);
            return newContainer;
        }
    }

    private static void setRoot(final S2Container root, final S2Container container) {
        container.setRoot(root);
        for (int i = 0, size = container.getChildSize(); i < size; i++) {
            setRoot(root, container.getChild(i));
        }
    }

    private static void setDescendant(final S2Container root, final S2Container container) {
        if (root != container && !root.hasDescendant(container.getPath())) {
            root.registerDescendant(container);
        }
        for (int i = 0, size = container.getChildSize(); i < size; i++) {
            setDescendant(root, container.getChild(i));
        }
    }

    private static S2Container overrideInternal(final S2Container container, final S2Container override,
            final Map<S2Container, S2Container> cache) {
        final List<S2Container> children = new ArrayList<S2Container>();
        for (int i = 0, size = container.getChildSize(); i < size; i++) {
            children.add(overrideInternal(container.getChild(i), override, cache));
        }
        if (cache.containsKey(container)) {
            return cache.get(container);
        }
        final S2Container newContainer = new ThreadSafeS2ContainerImpl();
        newContainer.setNamespace(container.getNamespace());
        newContainer.setClassLoader(container.getClassLoader());
        newContainer.setExternalContext(container.getExternalContext());
        newContainer.setExternalContextComponentDefRegister(container.getExternalContextComponentDefRegister());
        newContainer.setPath(container.getPath());
        newContainer.setInitializeOnCreate(container.isInitializeOnCreate());
        for (int i = 0, size = container.getMetaDefSize(); i < size; i++) {
            newContainer.addMetaDef(container.getMetaDef(i));
        }
        for (int i = 0, size = container.getComponentDefSize(); i < size; i++) {
            final Set<ComponentDef> defSet = new LinkedHashSet<ComponentDef>();
            final ComponentDef def = container.getComponentDef(i);
            if (StringUtil.isNotEmpty(def.getComponentName()) && override.hasComponentDef(def.getComponentName())) {
                defSet.add(override.getComponentDef(def.getComponentName()));
            } else if (def.getComponentClass() != null) {
                final Class<?>[] classes = S2ContainerUtil.getAssignableClasses(def.getComponentClass());
                for (final Class<?> clazz : classes) {
                    if (override.hasComponentDef(clazz)) {
                        final ComponentDef overrideDef = override.getComponentDef(clazz);
                        if (StringUtil.isEmpty(overrideDef.getComponentName())) {
                            defSet.add(overrideDef);
                        }
                    }
                }
            }
            if (defSet.isEmpty()) {
                def.setContainer(newContainer);
                newContainer.register(def);
            } else {
                for (final ComponentDef overrideDef : defSet) {
                    overrideDef.setContainer(newContainer);
                    newContainer.register(overrideDef);
                }
            }
        }
        for (final S2Container child : children) {
            newContainer.include(child);
        }
        cache.put(container, newContainer);
        return newContainer;
    }

    public static void showContainer(final S2Container container) {
        if (log.isDebugEnabled()) {
            for (int i = 0, size = container.getChildSize(); i < size; i++) {
                showContainer(container.getChild(i));
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(
                    "\r\n===================================================================================================");
            sb.append("\r\npath:").append(container.getPath()).append("\r\ncontainer:").append(container);
            for (int i = 0, size = container.getComponentDefSize(); i < size; i++) {
                sb.append(
                        "\r\n---------------------------------------------------------------------------------------------------");
                final ComponentDef def = container.getComponentDef(i);
                sb.append("\r\ncomponentName:").append(def.getComponentName()).append("\r\ncomponentClass:")
                        .append(def.getComponentClass()).append("\r\nconcreteClass:").append(def.getConcreteClass());
            }
            sb.append(
                    "\r\n===================================================================================================");
            log.debug(sb.toString());
        }
    }

    private S2Tests() {
    }

}
