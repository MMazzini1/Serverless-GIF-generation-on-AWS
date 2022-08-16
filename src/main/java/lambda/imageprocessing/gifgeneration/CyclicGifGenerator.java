package lambda.imageprocessing.gifgeneration;

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
import java.util.List;

public class CyclicGifGenerator implements GifGenerator {

    private FrameGeneratorHelper frameGeneratorHelper = new FrameGeneratorHelper();
    private ImageProcessingUtils imageProcessingUtils = new ImageProcessingUtils();
    private GifWriter gifWriter = new GifWriterImpl();
    private static final Logger logger = LoggerFactory.getLogger(CyclicGifGenerator.class);


    @Override
    public ByteArrayOutputStream generateGif(BufferedImage srcImage) throws IOException {
        java.util.List<BufferedImage> imgs = generateFrames(srcImage);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Integer delayBetweenFrames = 3000 / imgs.size();
        try (ImageOutputStream output = new MemoryCacheImageOutputStream(bytes)) {
            gifWriter.writeGifToStream(imgs, delayBetweenFrames, true, output);
        } catch (Exception e) {
            logger.error("Error generating GIF", e);
        }
        return bytes;
    }


    public java.util.List<BufferedImage> generateFrames(BufferedImage srcImage) throws IOException {

        int width = srcImage.getWidth();
        int height = srcImage.getHeight();

        List<BufferedImage> imgs = new ArrayList<>();
        imgs.add(srcImage);

        int minimumPixelSize = 4;
        int iteration = 0;
        while (width >= minimumPixelSize && height >= minimumPixelSize) {
            BufferedImage resized = imageProcessingUtils.resize(srcImage, width, height);


            ProcessFragment processFragment = input -> {
                //for gradually increasing opacity of fragment in each iteration
                int alpha = (int) ((float) input.iteration / 10f * 255f);
                Color color = imageProcessingUtils.averageColor(srcImage, input.topLeftSquareXCoordinate, input.topLeftSquareYCoordinate, input.currWidth, input.currHeight);
                BufferedImage colorizedFragment = imageProcessingUtils.cloneAndColorize(resized, color, alpha);
                return colorizedFragment;
            };


            BufferedImage bufferedImage = frameGeneratorHelper.generateFrames(srcImage, resized.getWidth(), resized.getHeight(), processFragment, iteration);
            imgs.add(bufferedImage);
            width = width / 2;
            height = height / 2;
            iteration++;
        }
        return imgs;
    }



    @Override
    public GifWriter getGifWriter() {
        return gifWriter;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

}
