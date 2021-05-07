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
import java.util.function.Supplier;

public class JdbcOperationsProxy {
    private static final Logger logger = LoggerFactory.getLogger(JdbcOperationsProxy.class);
    private static final ConcurrentHashMap<String, JdbcOperations> JDBC_OPERATIONS_MAP = new ConcurrentHashMap<>();

    public static JdbcOperations getProxyInstance(String dsName) {
        if (JDBC_OPERATIONS_MAP.get(dsName) == null) {
            JDBC_OPERATIONS_MAP.putIfAbsent(dsName, getInstance(dsName));
        }
        return JDBC_OPERATIONS_MAP.get(dsName);
    }

    private static JdbcOperations getInstance(String dsName) {
        DataSource dataSource = MultiDataSourceHolder.getDataSource(dsName);
        Assert.notNull(dataSource, dsName + " datasource is not exists in MultiDataSourceHolder!");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return (JdbcOperations) Proxy.newProxyInstance(JdbcOperations.class.getClassLoader(), new Class<?>[]{JdbcOperations.class}, (proxy, method, args) -> {
            long l = System.currentTimeMillis();
            AtomicReference<String> sql = new AtomicReference<>("");
            Arrays.stream(args).forEach(item -> {
                if (item instanceof String) {
                    sql.set((String) item);
                } else if (item instanceof Object[]) {
                    Arrays.stream(((Object[]) item)).forEach(param -> {
                        sql.updateAndGet(s -> s.replaceFirst("\\?", "'" + param + "'"));
                    });
                }
            });
            Object result = method.invoke(jdbcTemplate, args);
            logger.info("\nJdbcOperations Method: " + method.getName() + "\nSQL: " + sql.accumulateAndGet(";", (s, s2) -> s + s2) + "\nInvoke Cost: " + (System.currentTimeMillis() - l) + " ms");
            return result;
        });
    }

}
