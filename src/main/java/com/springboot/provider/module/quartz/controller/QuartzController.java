package com.springboot.provider.module.quartz.controller;

import com.springboot.provider.common.ResultCode;
import com.springboot.provider.common.ResultJson;
import com.springboot.provider.module.quartz.entity.QuartzJob;
import com.springboot.provider.module.quartz.service.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private QuartzService quartzService;

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

    @RequestMapping("/test/pause")
    public ResultJson pause(@RequestBody QuartzJob job) {
        Boolean aBoolean = quartzService.pauseJob(job.getJobName(), job.getJobGroupName());
        if (aBoolean) {
            return ResultJson.success();
        } else {
            return ResultJson.failure(ResultCode.BAD_REQUEST);
        }
    }

    @RequestMapping("/test/update")
    public ResultJson update(@RequestBody QuartzJob job) {
        Boolean aBoolean = quartzService.updateJob(job.getJobName(), job.getJobGroupName(), job.getCronExpression());
        if (aBoolean) {
            return ResultJson.success();
        } else {
            return ResultJson.failure(ResultCode.BAD_REQUEST);
        }
    }

    @RequestMapping("/test/resume")
    public ResultJson resume(@RequestBody QuartzJob job) {
        Boolean aBoolean = quartzService.resumeJob(job.getJobName(), job.getJobGroupName());
        if (aBoolean) {
            return ResultJson.success();
        } else {
            return ResultJson.failure(ResultCode.BAD_REQUEST);
        }
    }

    @RequestMapping("/test/runAJobNow")
    public ResultJson runAJobNow(@RequestBody QuartzJob job) {
        Boolean aBoolean = quartzService.runAJobNow(job.getJobName(), job.getJobGroupName());
        if (aBoolean) {
            return ResultJson.success();
        } else {
            return ResultJson.failure(ResultCode.BAD_REQUEST);
        }
    }

    @RequestMapping("/test/delete")
    public ResultJson delete(@RequestBody QuartzJob job) {
        Boolean aBoolean = quartzService.deleteJob(job.getJobName(), job.getJobGroupName());
        if (aBoolean) {
            return ResultJson.success();
        } else {
            return ResultJson.failure(ResultCode.BAD_REQUEST);
        }
    }

    @RequestMapping("/test/queryRunJob")
    public ResultJson queryRunJob(@RequestBody QuartzJob job) {
        return ResultJson.success(quartzService.queryRunJob());
    }

    @RequestMapping("/test/queryAllJob")
    public ResultJson queryAllJob(@RequestBody QuartzJob job) {
        return ResultJson.success(quartzService.queryAllJob());
    }
}
