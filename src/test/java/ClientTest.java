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

}
