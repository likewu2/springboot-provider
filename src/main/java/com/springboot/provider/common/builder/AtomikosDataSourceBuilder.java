package com.springboot.provider.common.builder;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.*;

/**
 * @program: springboot-provider
 * @package com.springboot.provider.common.builder
 * @description
 * @author: XuZhenkui
 * @create: 2020-12-10 15:22
 **/
public class AtomikosDataSourceBuilder {

    public static final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * 创建AtomikosDataSourceBean是使用Atomikos连接池的首选类
     */
    public static AtomikosDataSourceBean createAtomikosDataSourceBean(String xaDataSourceClassName,
                                                                      Properties properties, String resourceName) {
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setUniqueResourceName(resourceName);
        atomikosDataSourceBean.setXaDataSourceClassName(xaDataSourceClassName);
        atomikosDataSourceBean.setXaProperties(properties);
        return atomikosDataSourceBean;
    }

    /**
     * 创建SqlSessionFactory实例
     */
    public static SqlSessionFactory createSqlSessionFactory(DataSource dataSource, MybatisPlusProperties mybatisPlusProperties, String[] mapperLocations) throws Exception {
        /**
         * 必须使用MybatisSqlSessionFactoryBean，
         * 不能使用SqlSessionFactoryBean，不然会报invalid bound statement (not found)
         *
         * com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration#sqlSessionFactory(javax.sql.DataSource)
         * 源码中也是使用MybatisSqlSessionFactoryBean
         * 并且源码中使用了@ConditionalOnMissingBean，即IOC中如果存在了SqlSessionFactory实例，mybatis-plus就不创建SqlSessionFactory实例了
         */

        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();

        BeanUtils.copyProperties(mybatisPlusProperties, sessionFactoryBean);

        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPlugins(new MybatisPlusInterceptor());

        Set<Resource> resourceSet = new LinkedHashSet(16);
        for (String mapperLocation : mapperLocations) {
            Resource[] resources = resolver.getResources(mapperLocation);
            resourceSet.addAll(Arrays.asList(resources));
        }
        sessionFactoryBean.setMapperLocations(resourceSet.toArray(new Resource[0]));

//        sessionFactoryBean.setTypeAliasesPackage("com.springboot.provider.module.**.entity");
//        sessionFactoryBean.setVfs(SpringBootVFS.class);
//        sessionFactoryBean.setPlugins(new MybatisPlusInterceptor());
//        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(location));
//
//        MybatisConfiguration configuration = new MybatisConfiguration();
//        configuration.setMapUnderscoreToCamelCase(true);
//        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
//
//        sessionFactoryBean.setConfiguration(configuration);

        return sessionFactoryBean.getObject();
    }

}
