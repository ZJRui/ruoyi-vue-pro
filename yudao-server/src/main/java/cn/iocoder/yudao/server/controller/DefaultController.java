package cn.iocoder.yudao.server.controller;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.spring.SpringframeworkBeans;
import cn.iocoder.yudao.module.infra.framework.utils.InfraFrameworkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_IMPLEMENTED;

/**
 * 默认 Controller，解决部分 module 未开启时的 404 提示。
 * 例如说，/bpm/** 路径，工作流
 *
 * @author 芋道源码
 */
@RestController
public class DefaultController {

    /**
     *
     * note: 各个功能模块的启动 只需要在 yudao-server的pom文件中 添加该功能模块的依赖 + 导入该功能模块的sql 就可以开启了。
     *  启动类上配置了自动扫描模块的cn.iocoder.yudao.module包下的类
     *
     */

    @RequestMapping("/admin-api/bpm/**")
    public CommonResult<Boolean> bpm404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[工作流模块 yudao-module-bpm - 已禁用][参考 https://doc.iocoder.cn/bpm/ 开启]");
    }

    @RequestMapping("/admin-api/mp/**")
    public CommonResult<Boolean> mp404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[微信公众号 yudao-module-mp - 已禁用][参考 https://doc.iocoder.cn/mp/build/ 开启]");
    }

    @RequestMapping(value = {"/admin-api/product/**", // 商品中心
            "/admin-api/trade/**", // 交易中心
            "/admin-api/promotion/**"})  // 营销中心
    public CommonResult<Boolean> mall404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[商城系统 yudao-module-mall - 已禁用][参考 https://doc.iocoder.cn/mall/build/ 开启]");
    }

    @RequestMapping(value = {"/admin-api/report/**"})
    public CommonResult<Boolean> report404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[报表模块 yudao-module-report - 已禁用][参考 https://doc.iocoder.cn/report/ 开启]");
    }

    @RequestMapping(value = {"/admin-api/pay/**"})
    public CommonResult<Boolean> pay404() {

        List res = new ArrayList<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = ((RequestMappingHandlerMapping) InfraFrameworkUtils.getBean(SpringframeworkBeans.requestMappingHandlerMapping)).getHandlerMethods();
        handlerMethods.forEach((k, v) -> {

            Set<String> patterns = k.getPatternsCondition().getPatterns();
            patterns.forEach(pattern -> {
                System.out.println(pattern);
                if (pattern.contains("dmin-api/pay/order/page")) {
                    res.add(k.getName());
                }
            });
        });

        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[支付模块 yudao-module-pay - 已禁用][参考 https://doc.iocoder.cn/pay/build/ 开启]");
    }

}
