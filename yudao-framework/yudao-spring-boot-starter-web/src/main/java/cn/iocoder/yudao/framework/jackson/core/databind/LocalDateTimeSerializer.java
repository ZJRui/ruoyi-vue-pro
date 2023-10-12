package cn.iocoder.yudao.framework.jackson.core.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * LocalDateTime序列化规则
 * <p>
 * 会将LocalDateTime序列化为毫秒级时间戳
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    /**
     * Jackson对Java8 日期时间的支持：E:\programme\Jackson\博文\Jackson 之 LocalDateTime 序列化与反序列化 - 掘金.pdf
     * <p>
     * 默认情况下 JsonUtils中使用了objectMapper.registerModules(new JavaTimeModule()); 来实现对Java8 日期的序列化支持。
     * <p>
     * JavaTimeModule这个类中注册了对日期时间的序列化和反序列化规则，
     * JavaTimeModule中注册的LocalDateTimeSerializer 会 考虑字段上的JsonFormat注解来决定序列化后的格式。
     * <p>
     * 在这里我们自定义日期时间的序列化规则，将LocalDateTime序列化为毫秒级时间戳。因此所有的LocalDateTime都会被序列化为毫秒级时间戳，即便你使用了
     * JsonFoamt注解指定格式也不会生效
     */

    public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }
}
