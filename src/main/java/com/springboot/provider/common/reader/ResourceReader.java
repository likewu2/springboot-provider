package com.springboot.provider.common.reader;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceReader {

    /**
     *  获取 resources 目录下的资源文件
     *  ep: db/quartz_mysql.sql
     * @param file
     * @return
     */
    public static String getResource(String file) {
        String content = "";
        ClassPathResource classPathResource = new ClassPathResource(file);
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
            content = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     *  获取 resources 目录下的资源文件
     *  ep: db/quartz_mysql.sql
     * @param file
     * @return
     */
    public static String getResource(ResourceLoader resourceLoader, String file) {
        String content = "";
        Resource resource = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResource(file);
        try {
            InputStream inputStream = resource.getInputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            content = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

}
