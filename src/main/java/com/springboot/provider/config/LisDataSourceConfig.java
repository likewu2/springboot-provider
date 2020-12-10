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
@MapperScan(basePackages = {"com.springboot.provider.module.lis.**.mapper"}, sqlSessionTemplateRef = "lisSqlSessionTemplate")
public class LisDataSourceConfig {

    @Value("${mybatis-plus.mapper-locations}")
    private String location;

    @Bean(value = "lisProperties")
    @ConfigurationProperties(prefix = "spring.datasource.lis")
    public Properties lisProperties() {
        return new Properties();
    }

    @Bean(name = "lisDataSource")
    public DataSource lisDataSource(@Qualifier("lisProperties") Properties properties) {
        return AtomikosDataSourceBuilder.createAtomikosDataSourceBean(properties);
    }

    @Bean(name = "lisJdbcTemplate")
    public JdbcTemplate lisJdbcTemplate(@Qualifier("lisDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "lisSqlSessionFactory")
    public SqlSessionFactory lisSqlSessionFactory(@Qualifier(value = "lisDataSource") DataSource dataSource) throws Exception {
        return AtomikosDataSourceBuilder.createSqlSessionFactory(dataSource, location);
    }

    @Bean(name = "lisSqlSessionTemplate")
    public SqlSessionTemplate lisSqlSessionTemplate(@Qualifier("lisSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
