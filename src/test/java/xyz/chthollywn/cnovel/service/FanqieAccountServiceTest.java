package xyz.chthollywn.cnovel.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import xyz.chthollywn.cnovel.CNovelApplicationTests;
import xyz.chthollywn.cnovel.service.fanqie.FanqieAccountService;

import static org.apache.commons.lang3.StringUtils.substring;

public class FanqieAccountServiceTest extends CNovelApplicationTests {
    @Resource
    private FanqieAccountService fanqieAccountService;

    @Test
    public void sid_ttTest() {
        String sid_tt = fanqieAccountService.getSid_tt("26670c1aeb084f30e3c524e90ae0ab36_lq");
        System.out.println(sid_tt);
    }
}
