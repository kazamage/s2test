package jp.pgw.develop.swallow.s2test.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jp.pgw.develop.swallow.s2test.database.rules.S2DatabaseTester;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface S2DatabaseTestConfiguration {

    Class<? extends S2DatabaseTester> tester() default S2DatabaseTester.class;

    boolean enableCommit() default true;

}
