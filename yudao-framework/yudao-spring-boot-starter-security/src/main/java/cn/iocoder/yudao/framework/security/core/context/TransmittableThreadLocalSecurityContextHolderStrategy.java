package cn.iocoder.yudao.framework.security.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.util.Assert;

/**
 * 基于 TransmittableThreadLocal 实现的 Security Context 持有者策略
 * 目的是，避免 @Async 等异步执行时，原生 ThreadLocal 的丢失问题
 *
 * @author 芋道源码
 */
public class TransmittableThreadLocalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {
    /**
     *
     * 1.Spring-Security是如何保存登录信息的
     * SecurityContextPersistenceFilter 它的作用是为了存储SecurityContext而设计的。
     *  当 一 个 请 求 到 来 时 ， 从 HttpSession 中 获 取
     * SecurityContext并存入SecurityContext Holder中
     * 当一个请求处理完毕时，从SecurityContextHolder中获
     * 取 SecurityContext 并 存 入 HttpSession 中
     *
     * SecurityContextRepository:将SecurityContext存入HttpSession，或者从HttpSession中
     * 加 载 数 据 并 转 为 Security  Context 对 象
     *
     *
     * 2.sprin-security的用户登录信息存在ThreadLocal中，对于异步任务异步请求如何获取用户信息？
     * SpringBoot 2.X 异步处理@Async 拿不到 SpringSecurity 认证信息问题 解决方案
     *
     */

    /**
     * 使用 TransmittableThreadLocal 作为上下文
     */
    private static final ThreadLocal<SecurityContext> CONTEXT_HOLDER = new TransmittableThreadLocal<>();

    @Override
    public void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    @Override
    public SecurityContext getContext() {
        SecurityContext ctx = CONTEXT_HOLDER.get();
        if (ctx == null) {
            ctx = createEmptyContext();
            CONTEXT_HOLDER.set(ctx);
        }
        return ctx;
    }

    @Override
    public void setContext(SecurityContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        CONTEXT_HOLDER.set(context);
    }

    @Override
    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }

}
