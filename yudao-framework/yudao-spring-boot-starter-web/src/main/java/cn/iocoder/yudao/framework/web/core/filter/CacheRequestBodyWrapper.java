package cn.iocoder.yudao.framework.web.core.filter;

import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *  Request Body 缓存 Wrapper
 *
 * @author 芋道源码
 */
public class CacheRequestBodyWrapper extends HttpServletRequestWrapper {

    /**
     * 缓存的内容
     */
    private final byte[] body;

    public CacheRequestBodyWrapper(HttpServletRequest request) {
        super(request);
        //用了 request 缓存包装类，将 request的流给提前读取并缓存下来了
        //为什么HttpServletRequest对象的请求体body只读取一次：
        // HttpServletRequest使用getInputStream()与getReader()获取输入流因为读取时数据流指针的单向移动导致请求的body内容只可读取一次。
        body = ServletUtils.getBodyBytes(request);
        //question:上面的代码，在 中提示  调用方法 IoUtil.readBytes(request.getInputStream()); 后，getParam方法将失效
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);
        // 返回 ServletInputStream
        return new ServletInputStream() {

            @Override
            public int read() {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                //question:这个地方return false没有影响吗？
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {}

            @Override
            public int available() {
                return body.length;
            }

        };
    }

}
