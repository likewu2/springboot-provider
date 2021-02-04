package com.springboot.provider.module.common.controller;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.springboot.provider.common.ResultJson;
import com.springboot.provider.common.event.ApplicationMessageEvent;
import com.springboot.provider.common.event.ApplicationNotifyEvent;
import com.springboot.provider.common.holder.*;
import com.springboot.provider.module.common.service.CommonService;
import com.springboot.provider.module.his.entity.User;
import com.springboot.provider.module.pay.enums.PayStrategy;
import com.springboot.provider.module.pay.factory.PayStrategyFactory;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class CommonController {

    private final AtomicLong counter = new AtomicLong();

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private CommonService commonService;

    /*
    * @cache（“something");这个相当于save（）操作，
    * @cachePut相当于Update（）操作，只要他标示的方法被调用，那么都会缓存起来，而@cache则是先看下有没已经缓存了，然后再选择是否执行方法。
    * @CacheEvict相当于Delete（）操作。用来清除缓存用的。
    * */

    @Cacheable("index")
    @RequestMapping("/")
    public ResultJson index(){
        User user = new User();
        user.setUsername("spring boot");
        user.setPassword((String.valueOf(counter.incrementAndGet())));
        try {
            System.out.println("servletContext.getContextPath() = " + servletContext.getContextPath());
            System.out.println(ResourceLoaderHolder.getLoader().getResource("application.properties").getFile());
            System.out.println("EnvironmentHolder.getEnvironment().getProperty(\"server.port\") = " + EnvironmentHolder.getEnvironment().getProperty("server.port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultJson.success(user);
    }

    @RequestMapping("/user")
    public ResultJson user(){
        ApplicationEventPublisherHolder.publishEvent(new ApplicationNotifyEvent(counter,true));
        applicationEventPublisher.publishEvent(new ApplicationNotifyEvent(counter,true));
        applicationEventPublisher.publishEvent(new ApplicationMessageEvent("用户消息发送成功!"));
        return ResultJson.success("Greetings user from Spring Boot! " + counter.incrementAndGet());
    }

    @RequestMapping("/admin")
    public ResultJson admin(){
        return ResultJson.success("Greetings admin from Spring Boot! " + counter.incrementAndGet());
    }

    @RequestMapping("/test/addDataSource")
    public ResultJson addDataSource(){
        DataSource build = DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/development?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&useSSL=false")
                .username("root")
                .password("root").build();

        DataSource dataSource = MultiDataSourceHolder.buildDataSource("mysql", "localhost", "3306", "development", "root", "root", "");

        return ResultJson.success(MultiDataSourceHolder.addDataSource("development", dataSource));
    }

    @RequestMapping("/test/getRunningSQL")
    public ResultJson getRunningSQL(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(Objects.requireNonNull(MultiDataSourceHolder.getDataSource("development")));
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * FROM information_schema.processlist WHERE STATE = 'Sending data'");
        return ResultJson.success(maps);
    }

    @RequestMapping("/test/getDataSource")
    public ResultJson getFromDataSource(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(Objects.requireNonNull(MultiDataSourceHolder.getDataSource("development")));
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from role");
        return ResultJson.success(maps);
    }

    @RequestMapping("/test/insert")
    public ResultJson insert(){
        return ResultJson.success(commonService.insert());
    }

    @RequestMapping("/test/deleteById/{id}")
    public ResultJson deleteById(@PathVariable("id") Long id){
        return ResultJson.success(commonService.deleteById(id));
    }

    @RequestMapping("/test/selectAll")
    public List<User> selectAll(){
        return commonService.selectAll();
    }

    @RequestMapping("/test/selectById/{id}")
    public ResultJson selectById(@PathVariable("id") Long id){
        return ResultJson.success(commonService.selectById(id));
    }

    @RequestMapping("/test/selectByUsername/{username}")
    public ResultJson selectByUsername(@PathVariable("username") String username){
        return ResultJson.success(commonService.selectByUsername(username));
    }

    @RequestMapping("/test/pay/{type}")
    public String pay(@PathVariable("type") String type){
        PayStrategyFactory.get(PayStrategy.getEnumByKey(type)).pay();
        return Objects.requireNonNull(PayStrategy.getEnumByKey(type)).toString();
    }

    @RequestMapping("/test/async")
    public String async(){

        ListenableFuture<Object> submit = CallbackThreadPoolExecutorHolder.getThreadPoolExecutor().submit(() -> {
            try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
            System.out.println(LocalDateTime.now());
            throw new RuntimeException("error");
        });

        Futures.addCallback(submit, new FutureCallback<Object>() {
            @Override
            public void onSuccess(@Nullable Object result) {
                System.out.println("result = " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("t.getMessage() = " + t.getMessage());
            }
        }, MoreExecutors.directExecutor());

        return "success";
    }
}
