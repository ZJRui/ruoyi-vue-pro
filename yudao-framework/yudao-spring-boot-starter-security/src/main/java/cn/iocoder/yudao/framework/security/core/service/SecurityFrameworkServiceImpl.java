package cn.iocoder.yudao.framework.security.core.service;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import lombok.AllArgsConstructor;

import java.util.Arrays;

import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 默认的 {@link SecurityFrameworkService} 实现类
 *
 * @author 芋道源码
 */
@AllArgsConstructor
public class SecurityFrameworkServiceImpl implements SecurityFrameworkService {

    private final PermissionApi permissionApi;

    @Override
    public boolean hasPermission(String permission) {
        /**
         *
         *1.note：该方法的调用轨迹， controller方法上使用 @PreAuthorize("@ss.hasPermission('system:dict:query')")标记，
         *   然后AbstractSecurityInterceptor.attemptAuthorization 进行尝试授权
         * 	at cn.iocoder.yudao.framework.security.core.service.SecurityFrameworkServiceImpl.hasAnyPermissions(SecurityFrameworkServiceImpl.java:30)
         * 	at cn.iocoder.yudao.framework.security.core.service.SecurityFrameworkServiceImpl.hasPermission(SecurityFrameworkServiceImpl.java:25)
         * 	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
         * 	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
         * 	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
         * 	at java.lang.reflect.Method.invoke(Method.java:498)
         * 	at org.springframework.expression.spel.support.ReflectiveMethodExecutor.execute(ReflectiveMethodExecutor.java:139)
         * 	at org.springframework.expression.spel.ast.MethodReference.getValueInternal(MethodReference.java:112)
         * 	at org.springframework.expression.spel.ast.MethodReference.access$000(MethodReference.java:55)
         * 	at org.springframework.expression.spel.ast.MethodReference$MethodValueRef.getValue(MethodReference.java:383)
         * 	at org.springframework.expression.spel.ast.CompoundExpression.getValueInternal(CompoundExpression.java:93)
         * 	at org.springframework.expression.spel.ast.SpelNodeImpl.getTypedValue(SpelNodeImpl.java:119)
         * 	at org.springframework.expression.spel.standard.SpelExpression.getValue(SpelExpression.java:309)
         * 	at org.springframework.security.access.expression.ExpressionUtils.evaluateAsBoolean(ExpressionUtils.java:30)
         * 	at org.springframework.security.access.expression.method.ExpressionBasedPreInvocationAdvice.before(ExpressionBasedPreInvocationAdvice.java:51)
         * 	at org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter.vote(PreInvocationAuthorizationAdviceVoter.java:71)
         * 	at org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter.vote(PreInvocationAuthorizationAdviceVoter.java:42)
         * 	at org.springframework.security.access.vote.AffirmativeBased.decide(AffirmativeBased.java:60)
         * 	at org.springframework.security.access.intercept.AbstractSecurityInterceptor.attemptAuthorization(AbstractSecurityInterceptor.java:239)
         * 	at org.springframework.security.access.intercept.AbstractSecurityInterceptor.beforeInvocation(AbstractSecurityInterceptor.java:208)
         * 	at org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor.invoke(MethodSecurityInterceptor.java:58)
         * 	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
         *
         */
        return hasAnyPermissions(permission);
    }

    @Override
    public boolean hasAnyPermissions(String... permissions) {
        return permissionApi.hasAnyPermissions(getLoginUserId(), permissions);
    }

    @Override
    public boolean hasRole(String role) {
        return hasAnyRoles(role);
    }

    @Override
    public boolean hasAnyRoles(String... roles) {
        return permissionApi.hasAnyRoles(getLoginUserId(), roles);
    }

    @Override
    public boolean hasScope(String scope) {
        return hasAnyScopes(scope);
    }

    @Override
    public boolean hasAnyScopes(String... scope) {
        LoginUser user = SecurityFrameworkUtils.getLoginUser();
        if (user == null) {
            return false;
        }
        return CollUtil.containsAny(user.getScopes(), Arrays.asList(scope));
    }

}
