package cn.iocoder.yudao.framework.mybatis.config;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.collection.SetUtils;
import cn.iocoder.yudao.framework.mybatis.core.enums.SqlConstants;
import cn.iocoder.yudao.framework.mybatis.core.util.JdbcUtils;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Set;

/**
 * 当 IdType 为 {@link IdType#NONE} 时，根据 PRIMARY 数据源所使用的数据库，自动设置
 *
 * @author 芋道源码
 */
@Slf4j
public class IdTypeEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /**
     * 该属性key是 mybatis-plus的配置属性，该属性的默认值是
     * com.baomidou.mybatisplus.core.config.GlobalConfig.DbConfig#idType=IdType#ASSIGN_ID
     * <p>
     * E:\programme\mybatis-plus\官方文档\主键策略  IKeyGenerator I MyBatis-Plus.pdf
     * <p>
     * 系统启动的时候会从datasource url配置中解析出数据库类型，并根据数据库类型选择合适的mybatis 的 IdType，然后
     * 更新 com.baomidou.mybatisplus.core.config.GlobalConfig.DbConfig.idType配置属性的值
     *
     *
     *
     * SpringBoot中配置属性的优先级：
     * 1.命令行参数 2.来自java:comp/env的JNDI属性 3.Java系统属性（System.getProperties()）
     * 4.操作系统环境变量 5.RandomValuePropertySource配置的random.*属性值
     * 6.jar包外部的application-{profile}.properties或application.yml(带spring.profile)配置文件
     * 7.jar包内部的application-{profile}.properties或application.yml(带spring.profile)配置文件
     * 8.jar包外部的application.properties或application.yml(不带spring.profile)配置文件
     * 9.jar包内部的application.properties或application.yml(不带spring.profile)配置文件
     * 10.@Configuration注解类上的@PropertySource
     * 11.通过SpringApplication.setDefaultProperties指定的默认属性
     *
     *
     * 3. 外部配置文件：
     * 外部配置文件（如application.properties、application.yml等）提供了一种更灵活的配置方式。Spring Boot支持从外部配置
     * 文件中读取配置属性。外部配置文件的优先级低于命令行参数和环境变量。Spring Boot会按照以下顺序查找并加载外部配置文件：
     * config/ 目录下的application.properties或application.yml
     * 当前目录下的config/目录中的application.properties或application.yml
     * 当前目录下的application.properties或application.yml
     * 类路径下的config/目录中的application.properties或application.yml
     * 类路径下的application.properties或application.yml
     *
     *
     * 在这里 IdTypeEnvironmentPostProcessor 这个EnvironmentPostProcessor的postProcessEnvironment 中会将
     * mybatis-plus.global-config.db-config.id-type属性设置到 SystemProperties中，从而覆盖掉 application.ymal中的
     *
     * 参考E:\programme\SpringBoot\SpringBoot 技术内幕 (Z-Library).pdf
     *
     */
    private static final String ID_TYPE_KEY = "mybatis-plus.global-config.db-config.id-type";

    private static final String DATASOURCE_DYNAMIC_KEY = "spring.datasource.dynamic";

    private static final String QUARTZ_JOB_STORE_DRIVER_KEY = "spring.quartz.properties.org.quartz.jobStore.driverDelegateClass";

    private static final Set<DbType> INPUT_ID_TYPES = SetUtils.asSet(DbType.ORACLE, DbType.ORACLE_12C,
            DbType.POSTGRE_SQL, DbType.KINGBASE_ES, DbType.DB2, DbType.H2);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 如果获取不到 DbType，则不进行处理
        DbType dbType = getDbType(environment);
        if (dbType == null) {
            return;
        }

        // 设置 Quartz JobStore 对应的 Driver
        // TODO 芋艿：暂时没有找到特别合适的地方，先放在这里
        setJobStoreDriverIfPresent(environment, dbType);

        // 初始化 SQL 静态变量
        SqlConstants.init(dbType);

        // 如果非 NONE，则不进行处理
        IdType idType = getIdType(environment);
        if (idType != IdType.NONE) {
            return;
        }
        // 情况一，用户输入 ID，适合 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库
        //Oracle没有这个”auto_increment”属性，所以不能用AUTO,只能用INPUT,底层使用数据库序列实现自增
        //，Oracle里的序列（SEQUENCE），可间接实现自增主键的作用。
        if (INPUT_ID_TYPES.contains(dbType)) {
            setIdType(environment, IdType.INPUT);
            return;
        }
        // 情况二，自增 ID，适合 MySQL 等直接自增的数据库
        setIdType(environment, IdType.AUTO);
    }

    public IdType getIdType(ConfigurableEnvironment environment) {
        return environment.getProperty(ID_TYPE_KEY, IdType.class);
    }

    public void setIdType(ConfigurableEnvironment environment, IdType idType) {
        environment.getSystemProperties().put(ID_TYPE_KEY, idType);
        log.info("[setIdType][修改 MyBatis Plus 的 idType 为({})]", idType);
    }

    public void setJobStoreDriverIfPresent(ConfigurableEnvironment environment, DbType dbType) {
        String driverClass = environment.getProperty(QUARTZ_JOB_STORE_DRIVER_KEY);
        if (StrUtil.isNotEmpty(driverClass)) {
            return;
        }
        // 根据 dbType 类型，获取对应的 driverClass
        switch (dbType) {
            case POSTGRE_SQL:
                driverClass = "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate";
                break;
            case ORACLE:
            case ORACLE_12C:
                driverClass = "org.quartz.impl.jdbcjobstore.oracle.OracleDelegate";
                break;
            case SQL_SERVER:
            case SQL_SERVER2005:
                driverClass = "org.quartz.impl.jdbcjobstore.MSSQLDelegate";
                break;
        }
        // 设置 driverClass 变量
        if (StrUtil.isNotEmpty(driverClass)) {
            environment.getSystemProperties().put(QUARTZ_JOB_STORE_DRIVER_KEY, driverClass);
        }
    }

    public static DbType getDbType(ConfigurableEnvironment environment) {
        String primary = environment.getProperty(DATASOURCE_DYNAMIC_KEY + "." + "primary");
        if (StrUtil.isEmpty(primary)) {
            return null;
        }
        String url = environment.getProperty(DATASOURCE_DYNAMIC_KEY + ".datasource." + primary + ".url");
        if (StrUtil.isEmpty(url)) {
            return null;
        }
        return JdbcUtils.getDbType(url);
    }

}
