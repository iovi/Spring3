import iovi.Captcha;
import org.junit.Test;
import static org.junit.Assert.*;

/**Тест класса Captcha */
public class CaptchaTest {


    /**Тест создания ненулевой Captcha*/
    @Test
    public void NotNullCaptcha(){

        Captcha captcha=new Captcha();
        assertFalse(captcha==null);
        assertFalse(captcha.getText()==null);
        assertFalse(captcha.getImage()==null);
    }

    /**<p>Тест случайности текстов captcha.</p>
     * <p>Возможно маловероятное ошибочное непрохождение теста. Для стандартной длины текста 6 сиволов и количества
     * используемых символов 62, вероятность должна составить около 1/62^6 ~= 1.7*10^(-11)</p>*/
    @Test
    public void RandomText(){
        Captcha captcha1=new Captcha();
        Captcha captcha2=new Captcha();
        assertNotEquals(captcha1.getText(),captcha2.getText());
    }
}
