package lambda.imageprocessing.gifgeneration;

import lambda.imageprocessing.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

class RecursiveGifGeneratorTest {

    private RecursiveGifGenerator recursiveGifGenerator = new RecursiveGifGenerator();
    private TestUtils testUtils = new TestUtils();

    @Test
    public void testCreateGIF() throws IOException {
        BufferedImage bufferedImage = testUtils.loadImage("photo_test.png");
        List<BufferedImage> imageList = recursiveGifGenerator.generateFrames(bufferedImage);
        Assertions.assertDoesNotThrow(() ->       testUtils.writeGif(imageList, "recursive"));
    }

}
