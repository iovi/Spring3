package iovi;


import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

    CaptchaService captchaService=new CaptchaServiceImpl();

    @RequestMapping(value = "/captcha", method = GET)
    public void getCaptchaAsByteArray(HttpServletResponse response) throws IOException {
        Captcha captcha=captchaService.GetNewCaptcha();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(captcha.image, "png", os);
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        IOUtils.copy(inputStream, response.getOutputStream());
        response.setHeader("captcha-id",Integer.toString(captcha.id));
        response.setHeader("captcha-text",captcha.text);

    }
}
