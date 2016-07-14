package junit.extensions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag("unit-test")
@ExtendWith(MockitoFieldInjectionExtension.class)
@ExtendWith(ArgumentCaptorParameterResolver.class)
@ExtendWith(MockParameterResolver.class)
public @interface UnitTest {
}
