package com.springboot.provider.common.aspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
public class LogAspect {
    private Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(public * com.springboot.provider.module.*.controller.*.*(..))")
    public void log(){}

    @Around("log()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        long startTimeMillis = System.currentTimeMillis();
        //调用 proceed() 方法才会真正的执行实际被代理的方法
        Object result = joinPoint.proceed();

        long consultTime = System.currentTimeMillis() - startTimeMillis;
        logger.info("\n请求URL: "+request.getRequestURL()+"\n请求path: "+request.getRequestURI()+"\n入参:"+ Arrays.toString(joinPoint.getArgs()) +"\n出参:"+ JSON.toJSONString(result) +"\n执行时间: "+ consultTime +" 毫秒");

        return result;
    }
}
