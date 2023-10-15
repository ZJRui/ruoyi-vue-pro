package cn.iocoder.yudao.framework.security.core.aop;

import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Aspect
@Slf4j
public class PreAuthenticatedAspect {
    /**
     * 这个Bean 需要注入到spring容器中
     * （1）通过扫描注入 (当前包路径扫描不到)
     * （2）通过 自动装配的配置类注入
     *
     * 因为当前的类是在使用了@AutoConfiguration注解标记的 YudaoSecurityAutoConfiguration,因此属于通过自动装配的配置类注入
     *
     */




    /**
     * @annotation()表示标注了某个注解的所有方法
     *
     *
     * @param joinPoint
     * @param preAuthenticated
     * @return
     * @throws Throwable
     */
    @Around("@annotation(preAuthenticated)")
    public Object around(ProceedingJoinPoint joinPoint, PreAuthenticated preAuthenticated) throws Throwable {
        if (SecurityFrameworkUtils.getLoginUser() == null) {
            throw exception(UNAUTHORIZED);
        }
        return joinPoint.proceed();
    }

}
