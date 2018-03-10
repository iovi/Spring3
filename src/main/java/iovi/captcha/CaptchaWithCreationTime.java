package iovi.captcha;

import java.util.Calendar;
import java.util.Date;

/**Вспомогательный класс для хранения captcha и времени ее создания */
class CaptchaWithCreationTime extends Object{
    Captcha captcha;
    Date creationTime;
    public CaptchaWithCreationTime(Captcha captcha){
        this.captcha=captcha;
        this.creationTime= Calendar.getInstance().getTime();
    }
    public Captcha getCaptcha(){
        return captcha;
    }
    public Date getCreationTime(){
        return creationTime;
    }
}
