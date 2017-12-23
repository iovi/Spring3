package iovi;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Random;

import static java.lang.Math.sin;

public class Captcha {
    int id;
    String text;
    BufferedImage image;

    public Captcha(int id, int textLength){
        this.id=id;
        text=GetRandomWord(textLength);
        image=CreateCaptchaImageFromString(text);
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

    BufferedImage CreateCaptchaImageFromString(String string){
        Font font = new Font("Arial", Font.PLAIN, 48);
        BufferedImage textImage=CreateImageFromString(string,font);
        BufferedImage captchaImage=CreateDistortedImage(textImage);

        Graphics2D captchaGrapphics = captchaImage.createGraphics();
        captchaGrapphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        captchaGrapphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        captchaGrapphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        captchaGrapphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        captchaGrapphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        captchaGrapphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        captchaGrapphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        captchaGrapphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        captchaGrapphics.dispose();
        return captchaImage;
    }

    BufferedImage CreateDistortedImage(BufferedImage originalImage){
        BufferedImage distortedImage=new BufferedImage(originalImage.getWidth(), originalImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Random random=new Random();
        double w1=random.nextDouble()*0.1+0.05;
        double w2=random.nextDouble()*0.1+0.05;
        double t1=random.nextDouble()*3.14;
        double t2=random.nextDouble()*3.14;
        double a1=random.nextDouble()*2+5;
        double a2=random.nextDouble()*2+5;

        for(int distortedX = 0; distortedX < originalImage.getWidth(); distortedX++) {

            for (int distortedY = 0; distortedY < originalImage.getHeight(); distortedY++) {
                int originalY = distortedY + (int) (a1 * sin(w1 * distortedX + t1));
                int originalX = distortedX + (int) (a2 * sin(w2 * distortedY + t2));

                if (originalX < 0 || originalY < 0 || originalX >= originalImage.getWidth() - 1 || originalY >= originalImage.getHeight() - 1) {
                    distortedImage.setRGB(distortedX, distortedY, 1677725);
                } else {
                    distortedImage.setRGB(distortedX, distortedY, originalImage.getRGB(originalX, originalY));
                }
            }
        }
        return distortedImage;
    }

    BufferedImage CreateImageFromString(String string, Font font){
        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D textGrapphics = bufferedImage.createGraphics();

        textGrapphics.setFont(font);
        FontMetrics fm = textGrapphics.getFontMetrics();
        int width = fm.stringWidth(string);
        int height = fm.getHeight();
        textGrapphics.dispose();

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        textGrapphics = bufferedImage.createGraphics();
        textGrapphics.setColor(Color.BLACK);
        textGrapphics.setFont(font);
        textGrapphics.drawString(string, 0, fm.getAscent());
        textGrapphics.dispose();
        return bufferedImage;
    }

}
