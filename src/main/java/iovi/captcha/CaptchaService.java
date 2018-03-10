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
    Map<String,CaptchaWithCreationTime> captchas;

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

        captchas.put(uid,new CaptchaWithCreationTime(captcha));
        return uid;
    }

    /**
     * @param captchaId идентификатор ранее созданной captcha
     * @return текст captcha
     */
    public String getCaptchaText(String captchaId){
        return captchas.containsKey(captchaId)? captchas.get(captchaId).getCaptcha().getText():null;
    }

    /**
     * @param captchaId идентификатор ранее созданной captcha
     * @return картинка captcha
     */
    public BufferedImage getCaptchaImage(String captchaId){
        return captchas.containsKey(captchaId)? captchas.get(captchaId).getCaptcha().getImage():null;
    }

    /**
     * <p>Проверяет корректность текста для captcha с заданным идентификартором в соответствии со следующими условиями </p>
     * <ul>
     *     <li>Введенный текст должен совпадать с текстом объекта captcha (с указанным id)</li>
     *     <li>Для одной captcha можно пройти успешную проверку только 1 раз (последующие попытки
     *     проверки возвращают false)</li>
     *     <li>Выполнить успешную проверку можно только в течение таймаута с момента создания captcha</li>
     * </ul>
     * @param captchaId идентификатор ранее созданной captcha
     * @param captchaText текст captcha, нуждающийся в проверке
     * @return true если все условия проверки успешно выполнены, false в противном случае
     */
    public boolean checkCaptchaText(String captchaId, String captchaText){
        CaptchaWithCreationTime captchaData= captchas.get(captchaId);
        if (captchaData==null)
            return false;
        else {
            if (captchaData.getCaptcha().getText().equals(captchaText) &&
                    captchaData.getCreationTime().getTime() + getTimeout() > new Date().getTime()) {
                captchas.remove(captchaId);
                return true;
            } else{
                captchas.remove(captchaId);
                return false;
            }
        }
    }
}
