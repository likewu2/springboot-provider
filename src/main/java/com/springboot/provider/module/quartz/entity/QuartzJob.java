package com.springboot.provider.module.quartz.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * @program: springboot-provider
 * @package com.springboot.provider.module.quartz.entity
 * @description
 * @author: XuZhenkui
 * @create: 2021-02-23 15:27
 **/
public class QuartzJob implements Serializable {
    String jobClass;    // 任务实现类
    String jobName;     // 任务名称(建议唯一)
    String jobGroupName;// 任务组名
    String cronExpression;// 时间表达式 （如：0/5 * * * * ? ）

    Integer jobTime;    // 时间表达式 (这是每隔多少秒为一次任务)
    Integer jobTimes;   // 运行的次数 （<0:表示不限次数）

    Map jobData;        // 参数

    public QuartzJob() {
    }

    public QuartzJob(String jobClass, String jobName, String jobGroupName, String cronExpression, Integer jobTime, Integer jobTimes, Map jobData) {
        this.jobClass = jobClass;
        this.jobName = jobName;
        this.jobGroupName = jobGroupName;
        this.cronExpression = cronExpression;
        this.jobTime = jobTime;
        this.jobTimes = jobTimes;
        this.jobData = jobData;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroupName() {
        return jobGroupName;
    }

    public void setJobGroupName(String jobGroupName) {
        this.jobGroupName = jobGroupName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Integer getJobTime() {
        return jobTime;
    }

    public void setJobTime(Integer jobTime) {
        this.jobTime = jobTime;
    }

    public Integer getJobTimes() {
        return jobTimes;
    }

    public void setJobTimes(Integer jobTimes) {
        this.jobTimes = jobTimes;
    }

    public Map getJobData() {
        return jobData;
    }

    public void setJobData(Map jobData) {
        this.jobData = jobData;
    }

    @Override
    public String toString() {
        return "QuartzJob{" +
                "jobClass='" + jobClass + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobGroupName='" + jobGroupName + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", jobTime=" + jobTime +
                ", jobTimes=" + jobTimes +
                ", jobData=" + jobData +
                '}';
    }
}
