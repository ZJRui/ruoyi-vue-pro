package cn.iocoder.yudao.framework.jackson.config;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.jackson.core.databind.LocalDateTimeDeserializer;
import cn.iocoder.yudao.framework.jackson.core.databind.LocalDateTimeSerializer;
import cn.iocoder.yudao.framework.jackson.core.databind.NumberSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * careful：虽然@AutoConfiguration聚合了@Configuration，但是即便能够扫描到这个类，容
 *  器也不会创建该类的实例，E:\programme\SpringBoot\博文\@AutoConfiguration.pdf
 *
 *
 *
 */
@AutoConfiguration
@Slf4j
public class YudaoJacksonAutoConfiguration {

    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public JsonUtils jsonUtils(List<ObjectMapper> objectMappers) {
        // 1.1 创建 SimpleModule 对象
        SimpleModule simpleModule = new SimpleModule();
        simpleModule
                // 新增 Long 类型序列化规则，数值超过 2^53-1，在 JS 会出现精度丢失问题，因此 Long 自动序列化为字符串类型
                .addSerializer(Long.class, NumberSerializer.INSTANCE)
                .addSerializer(Long.TYPE, NumberSerializer.INSTANCE)
                // 新增 LocalDateTime 序列化、反序列化规则
                .addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE)
                .addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
        // 1.2 注册到 objectMapper
        objectMappers.forEach(objectMapper -> objectMapper.registerModule(simpleModule));

        // 2. 设置 objectMapper 到 JsonUtils {
        // question: 为什么只获取第一个ObjectMapper? 在一个就是 这里init方法会替换到JsonUtil中静态配置的ObjectMapper，为什么要替换？
        JsonUtils.init(CollUtil.getFirst(objectMappers));
        testJsonSeriNumber();
        testLocalDateTimeJson();
        return new JsonUtils();
    }



    private void testJsonSeriNumber(){
        Student student = new Student();
        student.setId(-9007199254740992L);
        student.setName("abc");
        System.out.println(JsonUtils.toJsonString(student));//{"name":"abc","id":"-9007199254740992"}
        String str="{\"name\":\"abc\",\"id\":\"-9007199254740992\"}";
        Student student1=JsonUtils.parseObject(str,Student.class);
        log.info("[init][初始化 JsonUtils 成功]");
    }
    public void testLocalDateTimeJson() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Map<String, Object> map = new HashMap<>();
        map.put("dateTime", localDateTime);
        Student student = new Student();
        student.setId(-9007199254740992L);
        student.setDateTime(localDateTime);

        /**
         * {
         *   "name" : null,
         *   "id" : "-9007199254740992",
         *   "dateTime" : 1697014365304  --->被序列化为时间戳，而不是 JsonFormat 注解中的格式
         * }
         */
        String jsonPrettyString = JsonUtils.toJsonPrettyString(student);
        System.out.println(jsonPrettyString);

    }
    @Data
    static  class Student{
        private String name;
        private  long id;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime dateTime;
    }

}
