package iovi.captcha;

import iovi.helper.ImageHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**Класс предоставляет средства создания и работы с captcha - картинки с соответствующим ей текстом */
public class Captcha {
    /**Текст captcha*/
    private String text;
    /**Минимально допустимая длина текста captcha*/
    final static int MIN_LENGTH=6;
    /**Флаг, говорящий показывалась ли картинка CAPTCHA*/
    private boolean isShown;
    /**Время создания CAPTCHA - с момента вызова конструктора*/
    Date creationTime;



    /**Создает новую captсha с текстом заданной длины
     * @param textLength длина текста, минимальное значние {@link #MIN_LENGTH}.
     * При передаче меньшего значния это переданное значение игнорируется и генерируется текст из {@link #MIN_LENGTH} символов*/
    public Captcha(int textLength){
        if (textLength<MIN_LENGTH)
            text=getRandomWord(MIN_LENGTH);
        else
            text=getRandomWord(textLength);
        isShown=false;
        creationTime= Calendar.getInstance().getTime();
    }

    /**Создает новую captсha с текстом длины {@link #MIN_LENGTH}*/
    public Captcha(){
        text=getRandomWord(MIN_LENGTH);
        isShown=false;
        creationTime= Calendar.getInstance().getTime();
    }

    public String getText(){
        return text;
    }
    public Date getCreationTime(){
        return creationTime;
    }

    /**
     * Возвращает CAPTCHA-картинку в виде BufferedImage.
     * Для одного объекта класса картинка показывается только один раз, при последующих вызовах вернется null.
     */
    public synchronized BufferedImage getImage(){
        if (!isShown){
            isShown=true;
            Font font = new Font("Arial", Font.PLAIN, 48);
            BufferedImage textImage= ImageHelper.createImageFromString(text,font);
            return ImageHelper.createDistortedImage(textImage);
        } else
            return null;

    }

    /**Создает случайный текст заданной длины из больших и маленьких латинских символов и цифр.*/
    private static String getRandomWord(int textLength){
        if (textLength<=0) return null;

        char[] word=new char[textLength];
        Random random=new Random();
        for (int i=0;i<textLength;i++) {
            switch (random.nextInt(3)) {
                case 0:
                    word[i] = (char) ('a' + random.nextInt(26));
                    break;
                case 1:
                    word[i] = (char) ('A' + random.nextInt(26));
                    break;
                case 2:
                    word[i] = (char) ('0' + random.nextInt(10));
                    break;
            }
        }
        return new String(word);
    }

}
