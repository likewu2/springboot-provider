package com.springboot.provider.config;

import com.mysql.cj.jdbc.MysqlXADataSource;
import com.springboot.provider.common.builder.AtomikosDataSourceBuilder;
import com.springboot.provider.common.holder.MultiDataSourceHolder;
import com.springboot.provider.common.proxy.JdbcOperationsProxy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import javax.sql.XADataSource;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String RESOURCE_NAME = "hisDataSource";

    @Value("${mybatis-plus.his.mapper-locations}")
    private String[] mapperLocations;

    @Value("${mybatis-plus.type-aliases-package}")
    private String typeAliasesPackage;

    @Value("${spring.jta.atomikos.datasource.his.xa-data-source-class-name}")
    private String xaDataSourceClassName;

//    @Bean(value = "hisProperties")
//    @ConfigurationProperties(prefix = "spring.datasource.his")
//    public Properties hisProperties() {
//        return new Properties();
//    }

//    @Bean(name = "hisXADataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.his")
//    public XADataSource mysqlXADataSource(){
//        return new MysqlXADataSource();
//    }

    @Primary
    @Bean(name = "hisDataSource")
    @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.his")
    public DataSource hisDataSource(/*@Qualifier("hisProperties") Properties properties*/   /*@Qualifier("hisXADataSource") XADataSource xaDataSource*/) {
        return new AtomikosDataSourceBean();
//        return AtomikosDataSourceBuilder.createAtomikosDataSourceBean(RESOURCE_NAME, xaDataSourceClassName, properties);
//        return AtomikosDataSourceBuilder.createAtomikosDataSourceBean(RESOURCE_NAME, xaDataSourceClassName, xaDataSource);
    }

    @Bean(name = "hisJdbcOperations")
    public JdbcOperations hisJdbcTemplate(@Qualifier("hisDataSource") DataSource dataSource) {
//        if (MultiDataSourceHolder.addDataSource(RESOURCE_NAME, dataSource)) {
//            return JdbcOperationsProxy.getProxyInstance(RESOURCE_NAME);
//        }
//        return null;

        return JdbcOperationsProxy.getProxyInstance(dataSource);
    }

    @Primary
    @Bean(name = "hisSqlSessionFactory")
    public SqlSessionFactory hisSqlSessionFactory(@Qualifier(value = "hisDataSource") DataSource dataSource) throws Exception {
        logger.info(RESOURCE_NAME + ": is registering...");
        SqlSessionFactory sqlSessionFactory = AtomikosDataSourceBuilder.createSqlSessionFactory(dataSource, typeAliasesPackage, mapperLocations);
        logger.info(RESOURCE_NAME + ": has been registered successfully!");
        return sqlSessionFactory;
    }

    @Bean(name = "hisSqlSessionTemplate")
    public SqlSessionTemplate hisSqlSessionTemplate(@Qualifier("hisSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
