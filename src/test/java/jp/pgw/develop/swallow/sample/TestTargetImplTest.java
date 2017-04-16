package jp.pgw.develop.swallow.sample;

import org.dbunit.ext.h2.H2DataTypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import jp.pgw.develop.swallow.s2test.annotations.S2DatabaseTestConfiguration;
import jp.pgw.develop.swallow.s2test.annotations.S2TestContextConfiguration;
import jp.pgw.develop.swallow.s2test.core.S2TestContext;
import jp.pgw.develop.swallow.s2test.database.S2TestSchemaInfo;
import jp.pgw.develop.swallow.s2test.mock.S2MockitoJUnitRunner;
import jp.pgw.develop.swallow.sample.service.PersonService;
import jp.pgw.develop.swallow.sample.target.TestTarget;

@RunWith(S2MockitoJUnitRunner.class)
@S2TestContextConfiguration
@S2DatabaseTestConfiguration(enableCommit = true)
public class TestTargetImplTest {

    TestTarget target;

    S2TestContext ctx;

    @Mock
    PersonService personService;

    // @BeforeClass
    // public static void init() throws SQLException {
    // RunScript.execute("jdbc:h2:file:./data/sample;DB_CLOSE_DELAY=-1;AUTOCOMMIT=FALSE",
    // "sa", "", "src/test/resources/schema.sql",
    // StandardCharsets.UTF_8, false);
    // }

    @Before
    public void setUp() {
        ctx.register(new S2TestSchemaInfo(null));
        ctx.register(H2DataTypeFactory.class);
    }

    @Test
    public void testExec1() {
        target.exec();
    }

    @Test
    public void testExec2() {
        target.exec();
    }

}
