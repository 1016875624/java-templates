package com.heky.mybatisstudy.config.datasoutce;

import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    @Bean
    @Primary
    @Qualifier("master")
    @ConfigurationProperties("spring.datasource1")
    public DataSourceProperties masterDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource2")
    @Qualifier("slave")
    public DataSourceProperties slaveDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "masterDataSource")
    @Qualifier("masterDataSource")
    public DataSource masterDataSource(@Qualifier("master") DataSourceProperties masterDataSourceProperties) {
        return masterDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "slaveDataSource")
    @Qualifier("slaveDataSource")
    public DataSource slaveDataSource(@Qualifier("slave") DataSourceProperties slaveDataSourceProperties) {
        return slaveDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
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
