package lambda.imageprocessing;

import lambda.imageprocessing.gifwriter.impl.GifWriterImpl;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TestUtils {


   public BufferedImage loadImage(String fileName) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedImage originalImg = ImageIO.read(is);
        return originalImg;
    }



   public void writeGif(List<BufferedImage> frames, String fileName) throws IOException {
        ImageOutputStream imageOutputStream = new FileImageOutputStream(new File("src/test/java/lambda/imageprocessing/testresults/gifgeneration" + fileName + ".gif"));
        GifWriterImpl gifWriter = new GifWriterImpl();
        gifWriter.writeGifToStream(frames, 300, true, imageOutputStream);

    }

}
