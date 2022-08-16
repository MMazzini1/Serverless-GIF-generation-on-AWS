package lambda.imageprocessing.gifgeneration;

import lambda.imageprocessing.gifwriter.GifWriter;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface GifGenerator {





    List<BufferedImage> generateFrames(BufferedImage srcImage) throws IOException;


    default ByteArrayOutputStream generateGif(BufferedImage srcImage) throws IOException {
        java.util.List<BufferedImage> imgs = generateFrames(srcImage);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //fixed duration of 3 seconds, independent of frame quantity
        Integer delayBetweenFrames = 3000 / imgs.size();
        try (ImageOutputStream output = new MemoryCacheImageOutputStream(bytes)) {
            getGifWriter().writeGifToStream(imgs, delayBetweenFrames, true, output);
        } catch (Exception e) {
            getLogger().error("Error generating GIF",e );
        }
        return bytes;
    }

    public GifWriter getGifWriter();
    public Logger getLogger();

}
