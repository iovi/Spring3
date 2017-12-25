import iovi.Captcha;
import iovi.CaptchaService;
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

        String captchaId1=captchaService.getNewCaptchaId();
        assertFalse(captchaId1==null);
        assertFalse(captchaService.getCaptchaText(captchaId1)==null);
        assertFalse(captchaService.getCaptchaImage(captchaId1)==null);

        String captchaId2=captchaService.getNewCaptchaId(10);
        assertFalse(captchaId2==null);
        assertFalse(captchaService.getCaptchaText(captchaId2)==null);
        assertFalse(captchaService.getCaptchaImage(captchaId2)==null);
    }

    @Test
    public void getsValidLengthCaptcha(){

        String captchaId=captchaService.getNewCaptchaId();
        assertEquals(captchaService.getCaptchaText(captchaId).length(),6);

        for (int i=-1;i<20;i++) {
            captchaId = captchaService.getNewCaptchaId(i);
            if (i <= 6)
                assertEquals(captchaService.getCaptchaText(captchaId).length(), 6);
            else
                assertEquals(captchaService.getCaptchaText(captchaId).length(), i);
        }
    }
    @Test
    public void checksCaptchaText(){
        String captchaId=captchaService.getNewCaptchaId();
        assertTrue(captchaService.checkCaptchaText(captchaId,captchaService.getCaptchaText(captchaId)));

        StringBuilder incorrectText = new StringBuilder(captchaService.getCaptchaText(captchaId));
        incorrectText.setCharAt(0, (char)(incorrectText.charAt(0)+1));
        assertFalse(captchaService.checkCaptchaText(captchaId,incorrectText.toString()));

        assertFalse(captchaService.checkCaptchaText(captchaId, null));
    }

    @Test
    public void checksTimeout() throws InterruptedException{
        String captchaId=captchaService.getNewCaptchaId();
        Thread.sleep(timeout+1);
        assertFalse(captchaService.checkCaptchaText(captchaId,captchaService.getCaptchaText(captchaId)));
    }

    @Test
    public void checksManyCallings(){
        String captchaId=captchaService.getNewCaptchaId();
        assertTrue(captchaService.checkCaptchaText(captchaId,captchaService.getCaptchaText(captchaId)));
        assertFalse(captchaService.checkCaptchaText(captchaId,captchaService.getCaptchaText(captchaId)));
    }

    @Test

    public void  worksWithMultithreading() {
        for(int i=0;i<50;i++){
            Runnable captchaUser= () -> {
                    String captchaId=captchaService.getNewCaptchaId();
                    assertTrue(captchaService.checkCaptchaText(captchaId,captchaService.getCaptchaText(captchaId)));
            };
            new Thread(captchaUser).start();
        }
    }


}
