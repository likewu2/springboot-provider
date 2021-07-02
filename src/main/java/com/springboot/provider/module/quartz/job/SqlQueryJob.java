package com.springboot.provider.module.quartz.job;

import com.springboot.provider.common.holder.JdbcTemplateHolder;
import com.springboot.provider.common.holder.MultiDataSourceHolder;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @program: springboot-provider
 * @package com.springboot.provider.module.quartz.job
 * @description
 * @author: XuZhenkui
 * @create: 2021-02-23 11:38
 **/
@Component
public class SqlQueryJob extends QuartzJobBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        DataSource dataSource = MultiDataSourceHolder.buildDataSource("mysql", "localhost", "3306", "test", "root", "root", "");
        MultiDataSourceHolder.addDataSource("test", dataSource);
        JdbcTemplateHolder.addJdbcTemplate("test", MultiDataSourceHolder.getDataSource("test"));

        JdbcTemplate test = JdbcTemplateHolder.getJdbcTemplate("test");

        // 获取参数
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        // 业务逻辑 ...
        final String[] sql = {jobDataMap.getString("sql")};
        Map map = (Map) jobDataMap.get("params");
        if (map != null && map.size() > 0) {
            map.forEach((k, v) -> {
                sql[0] = sql[0].replace(k.toString(), v.toString());
            });
        }
        List<Map<String, Object>> maps = test.queryForList(sql[0]);
        System.out.println(maps);
        logger.info("------ SqlQueryJob: 执行SQL: " + sql[0] + "------ ");
    }
}
