package jp.pgw.develop.swallow.s2test.database;

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
