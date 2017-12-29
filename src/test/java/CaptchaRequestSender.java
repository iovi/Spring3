import iovi.Captcha;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class CaptchaRequestSender {
    String captchaText;
    String captchaId;

    public String getCaptchaText(){return captchaText;}
    public String getCaptchaId(){return captchaId;}


    public int getCaptchaAndStoreData(String url) {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            int status = connection.getResponseCode();
            if (status==HttpURLConnection.HTTP_OK) {
                captchaId= connection.getHeaderField("captcha-id");
                captchaText= connection.getHeaderField("captcha-text");
            }
            System.out.println("!!captcha="+captchaText);
            connection.disconnect();
            return status;
        }catch (Exception e){
            System.out.print("%error: "+e.getMessage());
            return 0;
        }
    }

    public int checkCaptchaByStoredData(String url) {
        try {
            System.out.println("??captcha="+captchaText);
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
