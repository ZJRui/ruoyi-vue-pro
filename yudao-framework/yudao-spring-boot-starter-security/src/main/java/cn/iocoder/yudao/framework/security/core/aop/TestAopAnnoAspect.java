package cn.iocoder.yudao.framework.security.core.aop;


import cn.iocoder.yudao.framework.security.core.annotations.TestAopAnno;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TestAopAnnoAspect {

    public TestAopAnnoAspect() {
        System.out.println("TestAopAnnoAspect");
    }

    /**
     * 第一种切点，不关心方法上的注解的配置属性
     */
    @Pointcut("@annotation(cn.iocoder.yudao.framework.security.core.annotations.TestAopAnno)")
    public void testAopAnnoPointcutA() {
    }

    @Before("testAopAnnoPointcutA()")
    public void testAopAnnoBeforeA() {
        System.out.println("testAopAnnoBeforeA");
    }


    /**
     * 第二种切点，关心方法上的注解的配置属性，能够在切面中获取到
     *
     * @param testAopAnno
     */
    @Pointcut("@annotation(testAopAnno)")
    public void testAopAnnoPointcutB(TestAopAnno testAopAnno) {

    }

    @Before("testAopAnnoPointcutB(testAopAnno)")
    public void testAopAnnoBeforeB(TestAopAnno testAopAnno) {

        System.out.println("testAopAnnoBeforeB" + testAopAnno.toString());
    }

    /**
     * 第三种方案
     */
    @Before("@annotation(testAopAnno)")
    public void testAopAnnoBeforeC(TestAopAnno testAopAnno) {
        System.out.println("testAopAnnoBeforeC" + testAopAnno.toString());
    }


}
