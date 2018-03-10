package iovi.client;


import java.util.*;

public class ClientService {
   /**key-value хранилище клиентов, key - публичный ключ клиента, value - клиент*/
   Map<String,Client> clients;
   /**key-value хранилище токенов, key - секретный ключ клиента, value - токен*/
   Map<String,ClientToken> tokens;
   long clientTimeout;

   public ClientService(long clientTimeout){

       clients=new HashMap<>();
       tokens=Collections.synchronizedMap(new HashMap<>());
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
            tokens.put(clients.get(publicKey).getSecretKey(),token);
            return token.getTokenString();
       } else{
            return null;
       }

   }
   public String verifyClientToken(String secretKey,String tokenString){
       ClientToken token= tokens.get(secretKey);
       if (token==null){
           return "NoTokenForSuchKey";
       } else{
           tokens.remove(secretKey);
           if (!token.getTokenString().equals(tokenString))
               return "IncorrectToken";
           Client client=clients.get(token.getClientPublicKey());
           if (isClientExpired(client)){
               clients.remove(client.getPublicKey());
               return "ClientIsExpired";
           } else {
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
