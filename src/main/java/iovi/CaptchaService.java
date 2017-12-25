package iovi;


import javafx.util.Pair;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.*;

public class CaptchaService{

    private class CaptchaWithCreationTime extends Object{
        Captcha captcha;
        Date creationTime;
        public CaptchaWithCreationTime(Captcha captcha){
            this.captcha=captcha;
            this.creationTime=Calendar.getInstance().getTime();
        }
        public Captcha getCaptcha(){
            return captcha;
        }
        public Date getCreationTime(){
            return creationTime;
        }
    }

    long timeout;
    Map<String,CaptchaWithCreationTime> captchas;

    public CaptchaService(long timeoutInMs){
        captchas =new HashMap<>();
        timeout=timeoutInMs;
    }

    public Pair<String,Captcha> getNewCaptcha(){
        Captcha captcha=new Captcha(6);
        String uid= UUID.randomUUID().toString();

        captchas.put(uid,new CaptchaWithCreationTime(captcha));
        return new Pair(uid,captcha);
    }

    public Pair<String,Captcha> getNewCaptcha(int textLength){
        if (textLength<6)
            textLength=6;
        Captcha captcha=new Captcha(textLength);
        String uid= UUID.randomUUID().toString();
        CaptchaWithCreationTime captchaData=new CaptchaWithCreationTime(captcha);
        captchas.put(uid,captchaData);
        return new Pair(uid,captcha);
    }

    public boolean checkCaptchaText(String captchaId, String captchaText){
        CaptchaWithCreationTime captchaData= captchas.get(captchaId);
        if (captchaData==null)
            return false;
        if (captchaData.getCaptcha().getText().equals(captchaText) &&
                captchaData.getCreationTime().getTime()+timeout> new Date().getTime()){
            captchas.remove(captchaId);
            return true;
        }
        else return false;
    }
    public BufferedImage getCaptchaImage(String uid){
        return captchas.get(uid).getCaptcha().getImage();
    }
}
