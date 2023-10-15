package cn.iocoder.yudao.module.infra.framework.utils;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class InfraFrameworkUtils {

    /**
     * 静态属性
     */
    private static ApplicationContext applicationContext;

    /**
     * beanFactory.autowireBean(new InfraFrameworkUtils());
     * @param applicationContext
     */
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext)  {
        InfraFrameworkUtils.applicationContext = applicationContext;
    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    public static Object getBean(String name) {
        if (Objects.isNull(applicationContext)) {
            return null;
        }
        return applicationContext.getBean(name);
    }

    public static Object getBean(String name, Class<T> requiredType) {
        if (Objects.isNull(applicationContext)) {
            return null;
        }
        return applicationContext.getBean(name, requiredType);
    }

    public static Object getBean(Class<T> requiredType) {
        if (Objects.isNull(applicationContext)) {
            return null;
        }
        return applicationContext.getBean(requiredType);
    }
}
