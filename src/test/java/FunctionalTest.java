import iovi.Main;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**Функциональный тест приложения*/
public class FunctionalTest {

    static final String REGISTER_URL="http://localhost:8080/client/register";
    static final String NEW_URL="http://localhost:8080/captcha/new";
    static final String IMAGE_URL="http://localhost:8080/captcha/image";
    static final String SOLVE_URL="http://localhost:8080/captcha/solve";
    static final String VERIFY_URL="http://localhost:8080/captcha/verify";

    /**Запуск приложения*/
    @BeforeClass
    public static void startMain(){
        String[] args = new String[]{};
        Main.main(args);
        System.setProperty("production","false");
    }

    /**
     * Проверка работы приложения с одним отправщиком запросов
     * */
    @Test
    public void oneSenderWorks(){
        CaptchaRequestSender sender=new CaptchaRequestSender();
        assertEquals(200,sender.registerClient(REGISTER_URL,"POST"));
        assertEquals(200,sender.newCaptcha(NEW_URL,"GET"));
        assertEquals(200,sender.getImage(IMAGE_URL,"GET"));
        assertEquals(200,sender.solveCaptcha(SOLVE_URL,"POST"));
        assertEquals(200,sender.verifyClient(VERIFY_URL,"GET"));
        assertEquals(true,sender.success);
    }

    /**Проверка одновременной работы приложения с несколькими отправщиками запросов*/
    @Test
    public void manySendersWork(){
        Runnable senderActivity=()->{
            CaptchaRequestSender sender=new CaptchaRequestSender();
            assertEquals(200,sender.registerClient(REGISTER_URL,"POST"));
            assertEquals(200,sender.newCaptcha(NEW_URL,"GET"));
            assertEquals(200,sender.getImage(IMAGE_URL,"GET"));
            assertEquals(200,sender.solveCaptcha(SOLVE_URL,"POST"));
            assertEquals(200,sender.verifyClient(VERIFY_URL,"GET"));
            assertEquals(true,sender.success);
        };

        Thread threads[]=new Thread[1000];
        for (int i=0;i<1000;i++){
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

    /**
     * <p>Проверка контроля приложением текста captcha</p>
     * <p>При отправке на {@link #CHECK_URL} текста из предварительного запроса на {@link #GET_URL} - возвращается статус 200.
     * При отправке другого текста - ошибка 422</p>
     * *
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

    /**
     * <p>Проверка контроля приложением таймаута</p>
     * <p>Запрос на {@link #CHECK_URL} с корректыми данными выдает ошибку 422 после таймаута</p>
     * *
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

    /**
     * <p>Проверка контроля приложением количества вызовов</p>
     * <p>Первый запрос {@link #CHECK_URL} возвращает одобренный статус 200, второй - 422</p>
     * *
    @Test
    public void checksSecondCall(){
        CaptchaRequestSender sender = new CaptchaRequestSender();
        assertEquals(200, sender.getCaptchaAndStoreData(GET_URL));
        assertEquals(200, sender.checkCaptchaByStoredData(CHECK_URL));
        assertEquals(422, sender.checkCaptchaByStoredData(CHECK_URL));
    }*/
}
