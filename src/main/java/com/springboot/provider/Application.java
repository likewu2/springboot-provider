package com.springboot.provider;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.springboot.mjt.annotation.EnableMappingJdbcTemplate;
import com.springboot.provider.common.annotation.EnableBeans;
import com.springboot.provider.common.annotation.EnableHttps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@EnableHttps
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
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }

    @Bean
    public SymmetricCrypto symmetricCrypto() {
        return SmUtil.sm4(key.getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 对于空的对象转json的时候不抛出错误
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 允许属性名称没有引号
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 设置输入时忽略在json字符串中存在但在java对象实际没有的属性
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 设置输出时包含属性的风格
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        return objectMapper;
    }

    @Bean
    public XmlMapper xmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        // 对于空的对象转json的时候不抛出错误
        xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 允许属性名称没有引号
        xmlMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号
        xmlMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 设置输入时忽略在json字符串中存在但在java对象实际没有的属性
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 设置输出时包含属性的风格
        xmlMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 解析大小写不敏感
        xmlMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        // 正常忽略多余字段
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时加上文件头信息
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false);
        return xmlMapper;
    }
}
