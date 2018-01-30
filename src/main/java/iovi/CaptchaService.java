package iovi;


import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.synchronizedMap;

/**Сервис для работы с captcha */
public class CaptchaService{



    /**таймаут ожидания проверки captcha в мс*/
    long timeout;

    /**минимально допустимое значние таймаута {@link #timeout}*/
    final static long MIN_TIMEOUT=1000;
    final static long REMOVE_PERIOD=60000;
    Map<String,CaptchaWithCreationTime> captchas;

    /**
     * @param timeoutInMs значение в мс, присваиваемое {@link #timeout}.
     * Значение игнорируется, если оно меньше {@link #MIN_TIMEOUT}, в качестве таймаута устанавливается {@link #MIN_TIMEOUT}
     */
    public CaptchaService(long timeoutInMs){
        captchas = synchronizedMap(new HashMap());
        if (timeoutInMs<MIN_TIMEOUT)
            timeout=MIN_TIMEOUT;
        else
            timeout=timeoutInMs;

        RemovingThread remover=new RemovingThread(captchas,timeoutInMs,REMOVE_PERIOD);
        remover.start();
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
     *     <li>Выполнить успешную проверку можно только в течение {@link #timeout} с момента создания captcha</li>
     * </ul>
     * @param captchaId идентификатор ранее созданной captcha
     * @param captchaText текст captcha, нуждающийся в проверке
     * @return true если все условия проверки успешно выполнены, false в противном случае
     */
    public boolean checkCaptchaText(String captchaId, String captchaText){
        CaptchaWithCreationTime captchaData= captchas.get(captchaId);
        if (captchaData==null)
            return false;
        if (captchaData.getCaptcha().getText().equals(captchaText) &&
                captchaData.getCreationTime().getTime()+timeout> new Date().getTime()){
            captchas.remove(captchaId);
            return true;
        }
        else return false;
    }

}
