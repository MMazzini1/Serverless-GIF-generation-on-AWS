package lambda.imageprocessing.gifgeneration;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface GifGenerator {
    ByteArrayOutputStream generateGif(BufferedImage srcImage) throws IOException;
}
