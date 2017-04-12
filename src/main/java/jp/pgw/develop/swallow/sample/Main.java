package jp.pgw.develop.swallow.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;
import org.seasar.framework.container.impl.ThreadSafeS2ContainerImpl;

public class Main {

	public static void main(final String[] args) {
		final S2Container container = S2ContainerFactory.create("app.dicon");
		show(container);
		// container = move(container, new HashMap<S2Container, S2Container>());
		container.init();
		final TestTarget testTarget = (TestTarget) container.getComponent(TestTarget.class);
		testTarget.exec();
	}

	public static void show(final S2Container container) {
		for (int i = 0, size = container.getChildSize(); i < size; i++) {
			show(container.getChild(i));
		}
		System.out.println("");
		System.out.println("");
		System.out.println("path:" + container.getPath());
		System.out.println("container:" + container);
		for (int i = 0, size = container.getComponentDefSize(); i < size; i++) {
			final ComponentDef def = container.getComponentDef(i);
			System.out.println("");
			System.out.println("componentName:" + def.getComponentName());
			System.out.println("componentClass:" + def.getComponentClass());
			System.out.println("concreteClass:" + def.getConcreteClass());
		}
	}

	static S2Container move(final S2Container src, final Map<S2Container, S2Container> cache) {
		final List<S2Container> children = new ArrayList<S2Container>();
		for (int i = 0, size = src.getChildSize(); i < size; i++) {
			children.add(move(src.getChild(i), cache));
		}
		if (cache.containsKey(src)) {
			return cache.get(src);
		}
		final S2Container newContainer = new ThreadSafeS2ContainerImpl();
		newContainer.setExternalContext(src.getExternalContext());
		newContainer.setExternalContextComponentDefRegister(src.getExternalContextComponentDefRegister());
		newContainer.setNamespace(src.getNamespace());
		newContainer.setPath(src.getPath());
		for (int i = 0, size = src.getComponentDefSize(); i < size; i++) {
			final ComponentDef def = src.getComponentDef(i);
			def.setContainer(newContainer);
			newContainer.register(def);
		}
		for (final S2Container child : children) {
			newContainer.include(child);
		}
		cache.put(src, newContainer);
		return newContainer;
	}

}
