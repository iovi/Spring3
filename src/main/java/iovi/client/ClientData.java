package iovi.client;

import java.util.UUID;

public class ClientData extends Object{
    String secretKey;
    String publicKey;
    String captchaId;
    boolean verified;

    public ClientData(){
        secretKey= UUID.randomUUID().toString();
        publicKey= UUID.randomUUID().toString();
        verified=false;
    }
    public String getPublicKey(){return publicKey;}
    public String getSecretKey(){return secretKey;}
    public boolean isVerified(){return verified;}
    public void makeVerified(){
        verified=true;
    }
    public void attachCaptcha(String captchaId){
        this.captchaId=captchaId;
    }
    public String getCaptchaId(){return captchaId;}
}
