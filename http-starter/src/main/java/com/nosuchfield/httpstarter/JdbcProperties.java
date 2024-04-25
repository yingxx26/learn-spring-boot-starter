package com.nosuchfield.httpstarter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hourui 2017/10/10 16:25
 */
@ConfigurationProperties(prefix = "jdbc") // 自动获取配置文件中的属性，把值传入对象参数
@Setter
@Getter
public class JdbcProperties {

    // 如果配置了属性，则该属性会被覆盖
    private String driverClassName = "";

    private String driverUrl = "";

    private String driverUserName = "";

    private String driverPassword = "";

    private String logicTable = "";

    private String actualDataNodes = "";

    private String shardingColumn = "";

    private String mapperLocation = "";

    private String sql = "";

}
