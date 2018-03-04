package iovi;

import java.util.UUID;

public class ClientData extends Object{
    String secretKey;
    String publicKey;
    public ClientData(){
        secretKey= UUID.randomUUID().toString();
        publicKey= UUID.randomUUID().toString();
    }
    public String getPublicKey(){return publicKey;}
    public String getSecretKey(){return secretKey;}
}
