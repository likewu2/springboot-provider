package com.springboot.provider.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: test
 * @package util
 * @description
 * @author: XuZhenkui
 * @create: 2020-12-16 15:41
 **/
public class JsonAndXmlUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static XmlMapper xmlMapper = new XmlMapper();

    static {
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
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    }

    public static Map jsonToMap(String json) {
        if (json == null || "".equals(json)) {
            return null;
        }
        Map map;
        try {
            map = objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return null;
        }
        return map;
    }

    /**
     * 对象转字符串
     *
     * @param object
     * @return json 字符串
     */
    public static String objectToJson(Object object) {
        if (object == null) {
            return null;
        }
        String json;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
        return json;
    }

    /**
     * 对象转xml格式字符串
     *
     * @param data
     * @return xml 字符串
     */
    public static String objectToXml(Object data) {
        if (data == null) {
            return null;
        }
        String xml;
        try {
            xml = xmlMapper.writeValueAsString(data);
        } catch (Exception e) {
            return null;
        }
        return xml;
    }

    /**
     * json字符串转对象
     *
     * @param json  json字符串
     * @param clazz 类
     * @param <T>   泛型
     * @return java 对象
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        if (json == null || "".equals(json)) {
            return null;
        }
        T t;
        try {
            t = objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
        return t;
    }

    /**
     * xml字符串转对象
     *
     * @param xml   xml字符串
     * @param clazz 类
     * @param <T>   泛型
     * @return java 对象
     */
    public static <T> T xmlToObject(String xml, Class<T> clazz) {
        if (xml == null || "".equals(xml)) {
            return null;
        }
        T t;
        try {
            t = xmlMapper.readValue(xml, clazz);
        } catch (Exception e) {
            return null;
        }
        return t;
    }

    /**
     * 指定根节点, map转xml字符串
     *
     * @param map
     * @param root 根节点
     * @return xml字符串
     */
    public static String mapToXml(Map<String, Object> map, String root) {
        if (root == null || "".equals(root) || map == null || map.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(root).append(">");
        sb.append(mapToXml(map));
        sb.append("</").append(root).append(">");
        return sb.toString();
    }

    /**
     * map转xml字符串
     *
     * @param map
     * @return xml字符串
     */
    public static String mapToXml(Map<String, Object> map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                sb.append(mapToXml((Map<String, Object>) value));
            } else if (value != null && !("").equals(value)) {
                sb.append("<").append(key).append("><![CDATA[").append(value).append("]]></").append(key).append(">");
            }
        }
        return sb.toString();
    }

    /**
     * 获取xml字符串中元素标签值
     * <p>xml中元素标签唯一</p>
     *
     * @param xml     响应报文(xml字符串格式)
     * @param element 元素名
     * @return xml字符串中元素标签值
     * @throws Exception
     */
    public static String getXmlSingleElementValue(String xml, String element) {
        if (xml == null || "".equals(xml) || element == null || "".equals(element)) {
            return null;
        }
        //元素名<ELEMENT key = value ...>(.*)<ELEMENT/>
        StringBuffer regex = new StringBuffer();
        regex.append("<").append(element + ".*").append(">");
        regex.append("(.*)");
        regex.append("</").append(element).append(">");

        String str = "";
        Matcher matcher = Pattern.compile(regex.toString()).matcher(xml);
        while (matcher.find()) {
            str = matcher.group(1);
        }
        return str;
    }

    /**
     * 获取xml字符串中元素标签值
     * <p>xml存在多个该元素标签</p>
     * <p>exmple:<DATA></DATA></p>
     *
     * @param xml     响应报文(xml字符串格式)
     * @param element 元素名
     * @return xml字符串中元素标签列表
     * @throws Exception
     */
    public static List<String> getXmlListElementValue(String xml, String element) {
        if (xml == null || "".equals(xml) || element == null || "".equals(element)) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        //元素名<ELEMENT key = value ...>([^</ELEMENT>]*)</ELEMENT>
        StringBuffer regex = new StringBuffer();
        regex.append("<").append(element + ".*").append(">");
        regex.append("([^</" + element + ">]*)");
        regex.append("</").append(element).append(">");
        Matcher matcher = Pattern.compile(regex.toString()).matcher(xml);
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }

    /**
     * 将xml字符串中的节点转为大写字母
     *
     * @param xml
     * @return
     */
    public static String xmlNodeToUpperCase(String xml) {
        if (xml == null || "".equals(xml)) {
            return null;
        }
        String regex = "<(/*[A-Za-z]+)>";
        Matcher matcher = Pattern.compile(regex).matcher(xml);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<" + matcher.group(1).toUpperCase() + ">");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 将xml字符串中的节点转为小写字母
     *
     * @param xml
     * @return
     */
    public static String xmlNodeToLowerCase(String xml) {
        if (xml == null || "".equals(xml)) {
            return null;
        }
        String regex = "<(/*[A-Za-z]+)>";
        Matcher matcher = Pattern.compile(regex).matcher(xml);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<" + matcher.group(1).toLowerCase() + ">");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 将xml字符串中的节点首字母转为大写字母
     *
     * @param xml
     * @return
     */
    public static String xmlNodeFirstLetterToUpperCase(String xml) {
        if (xml == null || "".equals(xml)) {
            return null;
        }
        String regex = "<(/*[A-Za-z]+)>";
        Matcher matcher = Pattern.compile(regex).matcher(xml);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String node = matcher.group(1);
            String s = "<" + node.replace(node.charAt(0), lowerToUpperConvertor(node.charAt(0))) + ">";
            matcher.appendReplacement(sb, s);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 将xml字符串中的节点首字母转为小写字母
     *
     * @param xml
     * @return
     */
    public static String xmlNodeFirstLetterToLowerCase(String xml) {
        if (xml == null || "".equals(xml)) {
            return null;
        }
        String regex = "<(/*[A-Za-z]+)>";
        Matcher matcher = Pattern.compile(regex).matcher(xml);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String node = matcher.group(1);
            String s = "<" + node.replace(node.charAt(0), upperToLowerConvertor(node.charAt(0))) + ">";
            matcher.appendReplacement(sb, s);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 大小写字母相互转换
     *
     * @param c
     * @return
     */
    public static char characterConvertor(char c) {
        if ((int) c >= 65 && (int) c <= 90) {
            return (char) (c + 32);
        } else if ((int) c >= 97 && (int) c <= 122) {
            return (char) (c - 32);
        } else {
            return c;
        }
    }

    /**
     * 大写字母转为小写字母
     *
     * @param c 大写字母
     * @return 小写字母
     */
    public static char upperToLowerConvertor(char c) {
        if ((int) c >= 65 && (int) c <= 90) {
            return (char) (c + 32);
        } else {
            return c;
        }
    }

    /**
     * 小写字母转为大写字母
     *
     * @param c 小写字母
     * @return 大写字母
     */
    public static char lowerToUpperConvertor(char c) {
        if ((int) c >= 97 && (int) c <= 122) {
            return (char) (c - 32);
        } else {
            return c;
        }
    }

}
