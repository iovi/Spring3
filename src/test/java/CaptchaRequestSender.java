import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class CaptchaRequestSender {
    public String captchaText;
    public String captchaId;


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
