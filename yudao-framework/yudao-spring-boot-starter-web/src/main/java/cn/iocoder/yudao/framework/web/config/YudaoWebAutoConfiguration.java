package cn.iocoder.yudao.framework.web.config;

import cn.iocoder.yudao.framework.apilog.core.service.ApiErrorLogFrameworkService;
import cn.iocoder.yudao.framework.common.enums.WebFilterOrderEnum;
import cn.iocoder.yudao.framework.web.core.filter.CacheRequestBodyFilter;
import cn.iocoder.yudao.framework.web.core.filter.DemoFilter;
import cn.iocoder.yudao.framework.web.core.handler.GlobalExceptionHandler;
import cn.iocoder.yudao.framework.web.core.handler.GlobalResponseBodyHandler;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import javax.servlet.Filter;

@AutoConfiguration
@EnableConfigurationProperties(WebProperties.class)
@SuppressWarnings("all")
public class YudaoWebAutoConfiguration implements WebMvcConfigurer {

    @Resource
    private WebProperties webProperties;
    /**
     * 应用名
     */
    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        /**
         * configurePathMatch:帮助配置HandlerMapping路径匹配选项，例如是否使用解析的PathPatterns或与PathMatcher匹配的字符串模式，是否匹配尾部斜杠等。
         * **.controller.admin.** 包下的controller 添加/admin-api前缀
         * **.controller.app.** 包下的controller 添加/app-api前缀
         *
         * 给Controller的RequestMapping添加统一前缀
         */
        configurePathMatch(configurer, webProperties.getAdminApi());
        configurePathMatch(configurer, webProperties.getAppApi());
        // configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class))
    }

    /**
     * 设置 API 前缀，仅仅匹配 controller 包下的
     *
     * @param configurer 配置
     * @param api        API 配置
     */
    private void configurePathMatch(PathMatchConfigurer configurer, WebProperties.Api api) {
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
        /**
         * addPathPrefix:配置路径前缀以应用于匹配的控制器方法。前缀用于丰富控制器类型与相应谓词匹配的每个@RequestMapping方法的映射。使用第一个匹配谓词的前缀。
         *
         * 就是说addPathPrefix 用来添加前缀。比如说你可能有需求给 admin包下的所有的controller添加/admin前缀，
         * webapp包下的所有的controller添加/webapp前缀，那么你就可以使用addPathPrefix来实现这个需求。
         * addPathPrefix的第二个参数是一个Predicate，predicate的test入参是当前的controller class，你可以判断当前的这个controllerclass是否需要添加
         * 前缀，如果需要添加前缀，那么就返回true，否则返回false。 这种springboot项目在所有的controller里加上统一前缀的需求是很常见的，
         * 这种处理方式 不需要我们手动为每一个controller添加前缀
         */
        configurer.addPathPrefix(api.getPrefix(), clazz -> clazz.isAnnotationPresent(RestController.class)
                && antPathMatcher.match(api.getController(), clazz.getPackage().getName())); // 仅仅匹配 controller 包
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler(ApiErrorLogFrameworkService ApiErrorLogFrameworkService) {
        return new GlobalExceptionHandler(applicationName, ApiErrorLogFrameworkService);
    }

    @Bean
    public GlobalResponseBodyHandler globalResponseBodyHandler() {
        return new GlobalResponseBodyHandler();
    }

    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public WebFrameworkUtils webFrameworkUtils(WebProperties webProperties) {
        // 由于 WebFrameworkUtils 需要使用到 webProperties 属性，所以注册为一个 Bean
        return new WebFrameworkUtils(webProperties);
    }

    // ========== Filter 相关 ==========

    /**
     * 创建 CorsFilter Bean，解决跨域问题
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        // 创建 CorsConfiguration 对象
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 设置访问源地址
        config.addAllowedHeader("*"); // 设置访问源请求头
        config.addAllowedMethod("*"); // 设置访问源请求方法
        // 创建 UrlBasedCorsConfigurationSource 对象
        /**
         * UrlBasedCorsConfigurationSource 是 Spring 框架提供的一种基于 URL 的跨域资源共享（CORS）配置源，
         * 它可以帮助我们轻松地配置跨域资源访问策略。CORS 是一种机制，它允许网页或 Web 应用从不同的域名访问其资源。
         *
         * UrlBasedCorsConfigurationSource 可以通过配置 URL 来定义跨域资源访问策略，我们可以设定哪些域名能够进行访问、
         * 允许哪些 HTTP 方法、是否允许携带身份凭证等。在配置完策略后，我们只需要将 UrlBasedCorsConfigurationSource 对象与
         * CorsFilter 过滤器关联起来，就可以实现跨域资源访问控制了
         */
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 对接口配置跨域设置
        return createFilterBean(new CorsFilter(source), WebFilterOrderEnum.CORS_FILTER);
    }

    /**
     * 创建 RequestBodyCacheFilter Bean，可重复读取请求内容
     */
    @Bean
    public FilterRegistrationBean<CacheRequestBodyFilter> requestBodyCacheFilter() {
        return createFilterBean(new CacheRequestBodyFilter(), WebFilterOrderEnum.REQUEST_BODY_CACHE_FILTER);
    }

    /**
     * 创建 DemoFilter Bean，演示模式
     *
     * havingValue: 在这个注解中，name 属性指定了要检查的配置属性的名称，havingValue 属性指定了该属性的期望值。如
     * 果配置文件中的 property.name 属性的值与 havingValue 属性指定的值相等，条件就会满足，相应的 Bean 或者配置类就会被装配。
     *
     */
    @Bean
    @ConditionalOnProperty(value = "yudao.demo", havingValue = "true")
    public FilterRegistrationBean<DemoFilter> demoFilter() {
        return createFilterBean(new DemoFilter(), WebFilterOrderEnum.DEMO_FILTER);
    }

    public static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }

    /**
     * 创建 RestTemplate 实例
     *
     * @param restTemplateBuilder {@link RestTemplateAutoConfiguration#restTemplateBuilder}
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
