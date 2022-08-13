package lambda.imageprocessing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageProcessingUtils {


    /** Resize image to widht and height (if smaller than original) without stretching */
    public static BufferedImage resize(BufferedImage img, int originalWidth, int originalHeight){
        //dont enlarge image if it´s too small
        if (originalWidth > img.getWidth() && originalHeight > img.getHeight()){
            return img;
        }

        float srcWidth = img.getWidth();
        float srcHeight = img.getHeight();
        float scalingFactor = Math.min(originalWidth / srcWidth, originalHeight
                / srcHeight);
        int newWidth = (int) (scalingFactor * srcWidth);
        int newHeight = (int) (scalingFactor * srcHeight);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setPaint(Color.white);
        g.fillRect(0, 0, newWidth, newHeight);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resizedImage;
    }



    /** Calculates average color of a portion of an image .
     * x0, y0, coords of left top corner
     * w,h, width and height from the left top corner
     * */
    public static Color averageColor(BufferedImage bi, int x0, int y0, int w,
                                     int h) {
        int x1 = x0 + w;
        int y1 = y0 + h;
        long sumr = 0, sumg = 0, sumb = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                Color pixel = new Color(bi.getRGB(x, y));
                sumr += pixel.getRed();
                sumg += pixel.getGreen();
                sumb += pixel.getBlue();
            }
        }
        int num = w * h;
        int r = (int) (sumr / num);
        int g = (int) (sumg / num);
        int b = (int) (sumb / num);

        return new Color(r, g, b);
    }


    /**  sets color to image */
    private void setColor(BufferedImage bufferedImage, Color color) {
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                bufferedImage.setRGB(i, j, color.getRGB());
            }
        }
    }


    /** Joins multiple fragments of an image in one single image */
    public BufferedImage joinImagePortions(List<ImagePortion> imagesToJoin, Integer width, Integer height) {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2 = newImage.createGraphics();
        g2.fillRect(0, 0, width, height);
        for (ImagePortion img : imagesToJoin) {
            g2.drawImage(img.imageSegment, null, img.upperLeftCornerXCoordinate, img.upperLeftCornerYCoordinate);
        }
        g2.dispose();
        return newImage;
    }



}
