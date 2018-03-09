import iovi.Main;
import org.junit.BeforeClass;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**Проверка работы всех запросов на веб-сервис*/
public class RequestsTest {
    static CaptchaRequestSender sender;

    /**Запуск приложения*/
    @BeforeClass
    public static void startMain(){
        String[] args = new String[]{};
        Main.main(args);
    }
    /**Проверка работы регистрации клиента */
    @Test
    public void registerTest(){
        sender=new CaptchaRequestSender();
        assertEquals(200,sender.registerClient("http://localhost:8080/client/register","POST"));
        assertTrue(sender.publicKey!=null);
        assertTrue(sender.secretKey!=null);
    }

    /**Проверка работы создвания новой CAPTCHA*/
    @Test
    public void createCaptchaTest(){
        System.setProperty("production","false");
        registerTest();
        assertEquals(200,sender.newCaptcha("http://localhost:8080/captcha/new","GET"));
        assertTrue(sender.captchaId!=null);
        assertTrue(sender.captchaText!=null);
    }

    /**Проверка заполнения поля ответа "answer" (в запросе создания новой CAPTCHA) в зависимости от параметра production. */
    @Test
    public void createCaptcha_returnsAnswer(){
        registerTest();
        System.setProperty("production","true");
        sender.newCaptcha("http://localhost:8080/captcha/new","GET");
        assertTrue(sender.captchaId!=null);
        assertTrue(sender.captchaText==null);
        System.setProperty("production","false");
        sender.newCaptcha("http://localhost:8080/captcha/new","GET");
        assertTrue(sender.captchaText!=null);
    }

    @Test
    public void getCaptchaImageTest(){
        createCaptchaTest();
        assertEquals(200,sender.getImage("http://localhost:8080/captcha/image","GET"));
    }

    @Test
    public void solveTest(){
        createCaptchaTest();
        assertEquals(200,sender.solveCaptcha("http://localhost:8080/captcha/solve","POST"));
        assertTrue(sender.token!=null);
    }

    @Test
    public void verifyTest(){
        solveTest();
        assertEquals(200,sender.verifyClient("http://localhost:8080/captcha/verify","GET"));
        assertEquals(true,sender.success);
    }

    /**Проверка возвращения ответа 403 при запросах от незарегистрированного клиента */
    @Test
    public void unregisteredPublicKey(){
        registerTest();
        StringBuilder incorrectKey = new StringBuilder(sender.publicKey);
        incorrectKey.setCharAt(0, (char)(sender.publicKey.charAt(0)+1));
        sender.publicKey=incorrectKey.toString();
        assertEquals(403,sender.newCaptcha("http://localhost:8080/captcha/new","GET"));
        assertEquals(403,sender.getImage("http://localhost:8080/captcha/image","GET"));
        assertEquals(403,sender.solveCaptcha("http://localhost:8080/captcha/solve","POST"));
    }

}
