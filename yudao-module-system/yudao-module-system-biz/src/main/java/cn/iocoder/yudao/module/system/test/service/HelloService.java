package cn.iocoder.yudao.module.system.test.service;

import cn.iocoder.yudao.framework.security.core.annotations.TestAopAnno;
import org.springframework.stereotype.Component;

@Component
public class HelloService {

    @TestAopAnno(name = "hello")
    public void sayHello() {
        System.out.println("hello");
    }
}
