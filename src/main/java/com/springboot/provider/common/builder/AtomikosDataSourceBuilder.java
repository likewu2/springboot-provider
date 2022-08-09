package com.springboot.provider.common.builder;

import cn.hutool.core.net.NetUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.springboot.provider.common.handler.MyBatisMetaObjectHandler;
import com.springboot.provider.common.interceptor.DataScopeInterceptor;
import com.springboot.provider.common.interceptor.EasySqlInjector;
import com.springboot.provider.common.interceptor.PerformanceInterceptor;
import com.springboot.provider.common.interceptor.SensitiveInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

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
    public static AtomikosDataSourceBean createAtomikosDataSourceBean(String resourceName, String xaDataSourceClassName,
                                                                      XADataSource xaDataSource) {
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setUniqueResourceName(resourceName);
        atomikosDataSourceBean.setXaDataSourceClassName(xaDataSourceClassName);
        atomikosDataSourceBean.setXaDataSource(xaDataSource);
        return atomikosDataSourceBean;
    }

    /**
     * 创建AtomikosDataSourceBean是使用Atomikos连接池的首选类
     */
    public static AtomikosDataSourceBean createAtomikosDataSourceBean(String resourceName, String xaDataSourceClassName,
                                                                      Properties properties) {
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setUniqueResourceName(resourceName);
        atomikosDataSourceBean.setXaDataSourceClassName(xaDataSourceClassName);
        atomikosDataSourceBean.setXaProperties(properties);
        return atomikosDataSourceBean;
    }

    /**
     * 创建SqlSessionFactory实例
     * @param dataSource XA 数据源
     * @param typeAliasesPackage 包路径
     * @param mapperLocations mappedStatement XML 文件路径
     * @return
     * @throws Exception
     */
    public static SqlSessionFactory createSqlSessionFactory(DataSource dataSource, String typeAliasesPackage, String[] mapperLocations) throws Exception {
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
        sessionFactoryBean.setVfs(SpringBootVFS.class);
        sessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);

        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 分页拦截器
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor()); // 乐观锁
        mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor()); // 攻击 SQL 阻断解析器,防止全表更新与删除
        sessionFactoryBean.setPlugins(mybatisPlusInterceptor, new DataScopeInterceptor(dataSource),
                new PerformanceInterceptor(dataSource), new SensitiveInterceptor(dataSource));

        Resource[] resources = resolveMapperLocations(mapperLocations);
        sessionFactoryBean.setMapperLocations(resources);

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
        GlobalConfigUtils.getGlobalConfig(configuration).setDbConfig(new GlobalConfig.DbConfig());
        GlobalConfigUtils.getGlobalConfig(configuration).setIdentifierGenerator(new DefaultIdentifierGenerator(NetUtil.getLocalhost()));
        GlobalConfigUtils.getGlobalConfig(configuration).setSqlInjector(new EasySqlInjector());
        GlobalConfigUtils.getGlobalConfig(configuration).setMetaObjectHandler(new MyBatisMetaObjectHandler());
        sessionFactoryBean.setConfiguration(configuration);

        return sessionFactoryBean.getObject();
    }

    public static Resource[] resolveMapperLocations(String[] baseLocations) {
        return Stream.of(Optional.ofNullable(baseLocations).orElse(new String[0]))
                .flatMap(location -> Stream.of(getResources(location))).toArray(Resource[]::new);
    }

    private static Resource[] getResources(String location) {
        try {
            return resolver.getResources(location);
        } catch (IOException e) {
            return new Resource[0];
        }
    }

}
