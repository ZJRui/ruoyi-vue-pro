package cn.iocoder.yudao.framework.common.util.json;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class JsonUtilsTest {



    @Test
    public void testToJsonString() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Map<String, Object> map = new HashMap<>();
        map.put("dateTime", localDateTime);

        String jsonPrettyString = JsonUtils.toJsonPrettyString(map);
        System.out.println(jsonPrettyString);

    }

    @Test
    public void testLocalDateTimeJson() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Map<String, Object> map = new HashMap<>();
        map.put("dateTime", localDateTime);

        String jsonPrettyString = JsonUtils.toJsonPrettyString(map);
        System.out.println(jsonPrettyString);

    }
}