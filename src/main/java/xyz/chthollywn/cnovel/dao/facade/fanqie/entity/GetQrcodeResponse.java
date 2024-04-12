package xyz.chthollywn.cnovel.dao.facade.fanqie.entity;

import lombok.Data;

@Data
public class GetQrcodeResponse {
    private String message;
    private ResponseData data;

    @Data
    public static class ResponseData {
        private Integer error_code;
        private String app_name;
        private String captcha;
        private String desc_url;
        private String description;
        private Long expire_time;
        private Boolean is_frontier;
        private String qrcode;
        private String qrcode_index_url;
        private String token;
        private String web_name;
    }
}
