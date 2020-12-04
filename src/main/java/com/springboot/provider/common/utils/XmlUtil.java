package com.springboot.provider.common.utils;

import java.util.Map;

public class XmlUtil {
    public static String mapToXml(Map<String, String> map, String root){
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(root).append(">");
        sb.append(mapToXml(map));
        sb.append("</").append(root).append(">");
        return sb.toString();
    }

    public static String mapToXml(Map<String, String> map){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && !("").equals(value)){
                sb.append("<").append(key).append("><![CDATA[").append(value).append("]]></").append(key).append(">");
            }
        }
        return sb.toString();
    }
}
