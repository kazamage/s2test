package jp.pgw.develop.swallow.sample;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

import jp.pgw.develop.swallow.s2test.core.S2Tests;
import jp.pgw.develop.swallow.sample.target.TestTarget;

public class Main {

    public static void main(final String[] args) {
        final S2Container container = S2ContainerFactory.create("app.dicon");
        S2Tests.showContainer(container);
        container.init();
        final TestTarget testTarget = (TestTarget) container.getComponent(TestTarget.class);
        testTarget.exec();
    }

}
