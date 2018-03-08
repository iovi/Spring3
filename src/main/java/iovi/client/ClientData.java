package iovi.client;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class ClientData extends Object{
    String secretKey;
    String publicKey;
    String captchaId;
    Date creationTime;

    public ClientData(){
        secretKey= UUID.randomUUID().toString();
        publicKey= UUID.randomUUID().toString();
        creationTime= Calendar.getInstance().getTime();
    }
    public String getPublicKey(){return publicKey;}
    public String getSecretKey(){return secretKey;}
    public Date getCreationTime(){return creationTime;}
    public void attachCaptcha(String captchaId){
        this.captchaId=captchaId;
    }
    public String getCaptchaId(){return captchaId;}
}
