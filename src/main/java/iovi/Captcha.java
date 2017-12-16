package iovi;

import java.util.Random;

public class Captcha {
    int id;
    String text;
    Picture picture;

    public Captcha(int id, int textLength){
        this.id=id;
        text=GetRandomWord(textLength);
        //picture=

    }
    private static String GetRandomWord(int textLength){
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
