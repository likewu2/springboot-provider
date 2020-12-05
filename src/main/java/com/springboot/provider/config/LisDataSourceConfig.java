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
@MapperScan(basePackages = {"com.springboot.provider.mapper.lis"}, sqlSessionTemplateRef = "lisSqlSessionTemplate")
// 扫描dao或mapper接口
public class LisDataSourceConfig {

    @Value("${mybatis.mapper-locations}")
    private String location;

    /**
     * 注入注入数据源属性配置
     *
     * @return
     */
    @Bean(value = "lisProperties")
    @ConfigurationProperties(prefix = "spring.datasource.lis")
    public Properties lisProperties() {
        return new Properties();
    }

    @Bean(name = "lisDataSource")
    public DataSource lisDataSource(@Qualifier("lisProperties") Properties properties) throws SQLException {
        MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        mysqlXADataSource.setUrl(properties.getProperty("url"));
        mysqlXADataSource.setUser(properties.getProperty("username"));
        mysqlXADataSource.setPassword(properties.getProperty("password"));
        mysqlXADataSource.setPinGlobalTxToPhysicalConnection(true);

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(mysqlXADataSource);
        xaDataSource.setMinPoolSize(5);
        xaDataSource.setMaxPoolSize(50);
        xaDataSource.setTestQuery("SELECT 1 FROM DUAL");
        xaDataSource.setUniqueResourceName("lisDataSource");
        return xaDataSource;
    }

    @Bean(name = "lisJdbcTemplate")
    public JdbcTemplate lisJdbcTemplate(@Qualifier("lisDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Mybatis对多数据源的整合
     *
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "lisSqlSessionFactory")
    public SqlSessionFactory lisSqlSessionFactory(@Qualifier(value = "lisDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        factory.setConfiguration(configuration);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(location));
        return factory.getObject();
    }

    @Bean(name = "lisSqlSessionTemplate")
    public SqlSessionTemplate lisSqlSessionTemplate(@Qualifier("lisSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
