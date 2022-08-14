package lambda.imageprocessing.gifgeneration;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface GifGenerator {
    ByteArrayOutputStream generateGif(BufferedImage srcImage) throws IOException;

    List<BufferedImage> generateFrames(BufferedImage srcImage) throws IOException;
}
