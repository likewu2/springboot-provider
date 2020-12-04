package com.springboot.provider.common.enums;

/**
 * @program: springboot-dev
 * @package com.spring.development.common.enums
 * @description
 * @author: XuZhenkui
 * @create: 2020-12-02 16:15
 **/
public enum DataSourceEnum {

    MYSQL("MYSQL", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://",
            "?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&useSSL=false"),

    ORACLE("ORACLE", "oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@","");

    private final String dbType;
    private final String driverClassName;
    private final String prefix;
    private final String suffix;

    DataSourceEnum(String dbType, String driverClassName, String prefix, String suffix) {
        this.dbType = dbType;
        this.driverClassName = driverClassName;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getDbType() {
        return dbType;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
}
