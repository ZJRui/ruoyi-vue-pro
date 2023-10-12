package cn.iocoder.yudao.framework.jackson.core.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import java.io.IOException;

/**
 * Long 序列化规则
 *
 * 会将超长 long 值转换为 string，解决前端 JavaScript 最大安全整数是 2^53-1 的问题
 *
 * @author 星语
 */
@JacksonStdImpl
public class NumberSerializer extends com.fasterxml.jackson.databind.ser.std.NumberSerializer {

    /**
     * 1.Number.MIN_SAFE_INTEGER 静态数据属性代表在 JavaScript 中最小的安全整数（-253 – 1）。
     * const x = Number.MIN_SAFE_INTEGER - 1; console.log(x);
     * // Expected output: -9007199254740992
     *
     *
     * 2.
     * question:为什么没有反序列化？ 原因是我们要考虑的是后端传递给前端精度丢失的问题。 至于前端传递给后端，当数据超过最大安全整数时，前
     *  端会自动转换为字符串,默认的com.fasterxml.jackson.databind.deser.std.NumberDeserializers.LongDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
     *  支持将输入的字符串转为 期望的long类型。
     *
     */
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;
    private static final long MIN_SAFE_INTEGER = -9007199254740991L;

    public static final NumberSerializer INSTANCE = new NumberSerializer(Number.class);

    public NumberSerializer(Class<? extends Number> rawType) {
        super(rawType);
    }

    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 超出范围 序列化位字符串
        if (value.longValue() > MIN_SAFE_INTEGER && value.longValue() < MAX_SAFE_INTEGER) {
            super.serialize(value, gen, serializers);
        } else {
            gen.writeString(value.toString());
        }
    }
}
