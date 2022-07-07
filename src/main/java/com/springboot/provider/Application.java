package com.springboot.provider;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.springboot.mjt.annotation.EnableMappingJdbcTemplate;
import com.springboot.provider.common.selector.annotation.EnableBeans;
import com.springboot.provider.config.SSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

//@EnableHttps
@EnableFeignClients
// 自定义数据源一定要排除SpringBoot自动配置数据源，不然会出现循环引用的问题，The dependencies of some of the beans in the application context form a cycle
@SpringBootApplication/*(exclude = {DataSourceAutoConfiguration.class})*/
@EnableBeans(packages = "com.springboot.provider.module.his.entity")
@EnableMappingJdbcTemplate(baseLocations = {"classpath:/xml/*.xml"})
public class Application {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${sm4.key:spring-framework}")
    private String key;

//    Springboot 启动流程及扩展点:
//    ApplicationContextInitializer(initialize) -> AbstractApplicationContext(refresh)
//        -> BeanDefinitionRegistryPostProcessor(postProcessBeanDefinitionRegistry) -> BeanDefinitionRegistryPostProcessor(postProcessBeanFactory)
//        -> BeanFactoryPostProcessor(postProcessBeanFactory) -> InstantiationAwareBeanPostProcessor(postProcessBeforeInstantiation)
//        -> SmartInstantiationAwareBeanPostProcessor(determineCandidateConstructors) -> MergedBeanDefinitionPostProcessor(postProcessMergedBeanDefinition)
//        -> InstantiationAwareBeanPostProcessor(postProcessAfterInstantiation) -> SmartInstantiationAwareBeanPostProcessor(getEarlyBeanReference)
//        -> BeanFactoryAware(setBeanFactory) -> InstantiationAwareBeanPostProcessor(postProcessProperties) -> ApplicationContextAwareProcessor(invokeAwareInterfaces)
//        -> BeanNameAware(setBeanName) -> InstantiationAwareBeanPostProcessor(postProcessBeforeInitialization) -> @PostConstruct -> InitializingBean(afterPropertiesSet)
//        -> InstantiationAwareBeanPostProcessor(postProcessAfterInitialization) -> FactoryBean(getObject) -> SmartInitializingSingleton(afterSingletonsInstantiated)
//        -> CommandLineRunner(run) -> DisposableBean(destroy)

//    我们从这些spring&springboot的扩展点当中，大致可以窥视到整个bean的生命周期。在业务开发或者写中间件业务的时候，可以合理利用spring提供给我们的扩展点，在spring启动的各个阶段内做一些事情。以达到自定义初始化的目的。

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    //    打印注册到spring boot中的bean
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            logger.info("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                logger.info(beanName);
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        restTemplateBuilder.additionalMessageConverters(mappingJackson2HttpMessageConverter);
        restTemplateBuilder.additionalMessageConverters(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplateBuilder.requestFactory(SSL::new);
        return restTemplateBuilder.build();
    }

    @Bean
    public SymmetricCrypto symmetricCrypto() {
        return SmUtil.sm4(key.getBytes(StandardCharsets.UTF_8));
    }


}
