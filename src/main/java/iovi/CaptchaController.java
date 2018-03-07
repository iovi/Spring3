package iovi;


import iovi.client.ClientData;
import iovi.client.ClientService;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
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

import static java.lang.System.getProperty;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
    public @ResponseBody JSONObject initiate(@RequestParam("public") String publicKey, HttpServletResponse response) {
        JSONObject json  = new JSONObject();

        if (clientService.checkClientExistence(publicKey)){
            String captchaId =captchaService.getNewCaptchaId();
            clientService.attachCaptchaToClient(captchaId,publicKey);
            json.put("request",captchaId);
            if ("false".equals(System.getProperty("production"))){
                json.put("answer",captchaService.getCaptchaText(captchaId));
            } else {
                json.put("answer",null);
            }
        } else{
            response.setStatus(403);
            json.put("message","Public key "+publicKey+" is absent. You should go to /client/register first");
            json.put("request",null);
            json.put("answer",null);
        }
        return json;
    }

    /** Метод для вывода страницы c новой captcha-картинкой*/
    @RequestMapping(value = "/captcha/image", method = GET)
    public String getImage(@RequestParam("public") String publicKey,
                           @RequestParam("request") String captchaId,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           Model model){
        if (clientService.checkClientExistence(publicKey) && clientService.checkCaptchaAttachedToClient(captchaId,publicKey)){
            model.addAttribute("imageURL",request.getRequestURL().toString().replace("/captcha/image","/captcha/"+captchaId));
            model.addAttribute("postURL",request.getRequestURL().toString().replace("/image","/solve"));
            model.addAttribute("captchaId",captchaId);
            return "Captcha";
        } else{
            response.setStatus(403);
            return "Wrong";
        }
    }

    @RequestMapping(value = "/captcha/solve", method = POST)
    public @ResponseBody JSONObject solve(@RequestParam("public") String publicKey,
                                          @RequestParam(value="answer") String captchaText,
                                          @RequestParam(value="request") String captchaId,
                                          HttpServletResponse response) {
        JSONObject json  = new JSONObject();
        if (clientService.checkClientExistence(publicKey) && clientService.checkCaptchaAttachedToClient(captchaId,publicKey)){
            boolean result=captchaService.checkCaptchaText(captchaId,captchaText);
            if (result){
                String token=clientService.getTokenForClient(publicKey);
                json.put("response",token);
            } else {
                response.setStatus(422);
                json.put("response",null);
                json.put("message","Incorrect captcha solving");
            }
        } else {
            response.setStatus(403);
            json.put("response",null);
            json.put("message","Public key "+publicKey+" is absent or does not attached to captcha #"+captchaId);
        }
        return json;
    }

    @RequestMapping(value = "/captcha/verify", method = GET)
    public @ResponseBody JSONObject verify(@RequestParam("secret") String secretKey,
                                          @RequestParam(value="response") String token,
                                          HttpServletResponse response) {
        JSONObject json  = new JSONObject();
        json.put("success",true);
        json.put("errorCode","");
        return json;
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
