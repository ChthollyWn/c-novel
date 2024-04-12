package xyz.chthollywn.cnovel.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestUtil {

    public static Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();

        // 获取请求参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue()[0]; // 取第一个值，适用于单值参数
            params.put(key, value);
        }
        return params;
    }

    public static String getRequestBody(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper == null) return null;
        return new String(wrapper.getContentAsByteArray());
    }

    public static LinkedHashMap<String, String> getRequestParamsAndBody(HttpServletRequest request) {
        LinkedHashMap<String, String> paramsAndBody = new LinkedHashMap<>();

        // 获取请求参数
        paramsAndBody.put("requestParams", getRequestParams(request).toString());

        // 获取请求体
        String requestBody = getRequestBody(request);
        if (StringUtils.isBlank(requestBody)) paramsAndBody.put("requestBody", "{}");
        else paramsAndBody.put("requestBody", getRequestBody(request));

        return paramsAndBody;
    }
}
