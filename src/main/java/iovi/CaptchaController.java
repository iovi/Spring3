package iovi;


import javafx.util.Pair;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class CaptchaController {

    static final int TIMEOUT=60000;
    CaptchaService captchaService=new CaptchaService(TIMEOUT);

    @RequestMapping(value = "/captcha/{captchaId}", method = GET)
    public void getCaptchaAsByteArray(@PathVariable("captchaId") String captchaId,
                                      HttpServletResponse response) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(captchaService.getCaptchaImage(captchaId), "png", os);
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        IOUtils.copy(inputStream, response.getOutputStream());
    }

    @RequestMapping(value = "/captcha", method = GET)
    public String getCaptcha(HttpServletResponse response,Model model) {
        Pair<String,Captcha>  captchaWithId =captchaService.getNewCaptcha();
        model.addAttribute("captchaId",captchaWithId.getKey());

        response.setHeader("captcha-id",captchaWithId.getKey());
        response.setHeader("captcha-text",captchaWithId.getValue().getText());
        return "Captcha";
    }

    @RequestMapping(value = "/captcha", method = POST)
    public String checkCaptcha(@RequestParam(value="text") String captchaText,
                             @RequestParam(value="id") String id,
                             Model model) throws IOException {


      boolean result=captchaService.checkCaptchaText(id,captchaText);
      if (result){
          model.addAttribute("captchaText",captchaText);
          model.addAttribute("id",id);
          return "Correct";
      }
      else {
          model.addAttribute("timeout",TIMEOUT/1000);
          return "Error";
      }
    }

}
