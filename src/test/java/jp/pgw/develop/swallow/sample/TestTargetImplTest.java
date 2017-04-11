package jp.pgw.develop.swallow.sample;

import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import jp.pgw.develop.swallow.s2test.annotations.S2ContextConfiguration;
import jp.pgw.develop.swallow.s2test.annotations.S2DatabaseConfiguration;
import jp.pgw.develop.swallow.s2test.core.S2TestContext;
import jp.pgw.develop.swallow.s2test.mock.S2MockitoJUnitRunner;

@RunWith(S2MockitoJUnitRunner.class)
@S2ContextConfiguration("app.dicon")
@S2DatabaseConfiguration()
public class TestTargetImplTest {

	TestTarget target;

	S2TestContext ctx;

	@Mock
	Service service;

	//	@BeforeClass
	//	public static void init() throws SQLException {
	//		RunScript.execute("jdbc:h2:file:./data/sample;DB_CLOSE_DELAY=-1;AUTOCOMMIT=FALSE", "sa", "", "src/test/resources/schema.sql",
	//				StandardCharsets.UTF_8, false);
	//	}

	@Before
	public void setUp() {
		ctx.override(service, "service");
	}

	@Test
	public void testExec1() {
		doReturn("moge!").when(service).getMessage();
		target.exec();
	}

	@Test
	public void testExec2() {
		doReturn("mogege!").when(service).getMessage();
		target.exec();
	}

}
