package com.springboot.provider.module.pay.enums;

/**
 * @program: springboot-provider
 * @package com.springboot.provider.module.common
 * @description
 * @author: XuZhenkui
 * @create: 2021-01-08 09:12
 **/
public enum PayStrategy {
    UNION("1", "unionPay", "银联支付"),
    WECHAT("2", "wechatPay", "微信支付"),
    ALI("3", "aliPay", "支付宝支付");

    private String key;
    private String value;
    private String remark;

    PayStrategy(String key, String value, String remark) {
        this.key = key;
        this.value = value;
        this.remark = remark;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    //根据key获取value
    public static String getValueByKey(String key) {
        for (PayStrategy strategy : PayStrategy.values()) {
            if (strategy.getKey().equals(key)) {
                return strategy.getValue();
            }
        }
        return null;
    }

    //根据key获取枚举
    public static PayStrategy getEnumByKey(String key) {
        for (PayStrategy strategy : PayStrategy.values()) {
            if (strategy.getKey().equals(key)) {
                return strategy;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "PayStrategy{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", remark='" + remark + '\'' +
                "} " + super.toString();
    }
}
