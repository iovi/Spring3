import iovi.Main;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**Функциональный тест приложения*/
public class FunctionalTest {

    static final String GET_URL="http://localhost:8080/captcha";
    static final String CHECK_URL="http://localhost:8080/captcha/check";

    /**Запуск приложения*/
    @BeforeClass
    public static void startMain(){
        String[] args = new String[]{};
        Main.main(args);
    }

    /**Проверка работы приложения с одним отправщиком запросов*/
    @Test
    public void oneSenderWorks(){
        CaptchaRequestSender sender=new CaptchaRequestSender();
        assertEquals(200,sender.getCaptchaAndStoreData(GET_URL));
        assertEquals(200,sender.checkCaptchaByStoredData(CHECK_URL));
    }

    /**Проверка одновременной работы приложения с несколькими отправщиками запросов*/
    @Test
    public void manySendersWork(){
        Runnable senderActivity=()->{
                CaptchaRequestSender sender = new CaptchaRequestSender();
                assertEquals(200, sender.getCaptchaAndStoreData(GET_URL));
                assertEquals(200, sender.checkCaptchaByStoredData(CHECK_URL));
            };

        Thread threads[]=new Thread[100];
        for (int i=0;i<100;i++){
            threads[i]=new Thread(senderActivity);
            threads[i].start();
        }

        try {
            for (Thread thread : threads)
                thread.join();
        }catch(InterruptedException e){
            fail();
        }
    }


    @Test
    public void checksValidText(){
        CaptchaRequestSender sender = new CaptchaRequestSender();
        assertEquals(200, sender.getCaptchaAndStoreData(GET_URL));

        String correctText=sender.captchaText;
        StringBuilder incorrectTextBuilder = new StringBuilder(correctText);
        incorrectTextBuilder.setCharAt(0, (char)(incorrectTextBuilder.charAt(0)+1));
        String incorrectText=incorrectTextBuilder.toString();

        sender.captchaText=incorrectText;
        assertEquals(422, sender.checkCaptchaByStoredData(CHECK_URL));

        sender.captchaText=correctText;
        assertEquals(200, sender.checkCaptchaByStoredData(CHECK_URL));
    }

    @Test
    public void checksTimeout(){
        CaptchaRequestSender sender = new CaptchaRequestSender();
        assertEquals(200, sender.getCaptchaAndStoreData(GET_URL));
        try{
            Thread.sleep(60001);
        }catch (InterruptedException e){
            fail();
        }
        assertEquals(422, sender.checkCaptchaByStoredData(CHECK_URL));
    }

    @Test
    public void checksSecondCall(){
        CaptchaRequestSender sender = new CaptchaRequestSender();
        assertEquals(200, sender.getCaptchaAndStoreData(GET_URL));
        assertEquals(200, sender.checkCaptchaByStoredData(CHECK_URL));
        assertEquals(422, sender.checkCaptchaByStoredData(CHECK_URL));
    }

}
