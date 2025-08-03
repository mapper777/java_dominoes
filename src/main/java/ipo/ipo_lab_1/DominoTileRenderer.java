package ipo.ipo_lab_1;

import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;

public class DominoTileRenderer {
    public WritableImage drawTile(int unicode) {
        String string = new String(Character.toChars(unicode));
        BufferedImage image;

        Font font = new Font(Font.DIALOG, Font.PLAIN, 350);

        FontRenderContext frc = new FontRenderContext(null, true, false);
        Rectangle2D rec = font.getStringBounds(string, frc);

        int width =  (int) Math.ceil(rec.getWidth());
        int height = (int) Math.ceil(rec.getHeight());

        // создаем изображение с учётом отступов
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // настройки для рендера текста
        g2.setFont(font);
        g2.setColor(java.awt.Color.black);
        g2.setBackground(java.awt.Color.white);

        g2.drawString(string, 0,  g2.getFontMetrics().getAscent());
        g2.dispose();

        //return SwingFXUtils.toFXImage(bufferedImage, null);
        return getImage(image);
    }

    private WritableImage getImage(BufferedImage img) {
        // converting to a good type as said in stackoverflow answer
        // https://stackoverflow.com/questions/30970005/bufferedimage-to-javafx-image
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        newImg.createGraphics().drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);

        // converting the BufferedImage to an IntBuffer
        int[] type_int_argb = ((DataBufferInt) newImg.getRaster().getDataBuffer()).getData();
        IntBuffer buffer = IntBuffer.wrap(type_int_argb);

        // converting the intBuffer to an Image
        PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
        PixelBuffer<IntBuffer> pixelBuffer = new PixelBuffer<>(newImg.getWidth(),
                newImg.getHeight(), buffer, pixelFormat);

        return new WritableImage(pixelBuffer);
    }
}
