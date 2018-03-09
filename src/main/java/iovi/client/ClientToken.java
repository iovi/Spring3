package iovi.client;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**Проверочный токен, выдаваемый клиенту*/
public class ClientToken {
    String tokenString;
    Date creationTime;
    String clientPublicKey;

    public ClientToken(String clientPublicKey){
        tokenString="token-"+ UUID.randomUUID().toString();
        creationTime=Calendar.getInstance().getTime();
        this.clientPublicKey=clientPublicKey;
    }
    public String getTokenString(){return tokenString;}
    public Date getCreationTime(){
        return creationTime;
    }
    public String getClientPublicKey(){return clientPublicKey;}
}
