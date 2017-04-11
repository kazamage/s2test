package jp.pgw.develop.swallow.sample;

import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class Main {

	public static void main(String[] args) {
		SingletonS2ContainerFactory.init();
		TestTarget testTarget = SingletonS2Container.getComponent(TestTarget.class);
		testTarget.exec();
	}

}
