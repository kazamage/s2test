package jp.pgw.develop.swallow.s2test.database.rules;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.dbunit.AbstractDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.seasar.framework.container.annotation.tiger.AutoBindingType;
import org.seasar.framework.container.annotation.tiger.Component;
import org.seasar.framework.container.annotation.tiger.InstanceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.pgw.develop.swallow.s2test.database.SchemaInfo;

@Component(autoBinding = AutoBindingType.CONSTRUCTOR, instance = InstanceType.SINGLETON)
public class S2DatabaseTester extends AbstractDatabaseTester implements TestRule {

    protected static final Logger log = LoggerFactory.getLogger(S2DatabaseTester.class);

    protected final IDataTypeFactory dataTypeFactory;

    protected final XADataSource xaDataSource;

    protected final DataSource dataSource;

    protected IDataSet expectedDataSet;

    public S2DatabaseTester(final IDataTypeFactory dataTypeFactory, final XADataSource xaDataSource,
            final DataSource dataSource, final SchemaInfo schemaInfo) {
        super(schemaInfo.getSchema());
        if (xaDataSource == null || dataSource == null) {
            throw new NullPointerException("The parameter 'xaDataSource' or 'dataSource' must not be null");
        }
        this.dataTypeFactory = dataTypeFactory;
        this.xaDataSource = xaDataSource;
        this.dataSource = dataSource;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                final IDataSet dataSet = createDataSet();
                if (dataSet == null) {
                    setSetUpOperation(DatabaseOperation.NONE);
                } else {
                    setDataSet(dataSet);
                    setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
                }
                onSetup();
                expectedDataSet = createExpectedDataSet();
                try {
                    base.evaluate();
                } finally {
                    onTearDown();
                }
            }

        };
    }

    @Override
    public IDatabaseConnection getConnection() throws Exception {
        log.debug("getConnection() - start");
        assertTrue("DataSource is not set", xaDataSource != null || dataSource != null);
        final DatabaseConnection conn = new DatabaseConnection(getOriginalConnection(), getSchema());
        final DatabaseConfig config = conn.getConfig();
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
        return conn;
    }

    public IDataSet getExpectedDataSet() {
        return expectedDataSet;
    }

    protected Connection getOriginalConnection() throws SQLException {
        return xaDataSource != null ? xaDataSource.getXAConnection().getConnection() : dataSource.getConnection();
    }

    protected IDataSet createDataSet() {
        return null;
    }

    protected IDataSet createExpectedDataSet() {
        return null;
    }

}
