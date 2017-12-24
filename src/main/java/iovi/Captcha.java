package iovi;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Random;

import static java.lang.Math.sin;

public class Captcha {
    private String text;
    private BufferedImage image;


    public Captcha(int textLength){
        text=getRandomWord(textLength);

        Font font = new Font("Arial", Font.PLAIN, 48);
        BufferedImage textImage=ImageHelper.createImageFromString(text,font);
        image=ImageHelper.createDistortedImage(textImage);
    }

    public String getText(){
        return text;
    }
    public BufferedImage getImage(){
        return image;
    }
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
