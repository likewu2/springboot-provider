package com.springboot.provider.config;

import com.mysql.cj.jdbc.MysqlXADataSource;
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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @program: bsinterface
 * @package com.bsoft.bsinterface.config
 * @description
 * @author: XuZhenkui
 * @create: 2020-12-03 14:12
 **/
@Configuration
@MapperScan(basePackages = {"com.springboot.provider.mapper.his"}, sqlSessionTemplateRef = "hisSqlSessionTemplate")
// 扫描dao或mapper接口
public class HisDataSourceConfig {

    @Value("${mybatis.mapper-locations}")
    private String location;

    /**
     * 注入数据源属性配置
     *
     * @return
     */
    @Bean(value = "hisProperties")
    @ConfigurationProperties(prefix = "spring.datasource.his")
    public Properties hisProperties() {
        return new Properties();
    }

    @Primary
    @Bean(name = "hisDataSource")
    public DataSource hisDataSource(@Qualifier("hisProperties") Properties properties) throws SQLException {
        MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        mysqlXADataSource.setUrl(properties.getProperty("url"));
        mysqlXADataSource.setUser(properties.getProperty("username"));
        mysqlXADataSource.setPassword(properties.getProperty("password"));
        mysqlXADataSource.setPinGlobalTxToPhysicalConnection(true);

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(mysqlXADataSource);
        xaDataSource.setMinPoolSize(5);
        xaDataSource.setMaxPoolSize(50);
        xaDataSource.setUniqueResourceName("hisDataSource");
        return xaDataSource;
    }

    @Bean(name = "hisJdbcTemplate")
    public JdbcTemplate hisJdbcTemplate(@Qualifier("hisDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Mybatis对多数据源的整合
     *
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "hisSqlSessionFactory")
    public SqlSessionFactory hisSqlSessionFactory(@Qualifier(value = "hisDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        factory.setConfiguration(configuration);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(location));
        return factory.getObject();
    }

    @Bean(name = "hisSqlSessionTemplate")
    public SqlSessionTemplate hisSqlSessionTemplate(@Qualifier("hisSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
