package iovi.client;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientService {
   Map<String,ClientData> clients;
   Map<String,ClientData> clientTokens;

   public ClientService(){
       clients= Collections.synchronizedMap(new HashMap<>());
   }
   public ClientData registerClient(){
       ClientData clientData=new ClientData();
       clients.put(clientData.publicKey,clientData);
       return clientData;
   }
   public boolean checkClientExistence(String publicKey){
       return clients.containsKey(publicKey);
   }
   public void attachCaptchaToClient(String captchaId, String publicKey){
        ClientData client=clients.get(publicKey);
        client.attachCaptcha(captchaId);
   }
   public boolean checkCaptchaAttachedToClient(String captchaId,String publicKey){
       ClientData client=clients.get(publicKey);
       if (captchaId.equals(client.getCaptchaId()))
           return true;
       else
           return false;
   }
   public static String generateToken(){
       return "token-"+UUID.randomUUID().toString();
   }
   public String getTokenForClient(String publicKey){
       String token=generateToken();
       clientTokens.put(token,clients.get(publicKey));
       return token;
   }
}
