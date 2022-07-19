package com.springboot.provider.config;

import com.springboot.provider.common.builder.AtomikosDataSourceBuilder;
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
import org.springframework.jdbc.core.JdbcOperations;

import javax.sql.DataSource;

/**
 * @program: bsinterface
 * @package com.bsoft.bsinterface.config
 * @description
 * @author: XuZhenkui
 * @create: 2020-12-03 14:12
 **/
@Configuration
@MapperScan(basePackages = {"com.springboot.provider.module.lis.**.mapper"}, sqlSessionTemplateRef = "lisSqlSessionTemplate")
public class LisDataSourceConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String RESOURCE_NAME = "lisDataSource";

    @Value("${mybatis-plus.lis.mapper-locations}")
    private String[] mapperLocations;

    @Value("${mybatis-plus.type-aliases-package}")
    private String typeAliasesPackage;

    @Value("${spring.jta.atomikos.datasource.lis.xa-data-source-class-name}")
    private String xaDataSourceClassName;

//    @Bean(value = "lisProperties")
//    @ConfigurationProperties(prefix = "spring.datasource.lis")
//    public Properties lisProperties() {
//        return new Properties();
//    }

//    @Bean(name = "lisXADataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.lis")
//    public XADataSource mysqlXADataSource(){
//        return new MysqlXADataSource();
//    }

    @Bean(name = "lisDataSource")
    @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.lis")
    public DataSource lisDataSource(/*@Qualifier("lisProperties") Properties properties*/   /*@Qualifier("lisXADataSource") XADataSource xaDataSource*/) {
        return new AtomikosDataSourceBean();
//        return AtomikosDataSourceBuilder.createAtomikosDataSourceBean(RESOURCE_NAME, xaDataSourceClassName, properties);
//        return AtomikosDataSourceBuilder.createAtomikosDataSourceBean(RESOURCE_NAME, xaDataSourceClassName, xaDataSource);
    }

    @Bean(name = "lisJdbcOperations")
    public JdbcOperations lisJdbcTemplate(@Qualifier("lisDataSource") DataSource dataSource) {
        return JdbcOperationsProxy.getProxyInstance(dataSource);
    }

    @Bean(name = "lisSqlSessionFactory")
    public SqlSessionFactory lisSqlSessionFactory(@Qualifier(value = "lisDataSource") DataSource dataSource) throws Exception {
        logger.info(RESOURCE_NAME + ": is registering...");
        SqlSessionFactory sqlSessionFactory = AtomikosDataSourceBuilder.createSqlSessionFactory(dataSource, typeAliasesPackage, mapperLocations);
        logger.info(RESOURCE_NAME + ": has been registered successfully!");
        return sqlSessionFactory;
    }

    @Bean(name = "lisSqlSessionTemplate")
    public SqlSessionTemplate lisSqlSessionTemplate(@Qualifier("lisSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
