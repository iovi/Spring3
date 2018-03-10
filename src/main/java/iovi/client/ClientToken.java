package iovi.client;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**Проверочный токен, выдаваемый клиенту*/
public class ClientToken {
    String tokenString;
    String clientPublicKey;

    public ClientToken(String clientPublicKey){
        tokenString="token-"+ UUID.randomUUID().toString();
        this.clientPublicKey=clientPublicKey;
    }
    public String getTokenString(){return tokenString;}
    public String getClientPublicKey(){return clientPublicKey;}
}
