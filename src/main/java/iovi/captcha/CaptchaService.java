package iovi.captcha;


import iovi.helper.PropertiesHelper;

import java.awt.image.BufferedImage;
import java.util.*;

import static java.util.Collections.synchronizedMap;

/**Сервис для работы с captcha */
public class CaptchaService{

    /**функция нахождения таймаута ожидания проверки captcha в мс*/
    private long getTimeout(){
        long timeout= PropertiesHelper.captchaTimeout()*1000;
        if (timeout==0)
            timeout=MIN_TIMEOUT;
        return timeout;
    }

    /**минимально допустимое значние таймаута в мс*/
    final static long MIN_TIMEOUT=60000;
    Map<String,Captcha> captchas;

    public CaptchaService(){
        captchas = synchronizedMap(new HashMap());
    }

    /**
     * Создает новую captcha, сохраняет ее внутри CaptchaService, возвращает ее id
     * @return id новой captcha в формате UUID
     */
    public String getNewCaptchaId(){
        Captcha captcha=new Captcha();
        String uid= UUID.randomUUID().toString();
        captchas.put(uid,captcha);
        return uid;
    }

    /**
     * @param captchaId идентификатор ранее созданной captcha
     * @return текст captcha
     */
    public String getCaptchaText(String captchaId){
        return captchas.containsKey(captchaId)? captchas.get(captchaId).getText():null;
    }

    /**
     * @param captchaId идентификатор ранее созданной captcha
     * @return картинка captcha
     */
    public BufferedImage getCaptchaImage(String captchaId){
        return captchas.containsKey(captchaId)? captchas.get(captchaId).getImage():null;
    }

    /**
     * <p>Проверяет корректность текста для captcha с заданным идентификартором в соответствии со следующими условиями </p>
     * <ul>
     *     <li>Введенный текст должен совпадать с текстом объекта captcha (с указанным id)</li>
     *     <li>Для одной captcha можно пройти проверку только 1 раз. Для исключения возможности подбора текста ответа,
     *     последующие попытки проверки возвращают false как при успешной, так и при неуспешной первой проверке </li>
     *     <li>Выполнить успешную проверку можно только в течение таймаута с момента создания captcha</li>
     * </ul>
     * @param captchaId идентификатор ранее созданной captcha
     * @param captchaText текст captcha, нуждающийся в проверке
     * @return true если все условия проверки успешно выполнены, false в противном случае
     */
    public boolean checkCaptchaText(String captchaId, String captchaText){
        Captcha captcha= captchas.get(captchaId);
        if (captcha==null)
            return false;
        else {
            if (captcha.getText().equals(captchaText) &&
                    captcha.getCreationTime().getTime() + getTimeout() > new Date().getTime()) {
                captchas.remove(captchaId);
                return true;
            } else{
                captchas.remove(captchaId);
                return false;
            }
        }
    }
}
