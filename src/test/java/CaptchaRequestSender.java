import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * <p>Отправщик запросов на сервис captcha</p>
 * <p>Рекомендуемый порядок работы - создать новый объект класса,
 * выполнить {@link #getCaptchaAndStoreData(String)} для получания данных о новой captcha,
 * затем проверить пришедшие результаты с помощью {@link #checkCaptchaByStoredData(String)}</p>
 */
public class CaptchaRequestSender {
    /**Текст captcha, получаемый из заголовка captcha-text при запросе {@link #getCaptchaAndStoreData(String)}*/
    public String captchaText;
    /**id captcha, получаемый из заголовка captcha-id при запросе {@link #getCaptchaAndStoreData(String)}*/
    public String captchaId;

    public String secretKey;
    public String publicKey;

    public String token;

    public boolean success;
    public String errorCode;


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
     * Выполнет HTTP запрос на указанный адрес, сохраняет поля public и secret полученного JSON-объекта
     * @param url адрес выполняемого запроса
     * @param method метод запроса
     * @return код ответа запроса на указанный адрес, 0 в случае возникших исключений
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
