package com.springboot.provider.common.handler;

import com.springboot.provider.common.holder.ThreadPoolExecutorHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/*
* org.springframework.context.ApplicationContextInitializer
* 这是整个spring容器在刷新之前初始化ConfigurableApplicationContext的回调接口，简单来说，就是在容器刷新之前调用此类的initialize方法。这个点允许被用户自己扩展。用户可以在整个spring容器还没被初始化之前做一些事情。
*
* 可以想到的场景可能为，在最开始激活一些配置，或者利用这时候class还没被类加载器加载的时机，进行动态字节码注入等操作。
* */
@Component
public class ApplicationContextInitializerHandler implements ApplicationContextInitializer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Initialize the given application context.
     * 因为这时候spring容器还没被初始化，所以想要自己的扩展的生效，有以下三种方式：
     *
     * 在启动类中用springApplication.addInitializers(new ApplicationContextInitializeHandler())语句加入
     * 配置文件配置context.initializer.classes=com.spring.development.common.holder.ApplicationContextInitializeHandler
     * Spring SPI扩展，在spring.factories中加入org.springframework.context.ApplicationContextInitializer=com.spring.development.common.holder.ApplicationContextInitializeHandler
     * @param applicationContext the application to configure
     */
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.out.println("[ApplicationContextInitializer]");
        Runtime.getRuntime().addShutdownHook(new Thread("thShutDownHook"){
            @Override
            public void run() {
                // 关闭线程池
                ExecutorService threadPoolExecutor = ThreadPoolExecutorHolder.getThreadPoolExecutor();
                threadPoolExecutor.shutdown();
                logger.info("threadPoolExecutor.isShutdown() = " + threadPoolExecutor.isShutdown());
                try {
                    threadPoolExecutor.awaitTermination(3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("threadPoolExecutor.isTerminated() = " + threadPoolExecutor.isTerminated());
                if(!threadPoolExecutor.isTerminated()){
                    threadPoolExecutor.shutdownNow();
                    try {
                        while (!threadPoolExecutor.isTerminated()){
                            threadPoolExecutor.awaitTermination(10,TimeUnit.SECONDS);
                            logger.info("threadPoolExecutor.isTerminated() = " + threadPoolExecutor.isTerminated());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
