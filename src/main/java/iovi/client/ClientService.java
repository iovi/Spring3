package iovi.client;

import iovi.OldObjectsRemover;

import java.util.*;

/**Сервис для работы с клиентами*/
public class ClientService implements OldObjectsRemover{
   /**key-value хранилище клиентов, key - публичный ключ клиента, value - клиент*/
   Map<String,Client> clients;
   /**key-value хранилище токенов, key - секретный ключ клиента, value - токен*/
   Map<String,ClientToken> tokens;
   /**Время действия клиента*/
   long clientTimeout;

   public ClientService(long clientTimeout){
       clients=Collections.synchronizedMap(new HashMap<>());
       tokens=Collections.synchronizedMap(new HashMap<>());
       this.clientTimeout=clientTimeout;
   }

   /**
    *  Регистрация нового клиента
    * @return новый клиент*/
   public Client registerClient(){
       Client client =new Client();
       clients.put(client.publicKey, client);
       return client;
   }
   /**Проверяет наличие действующего клиента по его публичному ключу*/
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
   /**Привязывает идентификатор CAPTCHA к клиенту*/
   public void attachCaptchaToClient(String captchaId, String publicKey){
       if (checkClientExistence(publicKey)){
           Client client=clients.get(publicKey);
           client.attachCaptcha(captchaId);
       }
   }
    /**Проверяет, привязан ли заданный идентификатор CAPTCHA к заданному клиенту*/
   public boolean checkCaptchaAttachedToClient(String captchaId,String publicKey){
       if (checkClientExistence(publicKey)){
            Client client=clients.get(publicKey);
            if (captchaId.equals(client.getCaptchaId()))
                return true;
       }
       return false;
   }

   /**
    * Создает и привязывает новый Token для клиента (по указанному публичному ключу)
    * @return строковый идентификатор токена*/
   public String getTokenForClient(String publicKey){
       if (checkClientExistence(publicKey)){
            ClientToken token=new ClientToken(publicKey);
            tokens.put(clients.get(publicKey).getSecretKey(),token);
            return token.getTokenString();
       } else{
            return null;
       }

   }
   /**
    * <p>Проверяет привязан ли заданный токен (ищется по строковому идентификатору) к заданному клиенту
    * (ищется по секретному ключу).</p>
    * <p>После проверки, независимо от ее результата, токен отвязывается от клиента (если клиент с таким ключом есть)
    * для исключения возможности подбора токена</p>
    * @return код ошибки. Возможные варианты:
    * <ul>
    *     <li>null - ошибки нет, такой токен привязан к такому клиенту</li>
    *     <li>NoTokenForSuchKey - для такого секретного ключа токена не существует.
    *     Возможен при некорреткном указании секретного ключа или при повторном поиске (в случае,
    *     когда после первого поиска токен был отвязан)</li>
    *     <li>IncorrectToken - токен указан некорреткно</li>
    *     <li>ClientIsExpired - срок действия клиента истек</li>
    * </ul>*/
   public String verifyClientToken(String secretKey,String tokenString){
       ClientToken token= tokens.get(secretKey);
       String errorCode=null;
       if (token==null){
           errorCode="NoTokenForSuchKey";
       } else{
           if (!token.getTokenString().equals(tokenString))
               errorCode="IncorrectToken";
           Client client=clients.get(token.getClientPublicKey());
           if (isClientExpired(client)){
               clients.remove(client.getPublicKey());
               errorCode="ClientIsExpired";
           }
           tokens.remove(secretKey);
       }
       return errorCode;
   }
    /**Проверяет истек ли срок действия указанного по публичному ключу клиента*/
    boolean isClientExpired(Client client){
        if (client.getCreationTime().getTime()+clientTimeout> new Date().getTime())
            return false;
        else
            return true;
    }

    public void removeOldObjects(){
        Iterator<Map.Entry<String,Client>> iterator;
        for(iterator=clients.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String,Client> entry = iterator.next();
            if(entry.getValue().creationTime.getTime()+clientTimeout < new Date().getTime()) {
                String secretKey=entry.getValue().getSecretKey();
                tokens.remove(secretKey);
                iterator.remove();
            }
        }
    }
}
