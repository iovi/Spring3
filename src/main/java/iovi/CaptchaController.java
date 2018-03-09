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

    static final int CAPTCHA_TIMEOUT=PropertiesHelper.captchaTimeout()*1000;
    static final int CLIENT_TIMEOUT=600000;
    CaptchaService captchaService=new CaptchaService(CAPTCHA_TIMEOUT);
    ClientService clientService=new ClientService(CLIENT_TIMEOUT);

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

    /**
     * Метод регистрации нового пользователя
     * @return JSON-объект  с  двумя UUID-строками: secret – приватный ключ клиента, public – публичный ключ, используемый на сайте.
     * */
    @RequestMapping(value = "/client/register", method = POST)
    public @ResponseBody JSONObject register() throws IOException {
        ClientData clientData=clientService.registerClient();
        JSONObject json  = new JSONObject();
        json.put("secret",clientData.getSecretKey());
        json.put("public",clientData.getPublicKey());
        return json;
    }

    /**
     * <p>Метод для создания новой CAPTCHA.
     * @param publicKey публичный ключ ранее зарегистированного в {@link #register()} клиента.
     * При указании не регистрировавшегося или истекшего клиента возвращает код ответа 403.
     * @return JSON-объект объект с двумя строками: request – идентификатор CAPTCHA, answer – разгадка к решению теста
     * */
    @RequestMapping(value = "/captcha/new", method = GET)
    public @ResponseBody JSONObject createCaptcha(@RequestParam("public") String publicKey, HttpServletResponse response) {
        JSONObject json  = new JSONObject();
        String requestField=null;
        String answerField=null;
        if (clientService.checkClientExistence(publicKey)){
            String captchaId =captchaService.getNewCaptchaId();
            clientService.attachCaptchaToClient(captchaId,publicKey);
            requestField=captchaId;
            if (!PropertiesHelper.isInProductionMode())
                answerField=captchaService.getCaptchaText(captchaId);
        } else{
            response.setStatus(403);
        }
        json.put("request",requestField);
        json.put("answer",answerField);
        return json;
    }

    /**
     * <p>Метод для вывода страницы c captcha-картинкой. Картинка получается дополнительным запросом на
     * {@link #getCaptchaAsByteArray(String, HttpServletResponse) getCaptchaAsByteArray} </p>
     * */
    @RequestMapping(value = "/captcha/image", method = GET)
    public String getCaptchaImage(@RequestParam("public") String publicKey,
                           @RequestParam("request") String captchaId,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           Model model){
        if (clientService.checkClientExistence(publicKey) && clientService.checkCaptchaAttachedToClient(captchaId,publicKey)){
            model.addAttribute("imageURL",request.getRequestURL().toString().replace("/captcha/image","/captcha/"+captchaId));
            model.addAttribute("postURL",request.getRequestURL().toString().replace("/image","/solve"));
            model.addAttribute("captchaId",captchaId);
            model.addAttribute("clientId",publicKey);
            return "Captcha";
        } else{
            response.setStatus(403);
            return "Wrong";
        }
    }


    /** Метод для проверки разгадки captcha-картинки
     * @param publicKey публичный ключ ранее зарегистированного в {@link #register()} клиента.
     * При указании не регистрировавшегося или истекшего клиента возвращает код ответа 403.
     * @param captchaId идентификатор captcha-картинки, переданный в поле "request" ответа на
     * {@link #createCaptcha(String, HttpServletResponse)}
     *@param captchaText разгадка captcha-картинки, подлежащая проверке.
     *@return JSON-объект с полем response - токен, используемый для  последующей  верификации.
     *При неверной разгадке response = null, http-код ответа = 422
     * */
    @RequestMapping(value = "/captcha/solve", method = POST)
    public @ResponseBody JSONObject solve(@RequestParam("public") String publicKey,
                                          @RequestParam(value="answer") String captchaText,
                                          @RequestParam(value="request") String captchaId,
                                          HttpServletResponse response) {
        JSONObject json  = new JSONObject();
        String responseField=null;
        if (clientService.checkClientExistence(publicKey) && clientService.checkCaptchaAttachedToClient(captchaId,publicKey)){
            boolean result=captchaService.checkCaptchaText(captchaId,captchaText);
            if (result){
                responseField=clientService.getTokenForClient(publicKey);
            } else {
                response.setStatus(422);
            }
        } else {
            response.setStatus(403);
        }
        json.put("response",responseField);
        return json;
    }

    /**
     * Метод проверки токена клиента
     * @param secretKey секретный ключ ранее зарегистированного в {@link #register()} клиента.
     * @param token токен, возвращенный в ответе на {@link #solve(String, String, String, HttpServletResponse)}
     * @return JSON-объект с полями: success – результат  проверки токена (булевый  тип), errorCode – строка с информацией об ошибке.
     * Возможные значение errorCode см. в {@link ClientService#verifyClientToken(String, String)} }
     * */
     @RequestMapping(value = "/captcha/verify", method = GET)
    public @ResponseBody JSONObject verify(@RequestParam("secret") String secretKey,
                                          @RequestParam(value="response") String token) {
        JSONObject json  = new JSONObject();
        String errorCode=clientService.verifyClientToken(secretKey,token);
        json.put("errorCode",errorCode);
        json.put("success",errorCode==null?true:false);
        return json;
    }
}
