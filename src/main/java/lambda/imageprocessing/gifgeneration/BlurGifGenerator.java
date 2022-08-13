package lambda.imageprocessing.gifgeneration;

import lambda.imageprocessing.ImageFragment;
import lambda.imageprocessing.ImageProcessingUtils;
import lambda.imageprocessing.gifwriter.GifWriter;
import lambda.imageprocessing.gifwriter.impl.GifWriterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlurGifGenerator implements GifGenerator {

    private ImageProcessingUtils imageProcessingUtils = new ImageProcessingUtils();
    private GifWriter gifWriter = new GifWriterImpl();
    private static final Logger logger = LoggerFactory.getLogger(BlurGifGenerator.class);


    @Override
    public ByteArrayOutputStream generateGif(BufferedImage srcImage) throws IOException {
        List<BufferedImage> imgs = generateFrames(srcImage);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ImageOutputStream output = new MemoryCacheImageOutputStream(bytes)) {
            gifWriter.writeGifToStream(imgs, 300, true, output);
        } catch(Exception e){
            logger.error("Error generating GIF", e);
        }
        return bytes;
    }

    private List<BufferedImage> generateFrames(BufferedImage srcImage) throws IOException {

        int width = srcImage.getWidth();
        int height = srcImage.getHeight();


        int minDimension = Math.min(width, height) / 2;
        List<BufferedImage> imgs = new ArrayList<>();
        imgs.add(srcImage);


        int minimumPixelSize = 4;
        while (minDimension >= minimumPixelSize && width >= minimumPixelSize && height >= minimumPixelSize) {
            BufferedImage bufferedImage = generateFrame(srcImage, minDimension);
            imgs.add(bufferedImage);
            minDimension = minDimension / 2;
        }

        Collections.reverse(imgs);

        return imgs;
    }


    private BufferedImage generateFrame(BufferedImage srcImage, Integer squareSize) throws IOException {
        List<ImageFragment> result = new ArrayList<>();
        Integer iterX = (int) Math.ceil(((double) srcImage.getWidth() / squareSize));
        Integer iterY = (int) Math.ceil(((double) srcImage.getHeight() / squareSize));
        for (int i = 0; i < Math.ceil(iterX); i++) {
            for (int j = 0; j < iterY; j++) {
                int topLeftSquareXCoordinate = i * squareSize;
                int topLeftSquareYCoordinate = j * squareSize;

                Integer squareWidth = squareSize;
                Integer squareHeight = squareSize;

                //adjust square width as to no go beyond original image right border
                if (topLeftSquareXCoordinate > srcImage.getWidth() - squareSize) {
                    squareWidth = srcImage.getWidth() - topLeftSquareXCoordinate;
                }

                //adjust square width as to no go beyond original image left border
                if (topLeftSquareYCoordinate > srcImage.getHeight() - squareSize) {
                    squareHeight = srcImage.getHeight() - topLeftSquareYCoordinate;
                }

                Color squareAverageColor = imageProcessingUtils.averageColor(srcImage, topLeftSquareXCoordinate, topLeftSquareYCoordinate, squareWidth, squareHeight);
                BufferedImage colorSquare = new BufferedImage(squareWidth, squareHeight, BufferedImage.TYPE_3BYTE_BGR);
                imageProcessingUtils.setColor(colorSquare, squareAverageColor);
                result.add(new ImageFragment(colorSquare, topLeftSquareXCoordinate, topLeftSquareYCoordinate));
            }
        }

        BufferedImage resultFrame = imageProcessingUtils.joinImageFragments(result, srcImage.getWidth(), srcImage.getHeight());
        return resultFrame;
    }


}
