package com.heky.mybatisstudy.config.datasoutce;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author grayRainbow
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DatabaseContextHolder.getDbType();
    }
}
