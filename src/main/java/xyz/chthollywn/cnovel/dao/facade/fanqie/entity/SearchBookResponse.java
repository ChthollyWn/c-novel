package xyz.chthollywn.cnovel.dao.facade.fanqie.entity;

import lombok.Data;

import java.util.List;

@Data
public class SearchBookResponse {
    private Integer code;
    private String log_id;
    private String message;
    private ResponseData data;

    @Data
    public static class ResponseData {
        private Integer total_count;
        private List<BookData> search_book_data_list;
    }

    @Data
    public static class BookData {
        private String author;
        private String book_abstract;
        private String book_id;
        private String book_name;
        private String category;
        private Integer creation_status;
        private String first_chapter_id;
        private String last_chapter_id;
        private String last_chapter_time;
        private String last_chapter_title;
        private Integer read_count;
        private String thumb_uri;
        private String thumb_url;
        private Integer word_count;
    }
}
