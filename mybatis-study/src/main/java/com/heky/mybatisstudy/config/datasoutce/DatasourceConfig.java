package com.heky.mybatisstudy.config.datasoutce;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源配置类
 */
@Configuration
public class DatasourceConfig  {

    @Bean(name = "slaveDataSource")
    @Qualifier("slaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource2")
    public DataSource slave() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "masterDataSource")
    @Qualifier("masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource1")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 动态数据源实现方式 在这里可以进行配置动态数据源
     * @param masterDataSource
     * @param slaveDataSource
     * @return
     */
    @Bean
    public AbstractRoutingDataSource routingDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                                       @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        // 配置数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("slave", slaveDataSource);
        targetDataSources.put("master", masterDataSource);

        // 建立 实现切换 功能 的动态数据源对象
        AbstractRoutingDataSource routingDataSource = new DynamicDataSource();

        // 配置所有的数据源
        routingDataSource.setTargetDataSources(targetDataSources);

        // 配置默认的数据源
        routingDataSource.setDefaultTargetDataSource(slaveDataSource);

        return routingDataSource;
    }

    /**
     * 为 mybatis 建立一个 sqlSessionFactory 并且指定数据源 为动态数据源
     * @param routingDataSource 动态数据源
     * @return sqlSessionFactory
     * @throws IOException
     */
    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(AbstractRoutingDataSource routingDataSource) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        // 设置默认的数据源
        factory.setDataSource(routingDataSource);

        // 设置映射
        factory.setMapperLocations(resolver.getResources("classpath*:mapper/*Mapper.xml"));
        return factory;
    }

}
