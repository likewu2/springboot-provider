package com.springboot.provider.common.annotation;

import com.springboot.provider.common.selector.EnableBeanAutoSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @program: springboot-dev
 * @package com.spring.development.common.annotation
 * @description
 * @author: XuZhenkui
 * @create: 2021-02-11 11:45
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableBeanAutoSelector.class)
public @interface EnableBeans {
    //传入包名
    String[] packages() default "";

    boolean isRecursion() default false;
}
