package lambda.imageprocessing.gifgeneration;

import lambda.imageprocessing.TestUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CyclicGifGeneratorTest {

    private GifGenerator recursiveGifGenerator = new CyclicGifGenerator();
    private TestUtils testUtils = new TestUtils();

    @Test
    public void testCreateGIF() throws IOException {
        BufferedImage bufferedImage = testUtils.loadImage("photo_test.png");
        List<BufferedImage> imageList = recursiveGifGenerator.generateFrames(bufferedImage);
        testUtils.writeGif(imageList, "cyclic");
    }


}
