package iovi;


import javafx.util.Pair;

import java.time.LocalDateTime;
import java.util.*;

public class CaptchaService{

    private class CaptchaData extends Object{
        String captchaText;
        Date creationTime;
        public CaptchaData(String captchaText){
            this.captchaText=captchaText;
            this.creationTime=Calendar.getInstance().getTime();
        }
        public String getCaptchaText(){
            return captchaText;
        }
        public Date getCreationTime(){
            return creationTime;
        }
    }
    long timeout;
    Map<String,CaptchaData> captchas;

    public CaptchaService(long timeoutInMs){
        captchas =new HashMap<>();
        timeout=timeoutInMs;
    }

    public Pair<String,Captcha> getNewCaptcha(){
        Captcha captcha=new Captcha(6);
        String uid= UUID.randomUUID().toString();
        CaptchaData captchaData=new CaptchaData(captcha.getText());
        captchas.put(uid,captchaData);
        return new Pair(uid,captcha);
    }

    public Pair<String,Captcha> getNewCaptcha(int textLength){
        if (textLength<6)
            textLength=6;
        Captcha captcha=new Captcha(textLength);
        String uid= UUID.randomUUID().toString();
        CaptchaData captchaData=new CaptchaData(captcha.getText());
        captchas.put(uid,captchaData);
        return new Pair(uid,captcha);
    }

    public boolean checkCaptchaText(String captchaId, String captchaText){
        CaptchaData captchaData= captchas.get(captchaId);
        if (captchaData==null)
            return false;
        if (captchaData.getCaptchaText().equals(captchaText) &&
                captchaData.getCreationTime().getTime()+timeout< new Date().getTime()){
            captchas.remove(captchaId);
            return true;
        }
        else return false;
    }
}
