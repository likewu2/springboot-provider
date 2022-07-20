package com.springboot.provider.common.lifecycle.aware;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;


/*
* ApplicationEventPublisherAware：
* 用于获取ApplicationEventPublisher的一个扩展类，ApplicationEventPublisher可以用来发布事件，结合ApplicationListener来共同使用。
* 这个对象也可以通过spring注入的方式来获得。
* */
@Component
public class ApplicationEventPublisherHolder implements ApplicationEventPublisherAware {

    private static ApplicationEventPublisher publisher;

    /**
     * Set the ApplicationEventPublisher that this object runs in.
     * <p>Invoked after population of normal bean properties but before an init
     * callback like InitializingBean's afterPropertiesSet or a custom init-method.
     * Invoked before ApplicationContextAware's setApplicationContext.
     *
     * @param applicationEventPublisher event publisher to be used by this object
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        publisher = applicationEventPublisher;
    }

    public static ApplicationEventPublisher getPublisher() {
        return publisher;
    }

    public static void publishEvent(ApplicationEvent applicationEvent){
        publisher.publishEvent(applicationEvent);
    }

    public static void publishEvent(Object event){
        publisher.publishEvent(event);
    }
}
