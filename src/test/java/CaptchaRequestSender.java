import iovi.CaptchaController;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>Отправщик запросов на сервис captcha</p>
 * <p>Рекомендуемый порядок работы - создать новый объект класса, выполнить
 * <ul>
 *     <li>{@link #registerClient(String, String)}</li>
 *     <li>{@link #newCaptcha(String, String)}</li>
 *     <li>{@link #getImage(String, String)}</li>
 *     <li>{@link #solveCaptcha(String, String)}</li>
 *     <li>{@link #verifyClient(String, String)}</li>
 * </ul>
 * необходимые промежуточные результаты будут сохранены в поля класса
 */
public class CaptchaRequestSender {
    /**Текст captcha, получаемый из поля "answer" при запросе {@link #newCaptcha(String, String)}*/
    public String captchaText;
    /**id captcha, получаемый из поля "request" при запросе {@link #newCaptcha(String, String)}*/
    public String captchaId;
    /**секретный ключ, получаемый из поля "secret" при запросе {@link #registerClient(String, String)}*/
    public String secretKey;
    /**публичный ключ, получаемый из поля "public" при запросе {@link #registerClient(String, String)}*/
    public String publicKey;
    /**токен, получаемый из поля "response" при запросе {@link #solveCaptcha(String, String)}*/
    public String token;
    /**успешность запроса {@link #verifyClient(String, String)} из поля "success" */
    public boolean success;
    /**код ошибки, получаемый из поля "errorCode" при запросе {@link #verifyClient(String, String)}*/
    public String errorCode;


    /**Метод для получения JSON-объекта из соединения HttpURLConnection*/
    static JSONObject getResponseJSON(HttpURLConnection connection) throws IOException,ParseException{
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        JSONParser parser = new JSONParser();
        return(JSONObject) parser.parse(response.toString());
    }

    /**
     * Выполнет HTTP-запрос на указанный адрес, с указанным методом,
     * ожидает выполнения логики {@link CaptchaController#register()} по этому адресу
     */
    public int registerClient(String url, String method){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method);
            int status = connection.getResponseCode();
            if (status==HttpURLConnection.HTTP_OK) {
                JSONObject json= getResponseJSON(connection);
                secretKey=(String)json.get("secret");
                publicKey=(String)json.get("public");
            }
            connection.disconnect();
            return status;
        }catch (Exception e){
            System.err.println(e.getMessage());
            return 0;
        }
    }

    /**
     * Выполнет HTTP-запрос на указанный адрес, с указанным методом,
     * ожидает выполнения логики {@link CaptchaController#createCaptcha(String, HttpServletResponse)} по этому адресу
     */

    public int newCaptcha(String url, String method){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url+"?public="+publicKey).openConnection();
            connection.setRequestMethod(method);
            int status = connection.getResponseCode();
            if (status==HttpURLConnection.HTTP_OK) {
                JSONObject json= getResponseJSON(connection);
                captchaId=(String)json.get("request");
                captchaText=(String)json.get("answer");
            }
            connection.disconnect();
            return status;
        }catch (Exception e){
            System.err.println(e.getMessage());
            return 0;
        }
    }

    /**
     * Выполнет HTTP-запрос на указанный адрес, с указанным методом,
     * ожидает выполнения логики
     * {@link CaptchaController#getCaptchaImage(String, String, HttpServletRequest, HttpServletResponse, Model)} по этому адресу
     */
    public int getImage(String url, String method){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url+"?public="+publicKey+
                    "&request="+captchaId).openConnection();
            connection.setRequestMethod(method);
            int status = connection.getResponseCode();
            connection.disconnect();
            return status;
        }catch (Exception e){
            System.err.println(e.getMessage());
            return 0;
        }
    }

    /**
     * Выполнет HTTP-запрос на указанный адрес, с указанным методом,
     * ожидает выполнения логики {@link CaptchaController#solve(String, String, String, HttpServletResponse)} по этому адресу
     */
    public int solveCaptcha(String url, String method){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url+"?public="+publicKey+"&request="+captchaId+
                "&answer="+captchaText).openConnection();
            connection.setRequestMethod(method);
            int status = connection.getResponseCode();
            if (status==HttpURLConnection.HTTP_OK) {
                JSONObject json= getResponseJSON(connection);
                token=(String)json.get("response");
            }
            connection.disconnect();
            return status;
        }catch (Exception e){
            System.err.println(e.getMessage());
            return 0;
        }
    }

    /**
     * Выполнет HTTP-запрос на указанный адрес, с указанным методом,
     * ожидает выполнения логики {@link CaptchaController#verify(String, String, HttpServletResponse)} по этому адресу
     */
    public int verifyClient(String url, String method){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url+"?secret="+secretKey+
                    "&response="+token).openConnection();
            connection.setRequestMethod(method);
            int status = connection.getResponseCode();
            if (status==HttpURLConnection.HTTP_OK) {
                JSONObject json= getResponseJSON(connection);
                errorCode=(String)json.get("errorCode");
                success=(boolean)json.get("success");
            }
            connection.disconnect();
            return status;
        }catch (Exception e){
            System.err.println(e.getMessage());
            return 0;
        }
    }
}
