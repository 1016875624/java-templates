# mybatsi多数据源设置

#### 1.配置数据源

application.properties

```properties
# 数据源1
spring.datasource1.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource1.username=root
spring.datasource1.password=root
#mysql8.0 可用的连接
# 这里注意一下，不是默认的连接，而是要用datasource 属性里面的jdbc连接
spring.datasource1.jdbcUrl=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT


# 数据源2
spring.datasource2.username=root
spring.datasource2.password=root
spring.datasource2.jdbcUrl=jdbc:mysql://localhost:3306/test1?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT
spring.datasource2.driverClassName=com.mysql.cj.jdbc.Driver
```

#### 2.配置 数据源bean



DatasourceConfig.java

```java
	@Bean(name = "slaveDataSource")
    @Qualifier("slaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource2")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "masterDataSource")
    @Qualifier("masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource1")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }
```

#### 3.创建切换数据源 的工具类

DatabaseContextHolder

```java
/**
 * 动态数据源切换类
 * 切换数据库的时候 直接使用DatabaseContextHolder.setDbType(); 即可切换数据源
 */
public class DatabaseContextHolder {
    // 使用 ThreadLocal 实现线程安全
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
```



#### 4.创建动态数据源类

DynamicDataSource

```java
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
```

#### 5.创建动态数据源 bean

```java
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
```



6.将 mybatis的 sessionfactory 工厂 改为动态数据源

```java
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
```

