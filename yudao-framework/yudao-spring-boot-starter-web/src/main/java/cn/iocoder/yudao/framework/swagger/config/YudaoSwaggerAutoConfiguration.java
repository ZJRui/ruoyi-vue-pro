package cn.iocoder.yudao.framework.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.*;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.providers.JavadocProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Swagger 自动配置类，基于 OpenAPI + Springdoc 实现。
 * <p>
 * 友情提示：
 * 1. Springdoc 文档地址：<a href="https://github.com/springdoc/springdoc-openapi">仓库</a>
 * 2. Swagger 规范，于 2015 更名为 OpenAPI 规范，本质是一个东西
 *
 * @author 芋道源码
 */
@AutoConfiguration
@ConditionalOnClass({OpenAPI.class})
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true", matchIfMissing = true)
// 设置为 false 时，禁用
@SuppressWarnings({"all"})
public class YudaoSwaggerAutoConfiguration {

    // ========== 全局 OpenAPI 配置 ==========

    @Bean
    public OpenAPI createApi(SwaggerProperties properties) {


        /**
         * addSecurityItem 方法是用来添加安全项（Security Item）到 OpenAPI 文档的。
         * 在 OpenAPI 中，安全项通常指的是用于保护 API 资源的认证和授权机制。安全项可以包括各种认证方式，比如 API 密钥、OAuth 令牌等。
         *
         * 使用 addSecurityItem 方法，你可以将安全项添加到 OpenAPI 对象中，以定义API的安全策略。以下是一个简单的示例：
         *  new OpenAPI()
         *             .addSecurityItem(
         *                 new SecurityRequirement()
         *                     .addList("apiKeyAuth") // 这里的 "apiKeyAuth" 是你在 components/securitySchemes 中定义的安全方案的名称
         *             );
         *
         *note:访问http://localhost:48080/v3/api-docs 查看openapi文档
         *
         * question: org.springdoc.core.SpringdocBeanFactoryConfigurer#initBeanFactoryPostProcessor(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)z
         *  中将OpenAPI 和OpenAPIService 的scope设置为prototype，导致每次从容器中获取该对象的时候都会创建新的对象。
         *
         *  careful:xxx
         *  donote:xxx
         *
         *
         */
        Map<String, SecurityScheme> securitySchemas = buildSecuritySchemes();
        OpenAPI openAPI = new OpenAPI()
                // 接口信息
                .info(buildInfo(properties))
                // 接口安全配置
                .components(new Components().securitySchemes(securitySchemas))
                //  question: 为什么这个地方需要单独addSecurityItem?还有下面的forEach也addSecurityItem?
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
        securitySchemas.keySet().forEach(key -> openAPI.addSecurityItem(new SecurityRequirement().addList(key)));
        /**
         * openApi中的security：
         *   "security": [
         *     {
         *       "Authorization": [
         *
         *       ]
         *     },
         *     {
         *       "Authorization": [
         *
         *       ]
         *     }
         *   ]
         */
        return openAPI;


    }

    /**
     * API 摘要信息
     */
    private Info buildInfo(SwaggerProperties properties) {
        return new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion())
                .contact(new Contact().name(properties.getAuthor()).url(properties.getUrl()).email(properties.getEmail()))
                .license(new License().name(properties.getLicense()).url(properties.getLicenseUrl()));
    }

    /**
     * 安全模式，这里配置通过请求头 Authorization 传递 token 参数
     */
    private Map<String, SecurityScheme> buildSecuritySchemes() {
        Map<String, SecurityScheme> securitySchemes = new HashMap<>();
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY) // 类型
                .name(HttpHeaders.AUTHORIZATION) // 请求头的 name
                .in(SecurityScheme.In.HEADER); // token 所在位置
        securitySchemes.put(HttpHeaders.AUTHORIZATION, securityScheme);

        return securitySchemes;
    }

    /**
     * 自定义 OpenAPI 处理器
     *
     * question: 这个Bean没有新增业务逻辑，不知道为什么要单独注入。 实际在org.springdoc.core.SpringDocConfiguration#openAPIBuilder(java.util.Optional, org.springdoc.core.SecurityService, org.springdoc.core.SpringDocConfigProperties, org.springdoc.core.PropertyResolverUtils, java.util.Optional, java.util.Optional, java.util.Optional)
     *  中已经配置了自动注入。
     *
     * 在Springdoc-OpenAPI库中，`OpenAPIService` 类是一个核心类，用于处理OpenAPI文档的生成和操作。它提供了一些方法，
     * 用于配置和获取OpenAPI文档，以及对文档进行操作。具体来说，`OpenAPIService` 类的主要作用包括以下几点：
     *
     * 1. **生成OpenAPI文档：** `OpenAPIService` 可以自动扫描你的Spring应用程序，解析控制器、路径、操作和模型，并生成OpenAPI文档。
     *
     * 2. **提供OpenAPI文档的访问和操作接口：** 通过 `OpenAPIService` 类，你可以获取生成的OpenAPI文档对象，然后可以进一步操作文档，
     * 例如添加自定义信息、修改路径、操作、参数等。
     *
     * 3. **自定义OpenAPI文档的生成：** 你可以通过 `OpenAPIService` 提供的方法，设置和修改OpenAPI文档的各种属性，以满足你的定制需求。
     *
     * 4. **集成Swagger UI和ReDoc：** `OpenAPIService` 提供了与Swagger UI和ReDoc集成的功能，你可以通过该类的方法启用这些UI，
     * 以便在浏览器中查看和测试生成的OpenAPI文档。
     *
     * 5. **支持Spring Boot的自动配置：** Springdoc-OpenAPI库的自动配置是基于 `OpenAPIService` 类完成的，它能够自动注册到Spring应用
     * 程序的上下文中，实现OpenAPI文档的自动生成和访问。
     *
     * 在典型的Spring Boot应用程序中，你通常不需要直接操作 `OpenAPIService` 类，因为该类的功能已经被封装到了自动配置中。你只需使用
     * `@OpenAPIDefinition` 和其他相关注解配置你的API，Springdoc-OpenAPI库会自动处理文档的生成和展示。但是，如果你需要对生成的OpenAPI文档
     * 进行更高级的定制或操作，你可以通过 `OpenAPIService` 类来实现。
     */
    @Bean
    public OpenAPIService openApiBuilder(Optional<OpenAPI> openAPI,
                                         SecurityService securityParser,
                                         SpringDocConfigProperties springDocConfigProperties,
                                         PropertyResolverUtils propertyResolverUtils,
                                         Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers,
                                         Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers,
                                         Optional<JavadocProvider> javadocProvider) {

        return new OpenAPIService(openAPI, securityParser, springDocConfigProperties,
                propertyResolverUtils, openApiBuilderCustomizers, serverBaseUrlCustomizers, javadocProvider);
    }

    // ========== 分组 OpenAPI 配置 ==========

    /**
     * 所有模块的 API 分组
     */
    @Bean
    public GroupedOpenApi allGroupedOpenApi() {
        return buildGroupedOpenApi("all", "");
    }

    public static GroupedOpenApi buildGroupedOpenApi(String group) {
        return buildGroupedOpenApi(group, group);
    }

    public static GroupedOpenApi buildGroupedOpenApi(String group, String path) {
        /**
         *当需要对接口进行归类分组时
         * group=all 对应的接口是 /admin-api/** 和 /app-api/**
         * group=infra , YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("infra");  对应的接口是/admin-api/infra 和 /app-api/infra
         * group=system , YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("system");  对应的接口是/admin-api/system 和 /app-api/system
         *
         *http://server:port/context-path/v3/api-docs/groupName
         */
        return GroupedOpenApi.builder()
                .group(group)
                .pathsToMatch("/admin-api/" + path + "/**", "/app-api/" + path + "/**")
                .addOperationCustomizer((operation, handlerMethod) -> operation
                        .addParametersItem(buildTenantHeaderParameter())
                        .addParametersItem(buildSecurityHeaderParameter()))
                .build();
    }

    /**
     * 构建 Tenant 租户编号请求头参数
     *
     * @return 多租户参数
     */
    private static Parameter buildTenantHeaderParameter() {
        /**
         * tenant-id
         * integer($int32)
         * (header)
         * 租户编号
         *
         * Default value : 1
         */
        return new Parameter()
                .name(HEADER_TENANT_ID) // header 名
                .description("租户编号") // 描述
                .in(String.valueOf(SecurityScheme.In.HEADER)) // 请求 header
                .schema(new IntegerSchema()._default(1L).name(HEADER_TENANT_ID).description("租户编号")); // 默认：使用租户编号为 1
    }

    /**
     * 构建 Authorization 认证请求头参数
     * <p>
     * 解决 Knife4j <a href="https://gitee.com/xiaoym/knife4j/issues/I69QBU">Authorize 未生效，请求header里未包含参数</a>
     *
     * @return 认证参数
     */
    private static Parameter buildSecurityHeaderParameter() {
        return new Parameter()
                .name(HttpHeaders.AUTHORIZATION) // header 名
                .description("认证 Token") // 描述
                .in(String.valueOf(SecurityScheme.In.HEADER)) // 请求 header
                .schema(new StringSchema()._default("Bearer test1").name(HEADER_TENANT_ID).description("认证 Token")); // 默认：使用用户编号为 1
    }

}

