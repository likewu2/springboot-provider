package com.springboot.provider.module.pay.service.impl;

import com.springboot.provider.module.pay.enums.PayStrategy;
import com.springboot.provider.module.pay.factory.PayStrategyFactory;
import com.springboot.provider.module.pay.service.PayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @program: springboot-provider
 * @package com.springboot.provider.module.common.service.impl
 * @description
 * @author: XuZhenkui
 * @create: 2021-01-08 09:27
 **/
@Service
public class WechatPayService implements PayService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

//    @PostConstruct
//    public void init(){
//        PayStrategyFactory.register(PayStrategy.WECHAT, this);
//    }

    public WechatPayService(){
        PayStrategyFactory.register(PayStrategy.WECHAT, this);
    }

    @Override
    public void pay() {
        logger.info("PayEnum.WECHAT = " + PayStrategy.WECHAT);
    }
}
