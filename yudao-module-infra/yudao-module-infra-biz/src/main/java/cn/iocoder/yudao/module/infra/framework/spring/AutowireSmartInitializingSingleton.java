package cn.iocoder.yudao.module.infra.framework.spring;

import cn.iocoder.yudao.module.infra.framework.utils.InfraFrameworkUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;


@Component
public class AutowireSmartInitializingSingleton implements SmartInitializingSingleton {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    /**
     * 实现SmartInitializingSingleton的接口后，当所有单例 bean 都
     * 初始化完成以后， Spring的IOC容器会回调该接口的 afterSingletonsInstantiated()方法。
     */
    @Override
    public void afterSingletonsInstantiated() {
        /**
         * 对工具类的set属性Autowired，工具类对象只会进行autowire，不会进行register，因此容器中无法根据名称/类型得到工具类
         */
        beanFactory.autowireBean(new InfraFrameworkUtils());
    }
}
