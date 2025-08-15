package dogged.imager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageReader {
    public static BufferedImage readFromFile(String fileName) {
        BufferedImage image = null;
        try {
            File file = new File("C:\\Users\\isaac\\IntellIJ Projects\\Imager\\src\\main\\Images\\" + fileName);

            image = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        return image;
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, image.getType());

        Graphics2D g2d = resizedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, width, height, null);

        g2d.dispose();

        return resizedImage;
    }

    public static void writeToFile(BufferedImage image, String fileName) {
        try {
            File output = new File("Images\\" + fileName);

            ImageIO.write(image, fileName.substring(fileName.indexOf(".") + 1), output);

            System.out.println("Writing complete.");
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}
