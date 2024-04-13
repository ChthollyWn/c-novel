package xyz.chthollywn.cnovel.service.fanqie;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import xyz.chthollywn.cnovel.common.resp.FacadeException;
import xyz.chthollywn.cnovel.common.resp.PageDTO;
import xyz.chthollywn.cnovel.dao.facade.fanqie.FanqieClient;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.BookDetailResponse;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.ChapterFullResponse;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.SearchBookResponse;
import xyz.chthollywn.cnovel.strategy.cache.CacheService;
import xyz.chthollywn.cnovel.strategy.cache.CacheStrategy;

import java.util.Objects;

@Service
@Slf4j
public class FanqieNovelService {
    @Resource
    private FanqieClient fanqieClient;
    @Resource
    private CacheService cacheService;

    public SearchBookResponse.ResponseData searchBook(Integer pageCount, Integer pageIndex, Integer queryType, String queryWord) {
        ResponseEntity<SearchBookResponse> responseEntity = fanqieClient.searchBook(pageCount, pageIndex, queryType, queryWord);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FacadeException("HTTP异常 " + responseEntity);
        SearchBookResponse bookResponse = responseEntity.getBody();
        if (bookResponse == null)
            throw new FacadeException("搜索结果不存在 " + responseEntity);
        if (!Objects.equals(bookResponse.getCode(), 0))
            throw new FacadeException("搜索结果获取异常 " + bookResponse);
        return bookResponse.getData();
    }

    public PageDTO<SearchBookResponse.BookData> searchBookPage(Integer pageCount, Integer pageIndex, Integer queryType, String queryWord) {
        SearchBookResponse.ResponseData responseData = searchBook(pageCount, pageIndex, queryType, queryWord);
        return new PageDTO<>(responseData.getSearch_book_data_list(), pageIndex, pageCount, responseData.getTotal_count());
    }

    public PageDTO<SearchBookResponse.BookData> searchBookPage(Integer pageIndex, String queryWord) {
        if (pageIndex == null) pageIndex = 0;
        else pageIndex = pageIndex - 1;
        return searchBookPage(10, pageIndex, 0, queryWord);
    }

    public BookDetailResponse.ResponseData bookDetail(String bookId) {
        ResponseEntity<BookDetailResponse> responseEntity = fanqieClient.bookDetail(bookId);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FacadeException("HTTP异常 " + responseEntity);
        BookDetailResponse detailResponse = responseEntity.getBody();
        if (detailResponse == null)
            throw new FacadeException("书籍详情数据不存在 " + responseEntity);
        if (!Objects.equals(detailResponse.getCode(), 0))
            throw new FacadeException("数据详情数据获取异常 " + detailResponse);
        return detailResponse.getData();
    }

    public ChapterFullResponse.ResponseData chapterFull(String itemId) {
        CacheStrategy cacheStrategy = cacheService.getCacheStrategy();
        String fanqieSidTt = cacheStrategy.getFanqieSid_tt();
        if (StringUtils.isBlank(fanqieSidTt))
            throw new FacadeException("番茄sid_tt为空");

        ResponseEntity<ChapterFullResponse> responseEntity = fanqieClient.chapterFull(itemId, fanqieSidTt);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FacadeException("HTTP异常 " + responseEntity);
        ChapterFullResponse fullResponse = responseEntity.getBody();
        if (fullResponse == null)
            throw new FacadeException("章节详情数据不存在 " + responseEntity);
        if (!Objects.equals(fullResponse.getCode(), 0))
            throw new FacadeException("章节详情数据获取失败 " + fullResponse);

        ChapterFullResponse.ChapterData chapterData = fullResponse.getData().getChapterData();

        return fullResponse.getData();
    }
}
