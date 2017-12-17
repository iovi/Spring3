package iovi;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class GreetingController {


    @RequestMapping(value="/greeting", method=GET)
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }


    @RequestMapping(value = "/image", method = GET)
    public void getImageAsByteArray(HttpServletResponse response) throws IOException {
        Captcha captcha=new Captcha(20,6);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(captcha.image, "png", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        IOUtils.copy(is, response.getOutputStream());

    }
}
