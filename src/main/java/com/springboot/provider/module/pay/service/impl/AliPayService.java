package com.springboot.provider.module.pay.service.impl;

import com.springboot.provider.module.pay.enums.PayStrategy;
import com.springboot.provider.module.pay.factory.PayStrategyFactory;
import com.springboot.provider.module.pay.service.PayService;
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
public class AliPayService implements PayService {

    @PostConstruct
    public void init(){
        PayStrategyFactory.register(PayStrategy.ALI, this);
    }

    @Override
    public void pay() {
        System.out.println("PayEnum.ALI = " + PayStrategy.ALI);
    }
}
