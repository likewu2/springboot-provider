package com.springboot.provider.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.springboot.provider.common.jackson.BigNumberSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Value("${spring.servlet.header.location}")
    private String header;

    @Value("${spring.servlet.multipart.location}")
    private String file;

    /**
     * Return a handler mapping ordered at 1 to map URL paths directly to
     * view names. To configure view controllers, override
     * {@link #addViewControllers}.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
//        registry.addViewController("/home").setViewName("home");
//        registry.addViewController("/hello").setViewName("hello");
//        registry.addViewController("/login").setViewName("login");
//        registry.addViewController("/upload").setViewName("uploadForm");
    }

    /**
     * Override this method to configure cross origin requests processing.
     *
     * @see CorsRegistry
     * @since 4.2
     */
    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
//                .allowedOrigins("*")
                .allowedOriginPatterns("*")
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE")
                .exposedHeaders("Authorization")
                .maxAge(3600);
    }

    /**
     * Override this method to add resource handlers for serving static resources.
     *
     * @see ResourceHandlerRegistry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // http://127.0.0.1:8090/img/header.jpg
        registry.addResourceHandler("/img/**").addResourceLocations("file:" + header);
        // http://127.0.0.1:8090/file/ssr.txt
        registry.addResourceHandler("/file/**").addResourceLocations("file:" + file);

        registry.addResourceHandler("/**").addResourceLocations("classpath:/templates/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");

    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.forEach(converter -> {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ObjectMapper objectMapper = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
                // 对于空的对象转json的时候不抛出错误
                objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
                objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                // 设置输入时忽略在json字符串中存在但在java对象实际没有的属性
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                // 设置输出时包含属性的风格
                objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

                // 全局配置序列化返回 JSON 处理
                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
                simpleModule.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
                simpleModule.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
                simpleModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
                simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
                simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
                objectMapper.registerModule(simpleModule);
                // 时间格式化
                objectMapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_FORMAT));
                objectMapper.setTimeZone(TimeZone.getDefault());

                ((MappingJackson2HttpMessageConverter) converter).setObjectMapper(objectMapper);
            }
        });
    }
}