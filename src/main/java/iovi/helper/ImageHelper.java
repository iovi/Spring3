package iovi.helper;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static java.lang.Math.sin;

/** Помощник при работе с изображениями*/
public class ImageHelper {

    /**
     * <p>Создает новое искаженное изображение BufferedImage из оригинального.</p>
     * <p>Пикселю нового изображения с координатами (xD,yD) устанавливается
     * цвет пикселя оригинального изображения с координатами (x0, y0). </p>
     * <p>Координаты вычисляются по формулам:</p>
     *  y0=a1*sin(w1*xD+t1)<br>
     *  x0=a2*sin(w2*yD+t2)<br>
     * <p>где a1,a2,w1,w2,t1,t2 - случайные коэффициенты:
     * a1 ∈ [5;7], a2 ∈ [5;7], w1 ∈ [0.05;0.15] w2 ∈ [0.05;0.15], t1 ∈ [0;3.14], t2 ∈ [0;3.14]</p>
     */
    public static BufferedImage createDistortedImage(BufferedImage originalImage){
        BufferedImage distortedImage=new BufferedImage(originalImage.getWidth(), originalImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Random random=new Random();
        double w1=random.nextDouble()*0.1+0.05;
        double w2=random.nextDouble()*0.1+0.05;
        double t1=random.nextDouble()*3.14;
        double t2=random.nextDouble()*3.14;
        double a1=random.nextDouble()*2+5;
        double a2=random.nextDouble()*2+5;

        for(int xD = 0; xD < originalImage.getWidth(); xD++) {

            for (int yD = 0; yD < originalImage.getHeight(); yD++) {
                int y0 = yD + (int) (a1 * sin(w1 * xD + t1));
                int x0 = xD + (int) (a2 * sin(w2 * yD + t2));

                if (x0 < 0 || y0 < 0 || x0 >= originalImage.getWidth() - 1 || y0 >= originalImage.getHeight() - 1) {
                    distortedImage.setRGB(xD, yD, 0);
                } else {
                    distortedImage.setRGB(xD, yD, originalImage.getRGB(x0, y0));
                }
            }
        }
        return distortedImage;
    }

    /**
     * Создает изображение, в котором выведен указанный текст в указанном шрифте.
     * @param text текст, который будет выведен в изображении
     * @param font шрифт вывода. Рекомендуется указывать Arial, кегль 48
     * @return изображение BufferedImage с прозрачным фоном, черным цветом текста.
     */
    public static BufferedImage createImageFromString(String text, Font font){
        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D textGraphics = bufferedImage.createGraphics();

        textGraphics.setFont(font);
        FontMetrics fm = textGraphics.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        textGraphics.dispose();

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        textGraphics = bufferedImage.createGraphics();
        textGraphics.setColor(Color.BLACK);
        textGraphics.setFont(font);
        textGraphics.drawString(text, 0, fm.getAscent());
        textGraphics.dispose();
        return bufferedImage;
    }
}
