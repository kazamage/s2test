package jp.pgw.develop.swallow.sample;

import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

import jp.pgw.develop.swallow.sample.target.TestTarget;

public class Main {

    public static void main(final String[] args) {
        SingletonS2ContainerFactory.init();
        SingletonS2Container.getComponent(TestTarget.class).exec();
        SingletonS2ContainerFactory.destroy();
    }

}
