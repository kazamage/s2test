package jp.pgw.develop.swallow.s2test.database;

import org.seasar.framework.container.annotation.tiger.AutoBindingType;
import org.seasar.framework.container.annotation.tiger.Component;
import org.seasar.framework.container.annotation.tiger.InstanceType;

@Component(name = "testSchemaInfo", autoBinding = AutoBindingType.CONSTRUCTOR, instance = InstanceType.SINGLETON)
public class S2TestSchemaInfo implements SchemaInfo {

    final String testSchemaName;

    public S2TestSchemaInfo(final String testSchemaName) {
        this.testSchemaName = testSchemaName;
    }

    @Override
    public String getSchema() {
        return testSchemaName;
    }

}
