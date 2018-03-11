import iovi.client.Client;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**Тест класса Client */
public class ClientTest {

    /**Тест создания ненулевого Client*/
    @Test
    public void notNullClient(){
        Client client =new Client();
        assertFalse(client ==null);
        assertFalse(client.getPublicKey()==null);
        assertFalse(client.getSecretKey()==null);
    }

    /**Тест корректности времени создания Client*/
    @Test
    public void correctClientCreationTime(){
        long time1=new Date().getTime();
        Client client =new Client();
        long time2=new Date().getTime();
        assertFalse(client.getCreationTime()==null);
        assertTrue(client.getCreationTime().getTime()>=time1 && client.getCreationTime().getTime()<=time2);
    }

    /**Тест корректности привязки captcha к клиенту и проверки этой привязки*/
    @Test
    public void attachingTest(){
        Client client =new Client();
        String captchaId1="2132453aa41343", captchaId2="546351aebce44456",captchaId3="0cdacad3646";
        client.attachCaptcha(captchaId1);
        client.attachCaptcha(captchaId2);
        assertTrue(client.checkCaptchaAttached(captchaId1));
        assertTrue(client.checkCaptchaAttached(captchaId2));
        assertFalse(client.checkCaptchaAttached(captchaId3));
    }

}
