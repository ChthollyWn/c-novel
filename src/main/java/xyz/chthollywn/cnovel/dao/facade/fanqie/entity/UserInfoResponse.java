package xyz.chthollywn.cnovel.dao.facade.fanqie.entity;

import lombok.Data;

@Data
public class UserInfoResponse {
    private Integer code;
    private String log_id;
    private String message;
    private Long now;
    private ResponseData data;

    @Data
    public static class ResponseData {
        private String avatar;
        private String name;
        private String id;
        private Integer isCp;
        private Integer hasSerialPermission;
        private Integer firstBookGender;
        private Integer mpHighlightAuto;
        private String type;
        private Boolean isVip;
        private Boolean isVip2d;
    }
}
