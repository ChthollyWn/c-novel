package xyz.chthollywn.cnovel.rest.controller.fanqie;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.chthollywn.cnovel.common.resp.PageDTO;
import xyz.chthollywn.cnovel.common.resp.ResponseDTO;
import xyz.chthollywn.cnovel.dao.facade.fanqie.entity.SearchBookResponse;
import xyz.chthollywn.cnovel.service.fanqie.FanqieNovelService;

@RestController
@RequestMapping("/fanqie/novel/")
public class FanqieNovelController {
    @Resource
    private FanqieNovelService fanqieNovelService;

    @GetMapping("/search/book")
    public ResponseDTO searchBook(@RequestParam String queryWord, @RequestParam Integer current) {
        PageDTO<SearchBookResponse.BookData> pageDTO = fanqieNovelService.searchBookPage(current, queryWord);
        return ResponseDTO.success(pageDTO);
    }
}
