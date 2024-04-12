package xyz.chthollywn.cnovel.common.resp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
@Getter
public class ResponseDTO {
    // 代表服务端向客户端发送一条data为text类型的数据
    public static final String TEXT = "text";
    // 代表服务端向客户端发送一条img类型的数据 data为该图片的base64编码
    public static final String IMG_BASE64 = "img-base64";
    // 代表服务端向客户端发送一条data为messageEntity类型的数据
    public static final String MESSAGE = "message";
    // 代表服务端向客户端发送一条data为List<messageEntity>类型的历史数据
    public static final String HISTORY = "history";
    public static final String BOOK_PAGE = "book-page";
    public static final String BOOK_DETAIL = "book-detail";
    public static final String CHAPTER_FULL = "chapter-full";


    private int error;
    private String type;
    private String message;
    private Object data;

    public static ResponseDTO success() {
        return of(0, null, "success", null);
    }

    public static ResponseDTO success(Object data) {
        return of(0, null, "success", data);
    }

    public static ResponseDTO error() {
        return of(1, null, "error", null);
    }

    public static ResponseDTO error(String message) {
        return of(1, null,  message, null);
    }

    public static ResponseDTO socketSuccess(String type, Object data) {
        return of(0, type, "success", data);
    }

    public static ResponseDTO socketError(String message) {
        return of(1, TEXT, message, null);
    }
}

