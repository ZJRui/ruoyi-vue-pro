package cn.iocoder.yudao.module.infra.api.logger;

import cn.iocoder.yudao.module.infra.api.logger.dto.ApiErrorLogCreateReqDTO;
import cn.iocoder.yudao.module.infra.service.logger.ApiErrorLogService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * API 访问日志的 API 接口
 *
 * @author 芋道源码
 */
@Service
/**
 * @validated标注在类上，那么这个类的所有方法都会有该注解。该注解主要用于定义切点，类似于@Transactional
 * 对应的切面是MethodValidationInterceptor。
 *
 */
@Validated
public class ApiErrorLogApiImpl implements ApiErrorLogApi {

    @Resource
    private ApiErrorLogService apiErrorLogService;

    /**
     * createApiErrorLog方法在父类接口上 使用了@Valid注解，
     * question: 这里的createDTO参数会被校验吗？ 子类实现的接口方法 会继承父类的注解吗？不会继承。但是spring会i找到父类方法上的注解
     * @param createDTO 创建信息
     */
    @Override
    public void createApiErrorLog(ApiErrorLogCreateReqDTO createDTO) {
        apiErrorLogService.createApiErrorLog(createDTO);
    }

}
