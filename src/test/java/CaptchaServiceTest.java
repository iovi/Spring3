import iovi.Captcha;
import iovi.CaptchaService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**Тест класса CaptchaService */
public class CaptchaServiceTest {
    CaptchaService captchaService;
    long timeout=4000;

    @Before
    public void init(){
        captchaService=new CaptchaService(timeout);
    }

    /**Тест создания ненулевой Captcha*/
    @Test
    public void getNotNullCaptcha(){

        String captchaId=captchaService.getNewCaptchaId();
        assertFalse(captchaId==null);
        assertFalse(captchaService.getCaptchaText(captchaId)==null);
        assertFalse(captchaService.getCaptchaImage(captchaId)==null);
    }


    /**
     * Тест проверки текста методом checkCaptchaText(String,String).
     * Выдает true на совпадающий верный текст, false - на несовпадающий
     */
    @Test
    public void checkCaptchaText_ValidText(){
        String captchaId=captchaService.getNewCaptchaId();
        String captchaText=captchaService.getCaptchaText(captchaId);
        assertTrue(captchaService.checkCaptchaText(captchaId,captchaText));

        StringBuilder incorrectText = new StringBuilder(captchaText);
        incorrectText.setCharAt(0, (char)(incorrectText.charAt(0)+1));
        assertFalse(captchaService.checkCaptchaText(captchaId,incorrectText.toString()));

        assertFalse(captchaService.checkCaptchaText(captchaId, null));
    }

    /**
     * Тест проверки истечения таймаута методом checkCaptchaText(String,String).
     * Выдает true до таймаута, false после таймаута
     */
    @Test
    public void checkCaptchaText_PassTimeout() throws InterruptedException{
        String captchaId=captchaService.getNewCaptchaId();
        Thread.sleep(timeout-10);
        assertTrue(captchaService.checkCaptchaText(captchaId,captchaService.getCaptchaText(captchaId)));
        Thread.sleep(20);
        assertFalse(captchaService.checkCaptchaText(captchaId,captchaService.getCaptchaText(captchaId)));
    }

    /**
     * Тест проверки количества вызовов истечения методом checkCaptchaText(String,String).
     * Выдает true при первой проверке, false при второй с теми же входными данными
     */
    @Test
    public void checkCaptchaText_SecondCall(){
        String captchaId=captchaService.getNewCaptchaId();
        String captchaText=captchaService.getCaptchaText(captchaId);
        assertTrue(captchaService.checkCaptchaText(captchaId,captchaText));
        assertFalse(captchaService.checkCaptchaText(captchaId,captchaText));
    }


}
