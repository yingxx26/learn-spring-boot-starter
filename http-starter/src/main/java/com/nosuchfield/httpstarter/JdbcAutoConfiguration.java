package com.nosuchfield.httpstarter;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.statement.PreciseShardingTableAlgorithm;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.statement.RangeShardingTableAlgorithm;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author hourui 2017/10/10 16:29
 */
@Configuration
@EnableConfigurationProperties(JdbcProperties.class)
public class JdbcAutoConfiguration {

    @Resource
    private JdbcProperties properties;

    // 通过一定的手段或条件来创建bean，然后放入表中，之后从表中获取bean

    @Bean
    @Qualifier("shardingSqlSession")
    //@ConditionalOnMissingBean
    public SqlSession init() throws Exception {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        // 配置第一个数据源
        BasicDataSource dataSource1 = new BasicDataSource();
        dataSource1.setDriverClassName(properties.getDriverClassName());
        dataSource1.setUrl(properties.getDriverUrl());
        dataSource1.setUsername(properties.getDriverUserName());
        dataSource1.setPassword(properties.getDriverPassword());
        dataSourceMap.put("ds00", dataSource1);

        // 配置Order表规则
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration(properties.getLogicTable(), properties.getActualDataNodes());

        // 配置分库 + 分表策略
        orderTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration(properties.getShardingColumn(),
                new PreciseShardingTableAlgorithm(), new RangeShardingTableAlgorithm()));

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);

        // 获取数据源对象
        DataSource dataSource2 = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new Properties());


        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource2);
        // 配置Mapper扫描路径
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(properties.getMapperLocation()));
        SqlSessionFactory sqlSessionFactory=sqlSessionFactoryBean.getObject();
        SqlSession sqlSession=sqlSessionFactory.openSession();
        return  sqlSession;
    }

}
