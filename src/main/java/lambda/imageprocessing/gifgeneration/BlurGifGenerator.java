package lambda.imageprocessing.gifgeneration;

import lambda.imageprocessing.ImageFragment;
import lambda.imageprocessing.ImageProcessingUtils;
import lambda.imageprocessing.gifgeneration.FrameGeneratorHelper.ProcessFragmentParameters;
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
import java.util.function.Function;

public class BlurGifGenerator implements GifGenerator {

    private FrameGeneratorHelper frameGeneratorHelper = new FrameGeneratorHelper();
    private ImageProcessingUtils imageProcessingUtils = new ImageProcessingUtils();
    private GifWriter gifWriter = new GifWriterImpl();
    private static final Logger logger = LoggerFactory.getLogger(BlurGifGenerator.class);


    private ProcessFragment processFragment = input ->
    {
        Color squareAverageColor = imageProcessingUtils.averageColor(input.srcImage, input.topLeftSquareXCoordinate, input.topLeftSquareYCoordinate, input.currWidth, input.currHeight);
        BufferedImage colorSquare = new BufferedImage(input.currWidth, input.currHeight, BufferedImage.TYPE_3BYTE_BGR);
        imageProcessingUtils.setColor(colorSquare, squareAverageColor);
        return colorSquare;
    };


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

    @Override
    public GifWriter getGifWriter() {
        return gifWriter;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public List<BufferedImage> generateFrames(BufferedImage srcImage) throws IOException {

        int width = srcImage.getWidth();
        int height = srcImage.getHeight();

        List<BufferedImage> imgs = new ArrayList<>();
        imgs.add(srcImage);

        int minimumPixelSize = 4;
        while (width >= minimumPixelSize && height >= minimumPixelSize) {
            BufferedImage bufferedImage = frameGeneratorHelper.generateFrames(srcImage,width,height, processFragment);
            imgs.add(bufferedImage);
            width = width / 2;
            height = height / 2;
        }
        Collections.reverse(imgs);
        return imgs;
    }




}
