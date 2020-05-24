package com.heky.mybatisstudy.config.datasoutce;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * AbstractRoutingDataSource spring 提供的动态数据源抽象类，通过这个类，我们可以
 * 自由的切换数据源
 * @author grayRainbow
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    /**
     * 通过 返回的key的不同，可以获取到相应的数据源
     * 我们重点就是通过这个 方法来实现切换 数据源
     * 这里我们通过了DatabaseContextHolder 的静态方法来 获取当前的 db
     * 切换 数据源 就只要 调用DatabaseContextHolder 的setDBtype 就可以实现切换数据源
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DatabaseContextHolder.getDbType();
    }
}
