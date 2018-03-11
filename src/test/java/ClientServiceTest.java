import iovi.client.Client;
import iovi.client.ClientService;
import org.junit.Test;


import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

/**Тест класса ClientService */
public class ClientServiceTest {
    static final int TIMEOUT=10000;

    /**
     * <p>Тест метода {@link ClientService#registerClient()}. </p>
     * <p>Метод должен возвращать клиента c корректным временем создания и ненулевыми публичным и секретным ключами</p>
     * */
    @Test
    public void registerClientTest(){
        ClientService clientService =new ClientService(TIMEOUT);
        long time1=new Date().getTime();
        Client client=clientService.registerClient();
        long time2=new Date().getTime();
        assertFalse(client.getPublicKey()==null);
        assertFalse(client.getSecretKey()==null);
        assertTrue(client.getCreationTime().getTime()>=time1 && client.getCreationTime().getTime()<=time2);
    }

    /**
     * <p>Тест метода {@link ClientService#checkClientExistence(String)}. </p>
     * */
    @Test
    public void checkClientExistenceTest(){
        ClientService clientService =new ClientService(TIMEOUT);
        Client client=clientService.registerClient();
        assertTrue(clientService.checkClientExistence(client.getPublicKey()));
        try{
            Thread.sleep(TIMEOUT+10);
        }catch (InterruptedException e){
            fail();
        }
        assertFalse(clientService.checkClientExistence(client.getPublicKey()));
    }

    /**
     * <p>Тест методов {@link ClientService#attachCaptchaToClient(String, String)}
     * и {@link ClientService#checkClientExistence(String)}. </p>
     * */
    @Test
    public void attachingTest(){
        ClientService clientService =new ClientService(TIMEOUT);
        Client client=clientService.registerClient();
        String captchaId1="1111111111", captchaId2="222222222",captchaId3="33333333333";
        clientService.attachCaptchaToClient(captchaId1,client.getPublicKey());
        clientService.attachCaptchaToClient(captchaId2,client.getPublicKey());
        assertTrue(clientService.checkCaptchaAttachedToClient(captchaId1,client.getPublicKey()));
        assertTrue(clientService.checkCaptchaAttachedToClient(captchaId2,client.getPublicKey()));
        assertFalse(clientService.checkCaptchaAttachedToClient(captchaId3,client.getPublicKey()));
    }
    /**
     * <p>Тест методов {@link ClientService#getTokenForClient(String)}. </p>
     * */
    @Test
    public void getTokenForClientTest(){
        ClientService clientService =new ClientService(TIMEOUT);
        Client client=clientService.registerClient();
        String tokenString=clientService.getTokenForClient(client.getPublicKey());
        assertTrue(tokenString!=null);
    }
    /**Тест метода {@link ClientService#verifyClientToken(String, String)} на отсутствие ошибок только при первом вызове */
        @Test
        public void verifyClientToken_SecondCall(){
            ClientService clientService =new ClientService(TIMEOUT);
            Client client=clientService.registerClient();
            String tokenString=clientService.getTokenForClient(client.getPublicKey());
            assertEquals(null,clientService.verifyClientToken(client.getSecretKey(),tokenString));
            assertEquals("NoTokenForSuchKey",clientService.verifyClientToken(client.getSecretKey(),tokenString));
    }

    /**
     * <p>Тест метода {@link ClientService#verifyClientToken(String, String)} на правильный код ответа
     * при некорреткном секретном ключе и некорректном токене</p>
     * */
    @Test
    public void verifyClientToken_IncorrectInput(){
        ClientService clientService =new ClientService(TIMEOUT);
        Client client=clientService.registerClient();
        String tokenString=clientService.getTokenForClient(client.getPublicKey());

        StringBuilder incorrectKey = new StringBuilder(client.getSecretKey());
        incorrectKey.setCharAt(0, (char)(incorrectKey.charAt(0)+1));
        assertEquals("NoTokenForSuchKey",clientService.verifyClientToken(incorrectKey.toString(),tokenString));

        StringBuilder incorrectToken = new StringBuilder(client.getSecretKey());
        incorrectToken.setCharAt(0, (char)(incorrectToken.charAt(0)+1));
        assertEquals("IncorrectToken",clientService.verifyClientToken(client.getSecretKey(),incorrectToken.toString()));
    }
    /**
     * <p>Тест метода {@link ClientService#verifyClientToken(String, String)} на правильный код ответа
     * при истечении таймаута клиента</p>
     * */
    @Test
    public void verifyClientToken_Timeout(){
        ClientService clientService =new ClientService(TIMEOUT);
        Client client=clientService.registerClient();
        String tokenString=clientService.getTokenForClient(client.getPublicKey());
        try{
            Thread.sleep(TIMEOUT+10);
        }catch (InterruptedException e){
            fail();
        }
        assertEquals("ClientIsExpired",clientService.verifyClientToken(client.getSecretKey(),tokenString));
    }
}
