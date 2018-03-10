import iovi.client.Client;
import iovi.client.ClientToken;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**Тест класса ClientToken */
public class ClientTokenTest {

    /**Тест создания ненулевого Client*/
    @Test
    public void notNullClientTokenData(){
        String publicKey="123456789213456";
        ClientToken token =new ClientToken(publicKey);
        assertFalse(token ==null);
        assertTrue(token.getClientPublicKey()==publicKey);
        assertFalse(token.getTokenString()==null);
    }
}