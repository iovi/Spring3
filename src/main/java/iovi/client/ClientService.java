package iovi.client;


import java.util.*;

public class ClientService {
   Map<String,Client> clients;
   Map<String,ClientToken> tokens;
   long clientTimeout;
   long tokenTimeout;

   public ClientService(long clientTimeout, long tokenTimeout){

       clients= Collections.synchronizedMap(new HashMap<>());
       tokens =Collections.synchronizedMap(new HashMap<>());
       this.tokenTimeout=tokenTimeout;
       this.clientTimeout=clientTimeout;
   }
   public Client registerClient(){
       Client client =new Client();
       clients.put(client.publicKey, client);
       return client;
   }
   public boolean checkClientExistence(String publicKey){
       Client client =clients.get(publicKey);
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
           Client client=clients.get(publicKey);
           client.attachCaptcha(captchaId);
       }
   }
   public boolean checkCaptchaAttachedToClient(String captchaId,String publicKey){
       if (checkClientExistence(publicKey)){
            Client client=clients.get(publicKey);
            if (captchaId.equals(client.getCaptchaId()))
                return true;
       }
       return false;
   }

   public String getTokenForClient(String publicKey){
       if (checkClientExistence(publicKey)){
            ClientToken token=new ClientToken(publicKey);
            tokens.put(token.getTokenString(),token);
            return token.getTokenString();
       } else{
            return null;
       }

   }
   public String verifyClientToken(String secretKey,String tokenString){
       ClientToken token= tokens.get(tokenString);
       if (token==null){
           return "IncorrectToken";
       } else{
           tokens.remove(tokenString);
           if (token.getCreationTime().getTime()+tokenTimeout< new Date().getTime())
               return "TokenIsExpired";

           Client client=clients.get(token.getClientPublicKey());
           if (isClientExpired(client)){
               clients.remove(client.getPublicKey());
               return "ClientIsExpired";
           }
           if (!secretKey.equals(client.getSecretKey()))
               return "IncorrectSecretKey";
           else{
               clients.remove(token.getClientPublicKey());
               return null;
           }
       }
   }
    boolean isClientExpired(Client client){
        if (client.getCreationTime().getTime()+clientTimeout> new Date().getTime())
            return false;
        else
            return true;


    }
}
