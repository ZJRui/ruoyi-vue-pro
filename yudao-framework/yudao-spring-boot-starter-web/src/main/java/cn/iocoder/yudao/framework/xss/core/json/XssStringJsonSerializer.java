package cn.iocoder.yudao.framework.xss.core.json;

import cn.iocoder.yudao.framework.xss.config.XssProperties;
import cn.iocoder.yudao.framework.xss.core.clean.XssCleaner;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * XSS过滤 jackson 序列化器
 *
 * @author Hccake
 * @version 1.0
 * @date 2019/10/17 22:23
 */
public class XssStringJsonSerializer extends JsonSerializer<String> {

    private final XssCleaner xssCleaner;
    private final XssProperties xssProperties;


    public XssStringJsonSerializer(XssCleaner xssCleaner, XssProperties xssProperties) {
        this.xssProperties = xssProperties;
        this.xssCleaner = xssCleaner;
    }

    @Override
    public Class<String> handledType() {
        return String.class;
    }

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (value != null) {
            // 开启 Xss 才进行处理
            if (xssProperties.isEnable()) {
                value = xssCleaner.clean(value);
            }
            jsonGenerator.writeString(value);
        }
    }

}