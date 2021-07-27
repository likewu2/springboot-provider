package com.springboot.provider.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
public class LogAspect {
    private final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(public * com.springboot.provider.module.*.controller.*.*(..))")
    public void log() {}

    @Around("log()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        long l = System.currentTimeMillis();
        //调用 proceed() 方法才会真正的执行实际被代理的方法
        Object result = joinPoint.proceed();

        logger.info("\nRemote Address: {} \nRequest URL: {} \nRequest URI: {} \nParameter: {} \nReturn: {} \nInvoke Cost: {}",
                request.getRemoteAddr(), request.getRequestURL(), request.getRequestURI(), objectMapper.writeValueAsString(joinPoint.getArgs()), objectMapper.writeValueAsString(result), (System.currentTimeMillis() - l) + "ms");

        return result;
    }
}
