package xyz.chthollywn.cnovel.service.fanqie;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.chthollywn.cnovel.common.resp.FacadeException;
import xyz.chthollywn.cnovel.dao.facade.fanqie.FanqieClient;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.GetQrcodeResponse;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.UserInfoResponse;
import xyz.chthollywn.cnovel.listener.event.FanqieQrcodeGenerateEvent;
import xyz.chthollywn.cnovel.strategy.cache.CacheService;
import xyz.chthollywn.cnovel.strategy.cache.CacheStrategy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FanqieAccountService {
    @Resource
    private FanqieClient fanqieClient;
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private CacheService cacheService;

    public String getQrcodeBase64Str(String userId) {
        ResponseEntity<GetQrcodeResponse> responseEntity = fanqieClient.getQrcode();
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FacadeException("HTTP异常 " + responseEntity);
        GetQrcodeResponse qrcodeResponse = responseEntity.getBody();
        if (qrcodeResponse == null)
            throw new FacadeException("二维码数据不存在 " +  responseEntity);
        if (!Objects.equals(qrcodeResponse.getData().getError_code(), 0))
            throw new FacadeException("二维码数据获取失败 " + qrcodeResponse);
        // 发布二维码生成事件，校验二维码 如果有userId说明是socket，需要一起发布
        FanqieQrcodeGenerateEvent.EventData.EventDataBuilder builder = FanqieQrcodeGenerateEvent.EventData.builder();
        builder.token(qrcodeResponse.getData().getToken());
        if (StringUtils.isNotBlank(userId)) builder.userId(userId);
        applicationContext.publishEvent(new FanqieQrcodeGenerateEvent(builder.build()));
        return qrcodeResponse.getData().getQrcode();
    }

    public String getQrcodeBase64Str() {
        return getQrcodeBase64Str(null);
    }

    public BufferedImage getQrcodeImg() throws IOException {
        String base64Str = getQrcodeBase64Str();
        // 将Base64编码的字符串解码成字节数组
        byte[] imageBytes = Base64.getDecoder().decode(base64Str);

        // 使用ByteArrayInputStream从字节数组中读取数据
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);

        // 将数据转换为BufferedImage对象
        return ImageIO.read(bis);
    }

    public String getSid_tt(String token) {
        ResponseEntity<Object> responseEntity = fanqieClient.checkQrConnect(token);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FacadeException("HTTP异常 " + responseEntity);
        List<String> cookies = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (CollectionUtils.isEmpty(cookies)) return null;
        for (String cookie : cookies) {
            // 检查Cookie的键是否是sid_tt
            if (cookie.startsWith("sid_tt=")) {
                // 获取sid_tt的值
                return extractValue(cookie);
            }
        }
        return null;
    }

    private String extractValue(String cookie) {
        cookie = cookie.substring("sid_tt=".length());
        // 分割字符串以获取value部分
        String[] parts = cookie.split(";", 2); // 只分割成两部分，第一部分是我们需要的value
        if (parts.length > 0) return parts[0];
        else return null;
    }

    public UserInfoResponse.ResponseData getUserInfo() {
        CacheStrategy cacheStrategy = cacheService.getCacheStrategy();
        String fanqieSidTt = cacheStrategy.getFanqieSid_tt();
        ResponseEntity<UserInfoResponse> responseEntity = fanqieClient.getUserInfo(fanqieSidTt);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FacadeException("HTTP 异常 " + responseEntity);
        UserInfoResponse infoResponse = responseEntity.getBody();
        if (infoResponse == null)
            throw new FacadeException("用户数据不存在 " + responseEntity);
        if (!Objects.equals(infoResponse.getCode(), 0))
            throw new FacadeException("用户数据获取异常 " + infoResponse);

        return infoResponse.getData();
    }

    public Boolean checkUserIsVip() {
        UserInfoResponse.ResponseData userInfo = getUserInfo();
        if (userInfo == null) return false;
        return userInfo.getIsVip();
    }
}
