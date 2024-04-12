package xyz.chthollywn.cnovel.common.singleton;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * objectMapper 单例
 */
public enum ObjectMapperSingleton {
    INSTANCE;

    private final ObjectMapper objectMapper;

    ObjectMapperSingleton() {
        objectMapper = new ObjectMapper();
        // 当遇到未知属性时是否抛出 UnrecognizedPropertyException
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getObjectMapper() {
        return INSTANCE.objectMapper;
    }
}
