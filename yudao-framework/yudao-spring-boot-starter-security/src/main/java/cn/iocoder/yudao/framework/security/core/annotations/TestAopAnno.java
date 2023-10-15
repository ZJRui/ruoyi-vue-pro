package cn.iocoder.yudao.framework.security.core.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TestAopAnno {
    String name() default "";
}
