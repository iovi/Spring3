package iovi.client;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**Класс клиента-владельца публичного и секретного ключа, привязанной captcha*/
public class Client extends Object{
    String secretKey;
    String publicKey;
    String captchaId;
    Date creationTime;

    public Client(){
        secretKey= UUID.randomUUID().toString();
        publicKey= UUID.randomUUID().toString();
        creationTime= Calendar.getInstance().getTime();
    }
    public String getPublicKey(){return publicKey;}
    public String getSecretKey(){return secretKey;}
    public Date getCreationTime(){return creationTime;}
    /** Привязывает заданный идентификатор captcha к клиенту*/
    public void attachCaptcha(String captchaId){
        this.captchaId=captchaId;
    }
    public String getCaptchaId(){return captchaId;}
}
