package cn.iocoder.yudao.framework.social.config;

import cn.iocoder.yudao.framework.social.core.YudaoAuthRequestFactory;
import com.xingyuv.http.HttpUtil;
import com.xingyuv.http.support.hutool.HutoolImpl;
import com.xingyuv.jushauth.cache.AuthDefaultStateCache;
import com.xingyuv.jushauth.cache.AuthStateCache;
import com.xingyuv.justauth.autoconfigure.JustAuthProperties;
import com.xingyuv.justauth.autoconfigure.SocialAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 社交自动装配类
 *
 * @author timfruit
 * @date 2021-10-30
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(JustAuthProperties.class)
@SuppressWarnings("all")
public class YudaoSocialAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "justauth", value = "enabled", havingValue = "true", matchIfMissing = true)
    public YudaoAuthRequestFactory yudaoAuthRequestFactory(JustAuthProperties properties, AuthStateCache authStateCache) {
        // 需要修改 HttpUtil 使用的实现，避免类报错
        HttpUtil.setHttp(new HutoolImpl());
        // 创建 YudaoAuthRequestFactory
        return new YudaoAuthRequestFactory(properties, authStateCache);
    }

    // @Bean
    // public AuthStateCache authStateCache() {
    //     return new AuthRedisStateCache();https://github.com/yixiaco/ruoyi-tdesign/blob/34fb355910c270cd39eee1881e2755a90c2d0839/ruoyi-common/ruoyi-common-social/src/main/java/org/dromara/common/social/utils/AuthRedisStateCache.java#L14
    // }


    /**
     * redis
     */
 /*   @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnProperty(name = "jap.social.cache.type",havingValue = "redis")
    @AutoConfigureBefore(SocialAutoConfiguration.class)
    static class Redis{
        @Bean
        @ConditionalOnMissingBean
        public AuthStateCache authStateCache(SocialCacheProperties socialCacheProperties,
                                             RedisTemplate<String, String> redisTemplate){
            log.info("使用 redis 缓存social");
            return new RedisAuthStateCache(redisTemplate,socialCacheProperties);
        }
    }*/

    /**
     * 默认
     */
/*    @ConditionalOnProperty(name = "jap.social.cache.type",havingValue = "default", matchIfMissing = true)
    @AutoConfigureBefore(SocialAutoConfiguration.class)
    static class Default{
        @Bean
        @ConditionalOnMissingBean
        public AuthStateCache authStateCache(){
            log.info("使用social的 默认 缓存");
            return AuthDefaultStateCache.INSTANCE;
        }
    }*/

}
