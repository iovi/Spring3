import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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


    /**
     * Выполнет HTTP GET-запрос на указанный адрес, сохраняет полученные параметры из заголовков ответа captcha-id, captcha-text
     * @param url адрес выполняемого GET-запроса
     * @return код ответа GET-запроса на указанный адрес, 0 в случае возникших исключений
     */
    public int getCaptchaAndStoreData(String url) {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            int status = connection.getResponseCode();
            if (status==HttpURLConnection.HTTP_OK) {
                captchaId= connection.getHeaderField("captcha-id");
                captchaText= connection.getHeaderField("captcha-text");
            }
            connection.disconnect();
            return status;
        }catch (Exception e){
            return 0;
        }
    }

    /**
     * Выполнет HTTP POST-запрос на указанный адрес, с параметрами id={@link #captchaId}&text={@link #captchaText}
     * @param url адрес выполняемого POST-запроса
     * @return код ответа POST-запроса на указанный адрес, 0 в случае возникших исключений
     */
    public int checkCaptchaByStoredData(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes("text=" + captchaText + "&id=" + captchaId);
            int status = connection.getResponseCode();
            out.flush();
            out.close();
            connection.disconnect();
            return status;
        } catch (Exception e) {
            return 0;
        }
    }

}
