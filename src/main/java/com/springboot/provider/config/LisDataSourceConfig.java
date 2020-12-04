package com.springboot.provider.config;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * @program: bsinterface
 * @package com.bsoft.bsinterface.config
 * @description
 * @author: XuZhenkui
 * @create: 2020-12-03 14:12
 **/
@Configuration
@MapperScan(basePackages = {"com.springboot.provider.mapper.lis"}, sqlSessionTemplateRef = "sqlSessionTemplateLis")
// 扫描dao或mapper接口
public class LisDataSourceConfig {

    @Value("${mybatis.mapper-locations}")
    private String location;

    /**
     * 注入DruidXADataSource，Druid对JTA的支持，支持XA协议，采用两阶段事务的提交
     *
     * @return
     */
    @Bean(value = "druidXADataSourceLis")
    @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.lis")
    public DruidXADataSource druidXADataSourceLis() {
        return new DruidXADataSource();
    }

    @Bean(name = "dataSourceLis")
    public DataSource dataSourceLis(@Qualifier("druidXADataSourceLis") DruidXADataSource dataSource) {
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(dataSource);
        xaDataSource.setMinPoolSize(5);
        xaDataSource.setMaxPoolSize(50);
        xaDataSource.setUniqueResourceName("dataSourceLis");
        return xaDataSource;
    }

    /**
     * Mybatis对多数据源的整合
     *
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "sqlSessionFactoryLis")
    public SqlSessionFactory sqlSessionFactoryLis(@Qualifier(value = "dataSourceLis") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        factory.setConfiguration(configuration);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(location));
        return factory.getObject();
    }

    @Bean(name = "sqlSessionTemplateLis")
    public SqlSessionTemplate sqlSessionTemplateLis(@Qualifier("sqlSessionFactoryLis") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
