package iovi;


import javafx.util.Pair;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class CaptchaController {

    CaptchaService captchaService=new CaptchaService(1000*60);

    @RequestMapping(value = "/captcha", method = GET)
    public void getCaptchaAsByteArray(HttpServletResponse response) throws IOException {
        Pair<String,Captcha>  captchaWithId =captchaService.getNewCaptcha();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(captchaWithId.getValue().getImage(), "png", os);
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        IOUtils.copy(inputStream, response.getOutputStream());
        response.setHeader("captcha-id",captchaWithId.getKey());
        response.setHeader("captcha-text",captchaWithId.getValue().getText());

    }
}
