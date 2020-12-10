package com.springboot.provider.common.builder;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @program: springboot-provider
 * @package com.springboot.provider.common.builder
 * @description
 * @author: XuZhenkui
 * @create: 2020-12-10 15:22
 **/
public class AtomikosDataSourceBuilder {

    /**
     * 创建AtomikosDataSourceBean是使用Atomikos连接池的首选类
     */
    public static AtomikosDataSourceBean createAtomikosDataSourceBean(Properties properties) {
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setMinPoolSize(Integer.parseInt(properties.getProperty("min-pool-size")));
        atomikosDataSourceBean.setMaxPoolSize(Integer.parseInt(properties.getProperty("max-pool-size")));
        atomikosDataSourceBean.setMaxLifetime(Integer.parseInt(properties.getProperty("max-lifetime")));
        atomikosDataSourceBean.setTestQuery(properties.getProperty("test-query"));
        atomikosDataSourceBean.setUniqueResourceName(properties.getProperty("name"));

        MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        mysqlXADataSource.setURL(properties.getProperty("url"));
        mysqlXADataSource.setUser(properties.getProperty("username"));
        mysqlXADataSource.setPassword(properties.getProperty("password"));

        atomikosDataSourceBean.setXaDataSource(mysqlXADataSource);
        return atomikosDataSourceBean;
    }

    /**
     * 创建SqlSessionFactory实例
     */
    public static SqlSessionFactory createSqlSessionFactory(DataSource dataSource, String location) throws Exception {
        /**
         * 必须使用MybatisSqlSessionFactoryBean，
         * 不能使用SqlSessionFactoryBean，不然会报invalid bound statement (not found)
         *
         * com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration#sqlSessionFactory(javax.sql.DataSource)
         * 源码中也是使用MybatisSqlSessionFactoryBean
         * 并且源码中使用了@ConditionalOnMissingBean，即IOC中如果存在了SqlSessionFactory实例，mybatis-plus就不创建SqlSessionFactory实例了
         */
        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(location));
        MybatisConfiguration configuration = new MybatisConfiguration();
        sessionFactoryBean.setConfiguration(configuration);
        return sessionFactoryBean.getObject();
    }

}
