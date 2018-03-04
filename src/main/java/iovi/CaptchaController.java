package iovi;


import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.json.simple.JSONObject;

@Controller
public class CaptchaController {

    static final int TIMEOUT=60000;
    CaptchaService captchaService=new CaptchaService(TIMEOUT);
    ClientService clientService=new ClientService();

    /** Метод для вывода captcha-картинки с id, указанным в адресе запроса*/
    @RequestMapping(value = "/captcha/{captchaId}", method = GET)
    public void getCaptchaAsByteArray(@PathVariable("captchaId") String captchaId,
                                      HttpServletResponse response) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(captchaService.getCaptchaImage(captchaId), "png", os);
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        IOUtils.copy(inputStream, response.getOutputStream());
    }

    /** Метод регистрации пользователя*/
    @RequestMapping(value = "/client/register", method = POST)
    public @ResponseBody JSONObject register() throws IOException {
        ClientData clientData=clientService.registerClient();
        JSONObject json  = new JSONObject();
        json.put("secret",clientData.getSecretKey());
        json.put("public",clientData.getPublicKey());
        return json;
    }
    /** Метод инициирующий проверку пользователя*/
    @RequestMapping(value = "/captcha/new", method = GET)
    public @ResponseBody JSONObject initiate(@RequestParam("public") String publicKey) throws IOException {
        String captchaId =captchaService.getNewCaptchaId();
        JSONObject json  = new JSONObject();
        json.put("request",captchaId);
        json.put("answer",captchaService.getCaptchaText(captchaId));
        return json;
    }

    /** Метод для вывода страницы c новой captcha-картинкой*/
    @RequestMapping(value = "/captcha/image", method = GET)
    public @ResponseBody String getCaptcha(@RequestParam("public") String publicKey,
                                           @RequestParam("request") String captchaId,
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           Model model) throws IOException {
        model.addAttribute("imageURL",request.getRequestURL().toString().replace("/captcha/image","/captcha/"+captchaId));
        model.addAttribute("postURL",request.getRequestURL().toString().replace("/captcha/image","/check"));
        model.addAttribute("captchaId",captchaId);

        response.setHeader("captcha-id",captchaId);
        response.setHeader("captcha-text",captchaService.getCaptchaText(captchaId));
        return "Captcha";
    }

    /**
     * <p>Метод для вывода страницы c новой captcha-картинкой. Картинка получается дополнительным запросом на
     * {@link #getCaptchaAsByteArray(String, HttpServletResponse) getCaptchaAsByteArray} </p>
     * <p>В ответе на запрос присутствуют дополнительные заголовки</p>
     * <ul>
     *     <li>captcha-id - идентификатор captcha-картинки</li>
     *     <li>captcha-text - разгадка для captcha (для упрощения тестирования )</li>
     * </ul>
     * */
    @RequestMapping(value = "/captcha", method = GET)
    public String getCaptcha(HttpServletRequest request, HttpServletResponse response, Model model) {
        String captchaId =captchaService.getNewCaptchaId();
        model.addAttribute("imageURL",request.getRequestURL().toString()+"/"+captchaId);
        model.addAttribute("postURL",request.getRequestURL().toString()+"/check");
        model.addAttribute("captchaId",captchaId);

        response.setHeader("captcha-id",captchaId);
        response.setHeader("captcha-text",captchaService.getCaptchaText(captchaId));
        return "Captcha";
    }


    /** Метод для проверки разгадки captcha-картинки
     * @param id идентификатор captcha-картинки, переданный в заголовке ответа на
     * {@link #getCaptcha(HttpServletRequest, HttpServletResponse,Model) getCaptcha}
     *@param captchaText разгадка captcha-картинки, подлежащая проверке
     * */
    @RequestMapping(value = "/captcha/check", method = POST)
    public String checkCaptcha(@RequestParam(value="text") String captchaText,
                               @RequestParam(value="id") String id,
                               HttpServletResponse response,
                               Model model) throws IOException {


      boolean result=captchaService.checkCaptchaText(id,captchaText);
      if (result){
          model.addAttribute("captchaText",captchaText);
          model.addAttribute("id",id);
          return "Correct";
      }
      else {
          model.addAttribute("timeout",TIMEOUT/1000);
          response.setStatus(422);
          return "Wrong";
      }
    }

}
