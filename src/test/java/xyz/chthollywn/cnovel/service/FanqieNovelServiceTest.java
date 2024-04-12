package xyz.chthollywn.cnovel.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import xyz.chthollywn.cnovel.CNovelApplicationTests;
import xyz.chthollywn.cnovel.dao.facade.fanqie.FanqieClient;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.BookDetailResponse;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.ChapterFullResponse;

public class FanqieNovelServiceTest extends CNovelApplicationTests {
    @Resource
    private FanqieClient fanqieClient;

    @Test
    public void clientSearch() {
        Object object = fanqieClient.searchBook(10, 0, 0, "异世界");
        System.out.println();
    }

    @Test
    public void clientDetail() {
        ResponseEntity<BookDetailResponse> responseEntity = fanqieClient.bookDetail("7000950900046957576");
        System.out.println();
    }

    @Test
    public void clientFull() {
        ResponseEntity<ChapterFullResponse> responseEntity = fanqieClient.chapterFull("7000967441018782239", null);
        ChapterFullResponse body = responseEntity.getBody();
        ChapterFullResponse.ChapterData chapterData = body.getData().getChapterData();
        String content = chapterData.getContent();
        System.out.println(content);
    }
}
