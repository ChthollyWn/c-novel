package xyz.chthollywn.cnovel.rest.controller.fanqie;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.chthollywn.cnovel.common.resp.ResponseDTO;
import xyz.chthollywn.cnovel.service.fanqie.FanqieAccountService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/fanqie/account")
public class FanqieAccountController {
    @Resource
    private FanqieAccountService fanqieAccountService;

    @GetMapping("/qrcode-base64")
    public ResponseDTO qrcodeBase64() {
        return ResponseDTO.success(fanqieAccountService.getQrcodeBase64Str());
    }

    @GetMapping("/qrcode-img")
    public void qrcodeImg(HttpServletResponse servletResponse) throws IOException {
        BufferedImage qrcodeImg = fanqieAccountService.getQrcodeImg();

        servletResponse.setContentType("image/png");
        servletResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        servletResponse.setHeader("Pragma", "no-cache");

        ImageIO.write(qrcodeImg, "png", servletResponse.getOutputStream());
    }
}
