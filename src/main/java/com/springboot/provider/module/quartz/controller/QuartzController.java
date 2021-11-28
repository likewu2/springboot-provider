package com.springboot.provider.module.quartz.controller;

import com.springboot.provider.common.ResultCode;
import com.springboot.provider.common.ResultJson;
import com.springboot.provider.module.quartz.entity.QuartzJob;
import com.springboot.provider.module.quartz.service.QuartzService;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: springboot-provider
 * @package com.springboot.provider.module.quartz.controller
 * @description
 * @author: XuZhenkui
 * @create: 2021-02-23 11:30
 **/
@RestController
public class QuartzController {

    private final QuartzService quartzService;

    public QuartzController(QuartzService quartzService) {
        this.quartzService = quartzService;
    }

    /**
     * {
     * "jobClass":"com.springboot.provider.module.quartz.job.SqlQueryJob",
     * "jobName":"job_1",
     * "jobGroupName":"job_group_1",
     * "cronExpression":"0/1 * * * * ?",
     * "jobData":{
     * "sql":"select * from user where status = #status#",
     * "params": {
     * "#status#":"1"
     * }
     * }
     * }
     *
     * @param job
     * @return
     */
    @RequestMapping("/test/start")
    public ResultJson start(@RequestBody QuartzJob job) {
        try {
            Boolean aBoolean = quartzService.addJob((Class<? extends QuartzJobBean>) Class.forName(job.getJobClass()),
                    job.getJobName(), job.getJobGroupName(), job.getCronExpression(), job.getJobData());
            if (aBoolean) {
                return ResultJson.success();
            } else {
                return ResultJson.failure(ResultCode.BAD_REQUEST);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return ResultJson.failure(ResultCode.BAD_REQUEST);
        }
    }

    /**
     * 暂停
     *
     * @param job
     * @return
     */
    @RequestMapping("/test/pause")
    public ResultJson pause(@RequestBody QuartzJob job) {
        Boolean aBoolean = quartzService.pauseJob(job.getJobName(), job.getJobGroupName());
        if (aBoolean) {
            return ResultJson.success();
        } else {
            return ResultJson.failure(ResultCode.BAD_REQUEST);
        }
    }

    /**
     * 修改
     *
     * @param job
     * @return
     */
    @RequestMapping("/test/update")
    public ResultJson update(@RequestBody QuartzJob job) {
        Boolean aBoolean = quartzService.updateJob(job.getJobName(), job.getJobGroupName(), job.getCronExpression());
        if (aBoolean) {
            return ResultJson.success();
        } else {
            return ResultJson.failure(ResultCode.BAD_REQUEST);
        }
    }

    /**
     * 重启
     *
     * @param job
     * @return
     */
    @RequestMapping("/test/resume")
    public ResultJson resume(@RequestBody QuartzJob job) {
        Boolean aBoolean = quartzService.resumeJob(job.getJobName(), job.getJobGroupName());
        if (aBoolean) {
            return ResultJson.success();
        } else {
            return ResultJson.failure(ResultCode.BAD_REQUEST);
        }
    }

    /**
     * 触发一次
     *
     * @param job
     * @return
     */
    @RequestMapping("/test/runAJobNow")
    public ResultJson runAJobNow(@RequestBody QuartzJob job) {
        Boolean aBoolean = quartzService.runAJobNow(job.getJobName(), job.getJobGroupName());
        if (aBoolean) {
            return ResultJson.success();
        } else {
            return ResultJson.failure(ResultCode.BAD_REQUEST);
        }
    }

    /**
     * 删除
     *
     * @param job
     * @return
     */
    @RequestMapping("/test/delete")
    public ResultJson delete(@RequestBody QuartzJob job) {
        Boolean aBoolean = quartzService.deleteJob(job.getJobName(), job.getJobGroupName());
        if (aBoolean) {
            return ResultJson.success();
        } else {
            return ResultJson.failure(ResultCode.BAD_REQUEST);
        }
    }

    /**
     * 所有在运行的定时任务
     *
     * @param job
     * @return
     */
    @RequestMapping("/test/queryRunJob")
    public ResultJson queryRunJob(@RequestBody QuartzJob job) {
        return ResultJson.success(quartzService.queryRunJob());
    }

    /**
     * 所有定时任务
     *
     * @param job
     * @return
     */
    @RequestMapping("/test/queryAllJob")
    public ResultJson queryAllJob(@RequestBody QuartzJob job) {
        return ResultJson.success(quartzService.queryAllJob());
    }
}
