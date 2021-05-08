package com.springboot.provider.common.proxy;

import com.springboot.provider.common.holder.MultiDataSourceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;

public class JdbcOperationsProxy {
    private static final Logger logger = LoggerFactory.getLogger(JdbcOperationsProxy.class);
    private static final ConcurrentHashMap<String, JdbcOperations> JDBC_OPERATIONS_MAP = new ConcurrentHashMap<>();

    public static JdbcOperations getProxyInstance(String dsName) {
        if (JDBC_OPERATIONS_MAP.get(dsName) == null) {
            JDBC_OPERATIONS_MAP.putIfAbsent(dsName, getProxyInstance(dsName, null));
        }
        return JDBC_OPERATIONS_MAP.get(dsName);
    }

    public static JdbcOperations getProxyInstance(String dsName, DataSource dataSource) {
        Assert.notNull(dsName, "dsName requires non null!");

        if (dataSource == null) {
            dataSource = MultiDataSourceHolder.getDataSource(dsName);
            Assert.notNull(dataSource, dsName + " datasource is not exists in MultiDataSourceHolder!");
        }

        Assert.notNull(dataSource, "datasource requires non null!");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        return getInstance(jdbcTemplate);
    }


    private static JdbcOperations getInstance(JdbcTemplate jdbcTemplate) {
        Assert.notNull(jdbcTemplate, "jdbcTemplate requires non null!");

        return (JdbcOperations) Proxy.newProxyInstance(JdbcOperations.class.getClassLoader(), new Class<?>[]{JdbcOperations.class}, (proxy, method, args) -> {
            long l = System.currentTimeMillis();
            AtomicReference<String> sql = new AtomicReference<>("");

            // implant the args to sql
            Arrays.stream(args).forEach(item -> {
                if (item instanceof String) {
                    sql.set((String) item);
                } else if (item instanceof Object[]) {
                    Arrays.stream(((Object[]) item)).forEach(param -> {
                        sql.updateAndGet(s -> s.replaceFirst("\\?", "'" + Matcher.quoteReplacement(param.toString()) + "'"));
                    });
                }
            });

            Object result = method.invoke(jdbcTemplate, args);

            logger.info("\nJdbcOperations Method: " + method.getName() + "\nSQL: " + sql.accumulateAndGet(";", (s, s2) -> s + s2) + "\nInvoke Cost: " + (System.currentTimeMillis() - l) + " ms");
            return result;
        });
    }

}
