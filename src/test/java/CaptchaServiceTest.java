import iovi.Captcha;
import iovi.CaptchaService;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CaptchaServiceTest {
    CaptchaService captchaService;
    long timeout=4000;

    @Before
    public void init(){
        captchaService=new CaptchaService(timeout);
    }

    @Test
    public void getsNotNullCaptcha(){


        Pair<String,Captcha> captchaWithId1=captchaService.getNewCaptcha();
        assertFalse(captchaWithId1.getKey()==null);
        assertFalse(captchaWithId1.getValue().getText()==null);
        assertFalse(captchaWithId1.getValue().getImage()==null);

        Pair<String,Captcha> captchaWithId2=captchaService.getNewCaptcha(10);
        assertFalse(captchaWithId2.getKey()==null);
        assertFalse(captchaWithId2.getValue().getText()==null);
        assertFalse(captchaWithId2.getValue().getImage()==null);
    }

    @Test
    public void getsValidLengthCaptcha(){

        Pair<String,Captcha> captchaWithId=captchaService.getNewCaptcha();

        assertEquals(captchaWithId.getValue().getText().length(),6);

        for (int i=-1;i<20;i++) {
            captchaWithId = captchaService.getNewCaptcha(i);
            if (i <= 6)
                assertEquals(captchaWithId.getValue().getText().length(), 6);
            else
                assertEquals(captchaWithId.getValue().getText().length(), i);
        }
    }
    @Test
    public void checksCaptchaText(){
        Pair<String,Captcha> captchaWithId=captchaService.getNewCaptcha();
        assertTrue(captchaService.checkCaptchaText(captchaWithId.getKey(),captchaWithId.getValue().getText()));

        StringBuilder incorrectText = new StringBuilder(captchaWithId.getValue().getText());
        incorrectText.setCharAt(0, (char)(incorrectText.charAt(0)+1));
        assertFalse(captchaService.checkCaptchaText(captchaWithId.getKey(),incorrectText.toString()));

        assertFalse(captchaService.checkCaptchaText(captchaWithId.getKey(), null));
        assertFalse(captchaService.checkCaptchaText(null, captchaWithId.getValue().getText()));
    }

    @Test
    public void checksTimeout() throws InterruptedException{
        Pair<String,Captcha> captchaWithId=captchaService.getNewCaptcha();
        Thread.sleep(timeout+1);
        assertFalse(captchaService.checkCaptchaText(captchaWithId.getKey(),captchaWithId.getValue().getText()));
    }

    @Test
    public void checksManyCallings(){
        Pair<String,Captcha> captchaWithId=captchaService.getNewCaptcha();
        assertTrue(captchaService.checkCaptchaText(captchaWithId.getKey(),captchaWithId.getValue().getText()));
        assertFalse(captchaService.checkCaptchaText(captchaWithId.getKey(),captchaWithId.getValue().getText()));
    }

    @Test

    public void  worksWithMultithreading() {
        for(int i=0;i<50;i++){
            Runnable captchaUser= () -> {
                    Pair<String,Captcha> captchaWithId=captchaService.getNewCaptcha();
                    assertTrue(captchaService.checkCaptchaText(captchaWithId.getKey(),captchaWithId.getValue().getText()));
            };
            new Thread(captchaUser).start();
        }
    }


}
