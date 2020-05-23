package com.heky.mybatisstudy.config.datasoutce;

/**
 * 动态数据源切换类
 * 切换数据库的时候 直接使用DatabaseContextHolder.setDbType(); 即可切换数据源
 */
public class DatabaseContextHolder {
    private static ThreadLocal<String> contextHolder=new ThreadLocal<String>();
    public static void setDbType(String dbType){
        contextHolder.set(dbType);
    }
    public static String getDbType(){
        return contextHolder.get();
    }

    public static void clearDbType(){
        contextHolder.remove();
    }

}
