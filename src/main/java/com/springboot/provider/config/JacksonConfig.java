package com.springboot.provider.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.springboot.provider.common.jackson.BigNumberSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @Description
 * @Project springboot-provider
 * @Package com.springboot.provider.config
 * @Author xuzhenkui
 * @Date 2022/1/30 13:13
 */
@Configuration
public class JacksonConfig {

    @Primary
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


        // 全局配置序列化返回 JSON 处理
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
        simpleModule.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
        simpleModule.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
        simpleModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        objectMapper.registerModule(simpleModule);
        objectMapper.setTimeZone(TimeZone.getDefault());

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


        // 全局配置序列化返回 JSON 处理
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
        simpleModule.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
        simpleModule.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
        simpleModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        xmlMapper.registerModule(simpleModule);
        xmlMapper.setTimeZone(TimeZone.getDefault());

        return xmlMapper;
    }
}
