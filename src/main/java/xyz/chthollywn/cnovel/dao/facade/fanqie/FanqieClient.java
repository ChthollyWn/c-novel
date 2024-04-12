package xyz.chthollywn.cnovel.dao.facade.fanqie;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import xyz.chthollywn.cnovel.common.resp.FacadeException;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.BookDetailResponse;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.ChapterFullResponse;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.GetQrcodeResponse;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.SearchBookResponse;
import xyz.chthollywn.cnovel.strategy.cache.CacheService;
import xyz.chthollywn.cnovel.strategy.cache.CacheStrategy;

@Component
public class FanqieClient {
    @Resource
    private RestTemplate restTemplate;

    private final String aid = "2503";
    private final String account_sdk_source = "web";
    private final String sdk_version = "2.2.6-beta.2";
    private final String verifyFp = "verify_lup683u5_B4oq0cU6_wcuA_44gJ_AE07_n5VYOjBfZcKY";
    private final String fp = "verify_lup683u5_B4oq0cU6_wcuA_44gJ_AE07_n5VYOjBfZcKY";

    /**
     * 获取登录二维码
     *
     * @return
     */
    public ResponseEntity<GetQrcodeResponse> getQrcode() {
        String baseUrl = "https://fanqienovel.com/passport/web/get_qrcode/";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("next", "https://fanqienovel.com/main/writer/login")
                .queryParam("need_logo", "true")
                .queryParam("aid", aid)
                .queryParam("account_sdk_source", account_sdk_source)
                .queryParam("sdk_version", sdk_version)
                .queryParam("verifyFp", verifyFp)
                .queryParam("fp", fp);

        String url = builder.toUriString();

        return restTemplate.getForEntity(url, GetQrcodeResponse.class);
    }

    /**
     * 校验二维码扫描结果
     * @param token
     * @return
     */
    public ResponseEntity<Object> checkQrConnect(String token) {
        String baseUrl = "https://fanqienovel.com/passport/web/check_qrconnect/";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("next", "/")
                .queryParam("token", token)
                .queryParam("aid", aid)
                .queryParam("account_sdk_source", account_sdk_source)
                .queryParam("sdk_version", sdk_version)
                .queryParam("verifyFp", verifyFp)
                .queryParam("fp", fp);

        String url = builder.toUriString();

        return restTemplate.getForEntity(url, Object.class);
    }

    /**
     * 书籍搜索
     * @param pageCount 每页数据数量
     * @param pageIndex 页码 从0开始
     * @param queryType 搜索类型 0相关 1最新 2最热
     * @param queryWord 搜索关键词
     * @return
     */
    public ResponseEntity<SearchBookResponse> searchBook(Integer pageCount, Integer pageIndex, Integer queryType, String queryWord) {
        String baseUrl = "https://fanqienovel.com/api/author/search/search_book/v1";
        String customQuery = String.format("filter=127,127,127,127&page_count=%d&page_index=%d&query_type=%s&query_word=%s",
                pageCount, pageIndex, queryType, queryWord);
        String url = baseUrl + "?" + customQuery;

        return restTemplate.getForEntity(url, SearchBookResponse.class);
    }

    /**
     * 书籍详情
     * @param bookId
     * @return
     */
    public ResponseEntity<BookDetailResponse> bookDetail(String bookId) {
        String baseUrl = "https://fanqienovel.com/api/reader/directory/detail";
        String customQuery = String.format("bookId=%s", bookId);
        String url = baseUrl + "?" + customQuery;

        return restTemplate.getForEntity(url, BookDetailResponse.class);
    }

    /**
     * 章节详情
     * @param itemId
     * @return
     */
    public ResponseEntity<ChapterFullResponse> chapterFull(String itemId, String sid_tt) {
        String baseUrl = "https://fanqienovel.com/api/reader/full";
        String customQuery = String.format("itemId=%s", itemId);
        String url = baseUrl + "?" + customQuery;

        HttpHeaders httpHeaders = new HttpHeaders();
        if (StringUtils.isNotBlank(sid_tt))
            httpHeaders.set("sid_tt", sid_tt);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, ChapterFullResponse.class);
    }
}
