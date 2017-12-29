import iovi.Main;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionalTest {

    @Before
    public void startMain(){
        String[] args = new String[]{};
        Main.main(args);
    }
    @Test
    public void oneSenderWorks(){
        CaptchaRequestSender sender=new CaptchaRequestSender();
        assertEquals(200,sender.getCaptchaAndStoreData("http://localhost:8080/captcha"));
        assertEquals(200,sender.checkCaptchaByStoredData("http://localhost:8080/captcha/check"));
    }
    @Test
    public void manySendersWork(){
        Runnable senderActivity =() ->{
            CaptchaRequestSender sender=new CaptchaRequestSender();
            sender.getCaptchaAndStoreData("http://localhost:8080/captcha");
            //sender.checkCaptchaByStoredData("http://localhost:8080/captcha/check");
        };

        for (int i=0;i<20;i++){
            new Thread(senderActivity).start();

        }

    }

}
