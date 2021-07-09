package com.springboot.provider.module.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.common.util.concurrent.*;
import com.springboot.provider.common.ResultCode;
import com.springboot.provider.common.ResultJson;
import com.springboot.provider.common.event.ApplicationMessageEvent;
import com.springboot.provider.common.event.ApplicationNotifyEvent;
import com.springboot.provider.common.holder.*;
import com.springboot.provider.common.proxy.JdbcOperationsProxy;
import com.springboot.provider.common.utils.ResourceUtils;
import com.springboot.provider.common.utils.PropertyUtils;
import com.springboot.provider.module.common.service.CommonService;
import com.springboot.provider.module.his.entity.User;
import com.springboot.provider.module.lis.entity.Role;
import com.springboot.provider.module.pay.enums.PayStrategy;
import com.springboot.provider.module.pay.factory.PayStrategyFactory;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.websocket.server.PathParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class CommonController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RateLimiter rateLimiter = RateLimiter.create(5.0);

    private final BloomFilter bloomFilter = BloomFilter.create(Funnels.integerFunnel(), 100);


    private final AtomicLong counter = new AtomicLong();

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private CommonService commonService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /*
     * @cache（“something");这个相当于save（）操作，
     * @cachePut相当于Update（）操作，只要他标示的方法被调用，那么都会缓存起来，而@cache则是先看下有没已经缓存了，然后再选择是否执行方法。
     * @CacheEvict相当于Delete（）操作。用来清除缓存用的。
     * */

    @Cacheable("index")
    @RequestMapping("/")
    public ResultJson index() {
        User user = new User();
        user.setUsername("spring boot");
        user.setPassword((String.valueOf(counter.incrementAndGet())));
        try {
            logger.info("servletContext.getContextPath() = " + servletContext.getContextPath());
            logger.info(ResourceLoaderHolder.getLoader().getResource("application.properties").getFile().toString());
            logger.info("EnvironmentHolder.getEnvironment().getProperty(\"server.port\") = " + EnvironmentHolder.getEnvironment().getProperty("server.port"));

            logger.info("loadProperties: " + PropertyUtils.loadProperties("application.properties").getProperty("context.initializer.classes"));
            logger.info("loadProperties: " + PropertyUtils.loadAbsolutePathProperties("D:\\IdeaProjects\\development\\src\\main\\resources\\application.properties").getProperty("server.port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return ResultJson.success(user);
        String getCost = ResourceUtils.getResource(null, "db/quartz_mysql.sql");
        return ResultJson.success(getCost);
    }

    @RequestMapping("/user")
    public ResultJson user() {
        ApplicationEventPublisherHolder.publishEvent(new ApplicationNotifyEvent(counter, true));
        applicationEventPublisher.publishEvent(new ApplicationNotifyEvent(counter, true));
        applicationEventPublisher.publishEvent(new ApplicationMessageEvent("用户消息发送成功!"));
        return ResultJson.success("Greetings user from Spring Boot! " + counter.incrementAndGet());
    }

    @RequestMapping("/admin")
    public ResultJson admin() {
        return ResultJson.success("Greetings admin from Spring Boot! " + counter.incrementAndGet());
    }

    @RequestMapping("/test/addDataSource")
    public ResultJson addDataSource() {
        DataSource build = DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/development?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&useSSL=false")
                .username("root")
                .password("root").build();

        DataSource build1 = MultiDataSourceHolder.builder()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .jdbcUrl("jdbc:mysql://localhost:3306/development?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&useSSL=false")
                .username("root")
                .password("root").build();


        DataSource dataSource = MultiDataSourceHolder.buildDataSource("mysql", "localhost", "3306", "development", "root", "root", "");

        return ResultJson.success(MultiDataSourceHolder.addDataSource("development", dataSource));
    }

    @RequestMapping("/test/getRunningSQL")
    public ResultJson getRunningSQL() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(Objects.requireNonNull(MultiDataSourceHolder.getDataSource("development")));
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * FROM information_schema.processlist WHERE STATE = 'Sending data'");
        return ResultJson.success(maps);
    }

    @RequestMapping("/test/getDataSource")
    public ResultJson getFromDataSource() {
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(Objects.requireNonNull(MultiDataSourceHolder.getDataSource("development")));
        JdbcOperations jdbcTemplate = JdbcOperationsProxy.getProxyInstance("development");

        RowMapper<Role> rowMapper = new BeanPropertyRowMapper<>(Role.class);
//        List<Role> roles = jdbcTemplate.query("select * from role", rowMapper);
        List<Role> roles = jdbcTemplate.query("select * from role where id = ? and title = ?", rowMapper, 1, "超级管理员");

//        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from role");
//        List<String> nameList = jdbcTemplate.queryForList("select name from role", String.class);
//        List<String> nameList = jdbcTemplate.queryForList("select name from role where id >= ?", String.class, 9);

//        String name = jdbcTemplate.queryForObject("select name from role where id = ?", String.class, 1);


        return ResultJson.success(roles);
    }

    @RequestMapping("/test/insert")
    public ResultJson insert() {
        return ResultJson.success(commonService.insert());
    }

    @RequestMapping("/test/deleteById/{id}")
    public ResultJson deleteById(@PathVariable("id") Long id) {
        return ResultJson.success(commonService.deleteById(id));
    }

    @RequestMapping("/test/selectAll")
    public List<User> selectAll() {
        return commonService.selectAll();
    }

    @RequestMapping("/test/selectById/{id}")
    public ResultJson selectById(@PathVariable("id") Long id) {
        return ResultJson.success(commonService.selectById(id));
    }

    @RequestMapping("/test/selectByUsername/{username}")
    public ResultJson selectByUsername(@PathVariable("username") String username) {
        return ResultJson.success(commonService.selectByUsername(username));
    }

    @RequestMapping("/test/pay/{type}")
    public String payPath(@PathVariable("type") String type) {
        PayStrategyFactory.get(PayStrategy.getEnumByKey(type)).pay();
        return Objects.requireNonNull(PayStrategy.getEnumByKey(type)).toString();
    }

    @RequestMapping("/test/pay")
    public String payForm(@PathParam("type") String type) {
        PayStrategyFactory.get(PayStrategy.getEnumByKey(type)).pay();
        return Objects.requireNonNull(PayStrategy.getEnumByKey(type)).toString();
    }

    @RequestMapping(value = "/test/xml", method = RequestMethod.POST, produces = {"application/xml;charset=utf-8"})
    public String xml(@RequestBody String xml) {
        return xml;
    }

    @RequestMapping("/test/async")
    public String async() {

        ListenableFuture<Object> submit = CallbackThreadPoolExecutorHolder.getThreadPoolExecutor().submit(() -> {
            logger.info("submit: " + Thread.currentThread().getName());
//            try {
//                TimeUnit.SECONDS.sleep(3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            logger.info(LocalDateTime.now().toString());
//            throw new RuntimeException("error");

            ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("http://baidu.com", null, String.class);
            return stringResponseEntity;
        });

        Futures.addCallback(submit, new FutureCallback<Object>() {
            @Override
            public void onSuccess(@Nullable Object result) {
                logger.info("onSuccess: " + Thread.currentThread().getName());
                logger.info("result = " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.info("onFailure: " + Thread.currentThread().getName());
                logger.info("t.getMessage() = " + t.getMessage());
            }
        }, MoreExecutors.directExecutor());

        return "success";
    }

    @RequestMapping(value = "/test/limit/{id}")
    public ResultJson limit(@PathVariable String id) {
        if (rateLimiter.tryAcquire(1)) {
            boolean b = bloomFilter.mightContain(1);
            return ResultJson.success("consume: " + id + " success, Container might contain 1: " + b);
        }
        return ResultJson.failure(ResultCode.SERVICE_UNAVAILABLE, "access too frequently");
    }

    @RequestMapping(value = "/test/getJson")
    public void getJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取 post 请求中 json 数据
        String param = null;
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        streamReader.close();

        User user = new User();
        user.setId(1L);
        user.setUsername("许振奎");
        user.setPassword(responseStrBuilder.toString());

        // 返回 json 格式的数据
        response.setContentType("application/json; charset=UTF-8");
        assert param != null;
        response.getWriter().write(objectMapper.writeValueAsString(user));
    }
}
