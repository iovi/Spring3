package iovi;


import javafx.util.Pair;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    @RequestMapping(value = "/check", method = POST)
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
          return "Error";
      }
    }

}
