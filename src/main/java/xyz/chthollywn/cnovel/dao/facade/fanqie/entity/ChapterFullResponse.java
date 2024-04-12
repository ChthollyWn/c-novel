package xyz.chthollywn.cnovel.dao.facade.fanqie.entity;

import lombok.Data;

@Data
public class ChapterFullResponse {
    private Integer code;
    private String log_id;
    private String message;
    private Long now;
    private ResponseData data;

    @Data
    public static class ResponseData {
        private ChapterData chapterData;
    }

    @Data
    public static class ChapterData {
        private String author;
        private String bookId;
        private String bookName;
        private String chapterWordNumber;
        private String content;
        private String creationStatus;
        private String firstPassTime;
        private String genre;
        private String giftWord;
        private String groupId;
        private Boolean isChapterLock;
        private String isPaidStory;
        private String itemId;
        private Integer needPay;
        private String nextItemId;
        private String order;
        private String originalAuthors;
        private String platform;
        private String preItemId;
        private String serialCount;
        private String status;
        private String thumbUri;
        private String title;
        private String uid;
    }
}
