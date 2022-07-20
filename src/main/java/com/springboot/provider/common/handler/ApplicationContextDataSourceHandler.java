package com.springboot.provider.common.handler;

import com.springboot.provider.common.holder.ApplicationContextDataSourceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Description
 * @Project springboot-provider
 * @Package com.springboot.provider.common.handler
 * @Author xuzhenkui
 * @Date 2022-07-20 16:18
 */
@Component
public class ApplicationContextDataSourceHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ApplicationContext applicationContext;

    public ApplicationContextDataSourceHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @EventListener
    public void onStateChange(AvailabilityChangeEvent<ReadinessState> event) {
        switch (event.getState()) {
            case ACCEPTING_TRAFFIC:
                logger.info("[ApplicationContextDataSourceHandler ReadinessStateExporter] ACCEPTING_TRAFFIC");

                String[] beanNamesForType = this.applicationContext.getBeanNamesForType(DataSource.class);
                for (String beanName : beanNamesForType) {
                    Object bean = this.applicationContext.getBean(beanName);
                    ApplicationContextDataSourceHolder.addDataSource(beanName, (DataSource) bean);
                }

                logger.info("[ApplicationContextDataSourceHandler ReadinessStateExporter] load context datasource: " + Arrays.toString(beanNamesForType));
                break;
            case REFUSING_TRAFFIC:
                logger.info("[ApplicationContextDataSourceHandler ReadinessStateExporter] REFUSING_TRAFFIC");

                ApplicationContextDataSourceHolder.getDataSourceMap().values().forEach(dataSource -> {
                    logger.info("{} Shutdown initiated...", dataSource);
                    Class<? extends DataSource> clazz = dataSource.getClass();
                    try {
                        Method closeMethod = clazz.getDeclaredMethod("close");
                        closeMethod.invoke(dataSource);
                        logger.info("{} close completed.", dataSource);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        logger.info("{} close failed", dataSource);
                        try {
                            Method closeMethod = clazz.getDeclaredMethod("destroy");
                            closeMethod.invoke(dataSource);
                            logger.info("{} destroy completed.", dataSource);
                        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException invocationTargetException) {
                            logger.info("{} destroy  failed", dataSource);
                        }
                    }
                });

                logger.info("[ApplicationContextDataSourceHandler ReadinessStateExporter] close context datasource: " + ApplicationContextDataSourceHolder.getDataSourceMap().keySet());
                break;
        }
    }
}
