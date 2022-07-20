package com.springboot.provider.common.lifecycle.aware;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/*
* EmbeddedValueResolverAware：
* 用于获取StringValueResolver的一个扩展类， StringValueResolver用于获取基于String类型的properties的变量，
* 一般我们都用@Value的方式去获取，如果实现了这个Aware接口，把StringValueResolver缓存起来，通过这个类去获取String类型的变量，效果是一样的。
* */
@Component
public class EmbeddedValueResolverAwareHolder implements EmbeddedValueResolverAware {

    private static StringValueResolver stringValueResolver;

    /**
     * Set the StringValueResolver to use for resolving embedded definition values.
     *
     * @param resolver
     */
    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        stringValueResolver = resolver;
    }

    public static StringValueResolver getStringValueResolver(){
        return stringValueResolver;
    }
}
