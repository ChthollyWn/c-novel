package xyz.chthollywn.cnovel.dao.facade.fanqie.entity;

import lombok.Data;

import java.util.List;

@Data
public class BookDetailResponse {
    private Integer code;
    private String log_id;
    private String message;
    private Long now;
    private ResponseData data;

    @Data
    public static class ResponseData {
        private List<String> allItemIds;
        private List<String> volumeNameList;
        private List<List<ChapterData>> chapterListWithVolume;
    }

    @Data
    public static class ChapterData {
        private String itemId;
        private Integer needPay;
        private String title;
        private String isChapterLock;
        private String isPaidPublication;
        private String isPaidStory;
        private String volume_name;
        private String firstPassTime;
    }
}
