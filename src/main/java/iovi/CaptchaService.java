package iovi;

public interface CaptchaService {
    Captcha GetNewCaptcha();
    boolean CheckCaptchaText(int captchaId, String captchaText);
}
