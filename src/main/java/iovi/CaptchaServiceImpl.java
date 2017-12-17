package iovi;


public class CaptchaServiceImpl implements CaptchaService{
    public Captcha GetNewCaptcha(){
            return new Captcha(1,6);
    }
    public boolean CheckCaptchaText(int captchaId, String captchaText){
        return true;
    }
}
