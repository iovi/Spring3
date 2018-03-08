package iovi.client;


import java.util.*;

public class ClientService {
   Map<String,ClientData> clients;
   Map<String,ClientData> clientTokens;
   long timeout;

   public ClientService(long timeout){

       clients= Collections.synchronizedMap(new HashMap<>());
       clientTokens =Collections.synchronizedMap(new HashMap<>());
       this.timeout=timeout;
   }
   public ClientData registerClient(){
       ClientData clientData=new ClientData();
       clients.put(clientData.publicKey,clientData);
       return clientData;
   }
   public boolean checkClientExistence(String publicKey){
       ClientData client =clients.get(publicKey);
       if (client==null)
           return false;
       if (isClientExpired(client)){
           clients.remove(publicKey);
           return false;
       }
       return true;
   }
   public void attachCaptchaToClient(String captchaId, String publicKey){
       if (checkClientExistence(publicKey)){
           ClientData client=clients.get(publicKey);
           client.attachCaptcha(captchaId);
       }
   }
   public boolean checkCaptchaAttachedToClient(String captchaId,String publicKey){
       if (checkClientExistence(publicKey)){
            ClientData client=clients.get(publicKey);
            if (captchaId.equals(client.getCaptchaId()))
                return true;
       }
       return false;
   }
   public static String generateToken(){
       return "token-"+UUID.randomUUID().toString();
   }
   public String getTokenForClient(String publicKey){
       if (checkClientExistence(publicKey)){
            String token=generateToken();
            clientTokens.put(token,clients.get(publicKey));
            return token;
       } else{
            return null;
       }

   }
   public String verifyClientToken(String secretKey,String token){
       ClientData client=clientTokens.get(token);
       if (client==null)
           return "IncorrectToken";
       if (isClientExpired(client))
           return "ClientIsExpired";
       if (!secretKey.equals(client.getSecretKey()))
           return "IncorrectSecretKey";
       else{
           clientTokens.remove(token);
           return null;
       }
   }
   boolean isClientExpired(ClientData client){
       if (client.getCreationTime().getTime()+timeout> new Date().getTime())
           return false;
       else
           return true;


   }
}
