package lambda.imageprocessing.gifgeneration;

import lambda.imageprocessing.TestUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

class BlurGifGeneratorTest {

    private GifGenerator recursiveGifGenerator = new BlurGifGenerator();
    private TestUtils testUtils = new TestUtils();

    @Test
    public void testCreateGIF() throws IOException {
        BufferedImage bufferedImage = testUtils.loadImage("photo_test.png");
        List<BufferedImage> imageList = recursiveGifGenerator.generateFrames(bufferedImage);
        testUtils.writeGif(imageList, "blur");
    }

}
