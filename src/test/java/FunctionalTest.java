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

    CaptchaRequestSender internalSender;

    /**Запуск приложения*/
    @BeforeClass
    public static void startMain(){
        String[] args = new String[]{};
        Main.main(args);
        System.setProperty("production","false");
    }

    /**Проверка запроса регистрации клиента */
    @Test
    public void registerTest(){
        internalSender=new CaptchaRequestSender();
        assertEquals(200,internalSender.registerClient(REGISTER_URL,"POST"));
        assertTrue(internalSender.publicKey!=null);
        assertTrue(internalSender.secretKey!=null);
    }

    /**Проверка запроса создания новой CAPTCHA с корректными даными*/
    @Test
    public void createCaptchaTest(){
        registerTest();
        assertEquals(200,internalSender.newCaptcha(NEW_URL,"GET"));
        assertTrue(internalSender.captchaId!=null);
        assertTrue(internalSender.captchaText!=null);
    }

    /**Проверка заполнения поля ответа "answer" (на запрос создания новой CAPTCHA) в зависимости от параметра production. */
    @Test
    public void createCaptcha_returnsAnswer(){
        registerTest();
        System.setProperty("production","true");
        internalSender.newCaptcha(NEW_URL,"GET");
        assertTrue(internalSender.captchaId!=null);
        assertTrue(internalSender.captchaText==null);
        System.setProperty("production","false");
        internalSender.newCaptcha(NEW_URL,"GET");
        assertTrue(internalSender.captchaText!=null);
    }

    /**Проверка запроса показа картинки CAPTCHA с корректными даными*/
    @Test
    public void getCaptchaImageTest(){
        createCaptchaTest();
        assertEquals(200,internalSender.getImage(IMAGE_URL,"GET"));
    }

    /**Проверка запроса на разгадку CAPTCHA с корректными даными*/
    @Test
    public void solveTest(){
        createCaptchaTest();
        assertEquals(200,internalSender.solveCaptcha(SOLVE_URL,"POST"));
        assertTrue(internalSender.token!=null);
    }

    /**Проверка запроса с неправильной разгадкой CAPTCHA - должен вернуть код 422*/
    @Test
    public void solve_IncorrectText(){
        createCaptchaTest();
        StringBuilder incorrectText = new StringBuilder(internalSender.captchaText);
        incorrectText.setCharAt(0, (char)(incorrectText.charAt(0)+1));
        internalSender.captchaText=incorrectText.toString();
        assertEquals(422,internalSender.solveCaptcha(SOLVE_URL,"POST"));
    }


    /**Проверка запроса верификации клиента с корректными даными*/
    @Test
    public void verifyTest(){
        solveTest();
        assertEquals(200,internalSender.verifyClient(VERIFY_URL,"GET"));
        assertEquals(true,internalSender.success);
    }

    /**Проверка запроса с неправильным токеном - должен вернуть код 422*/
    @Test
    public void verify_IncorrectToken(){
        solveTest();
        StringBuilder incorrectToken = new StringBuilder(internalSender.token);
        incorrectToken.setCharAt(10, (char)(incorrectToken.charAt(10)+1));
        internalSender.token=incorrectToken.toString();
        assertEquals(422,internalSender.verifyClient(VERIFY_URL,"GET"));
        assertEquals(false,internalSender.success);
    }

    /**Проверка возвращения ответа 403 при запросах от незарегистрированного клиента */
    @Test
    public void unregisteredPublicKey(){
        registerTest();
        StringBuilder incorrectKey = new StringBuilder(internalSender.publicKey);
        incorrectKey.setCharAt(0, (char)(internalSender.publicKey.charAt(0)+1));
        internalSender.publicKey=incorrectKey.toString();
        assertEquals(403,internalSender.newCaptcha(NEW_URL,"GET"));
        assertEquals(403,internalSender.getImage(IMAGE_URL,"GET"));
        assertEquals(403,internalSender.solveCaptcha(SOLVE_URL,"POST"));
    }

    /**Проверка возврата ответа 422 при истечении таймаута Captcha, равного значению параметра ttl*/
    @Test
    public void solveCaptcha_PassTimeout(){
        Integer timeout=5;
        System.setProperty("ttl",timeout.toString());
        createCaptchaTest();
        try{
            Thread.sleep(timeout*1000+1);
        }catch (InterruptedException e){
            fail();
        }
        assertEquals(422,internalSender.solveCaptcha(SOLVE_URL,"POST"));
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

}
