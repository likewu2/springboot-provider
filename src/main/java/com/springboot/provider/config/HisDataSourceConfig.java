package com.springboot.provider.config;

import com.springboot.provider.common.builder.AtomikosDataSourceBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @program: bsinterface
 * @package com.bsoft.bsinterface.config
 * @description
 * @author: XuZhenkui
 * @create: 2020-12-03 14:12
 **/
@Configuration
@MapperScan(basePackages = {"com.springboot.provider.module.his.**.mapper"}, sqlSessionTemplateRef = "hisSqlSessionTemplate")
public class HisDataSourceConfig {

    @Value("${mybatis-plus.mapper-locations}")
    private String location;

    @Value("${spring.jta.atomikos.datasource.his.xa-data-source-class-name}")
    private String xaDataSourceClassName;

    public static final String RESOURCE_NAME = "hisDataSource";

    @Bean(value = "hisProperties")
    @ConfigurationProperties(prefix = "spring.datasource.his")
    public Properties hisProperties() {
        return new Properties();
    }

    @Primary
    @Bean(name = "hisDataSource")
    public DataSource hisDataSource(@Qualifier("hisProperties") Properties properties) {
        return AtomikosDataSourceBuilder.createAtomikosDataSourceBean(xaDataSourceClassName, properties, RESOURCE_NAME);
    }

    @Bean(name = "hisJdbcTemplate")
    public JdbcTemplate hisJdbcTemplate(@Qualifier("hisDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "hisSqlSessionFactory")
    public SqlSessionFactory hisSqlSessionFactory(@Qualifier(value = "hisDataSource") DataSource dataSource) throws Exception {
        return AtomikosDataSourceBuilder.createSqlSessionFactory(dataSource, location);
    }

    @Bean(name = "hisSqlSessionTemplate")
    public SqlSessionTemplate hisSqlSessionTemplate(@Qualifier("hisSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
