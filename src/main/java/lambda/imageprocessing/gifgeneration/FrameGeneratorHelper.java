package lambda.imageprocessing.gifgeneration;

import lambda.imageprocessing.ImageFragment;
import lambda.imageprocessing.ImageProcessingUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FrameGeneratorHelper {

    private ImageProcessingUtils imageProcessingUtils = new ImageProcessingUtils();

    public static class ProcessFragmentParameters{
        protected BufferedImage srcImage;
        protected int topLeftSquareXCoordinate;
        protected int topLeftSquareYCoordinate;
        protected int currWidth;
        protected int currHeight;

        public ProcessFragmentParameters(BufferedImage srcImage, int topLeftSquareXCoordinate, int topLeftSquareYCoordinate, int currWidth, int currHeight) {
            this.srcImage = srcImage;
            this.topLeftSquareXCoordinate = topLeftSquareXCoordinate;
            this.topLeftSquareYCoordinate = topLeftSquareYCoordinate;
            this.currWidth = currWidth;
            this.currHeight = currHeight;
        }
    }

    public BufferedImage generateFrames(BufferedImage srcImage, int currWidth, int currHeight, ProcessFragment processFragment) {
        List<ImageFragment> result = new ArrayList<>();
        for (int i = 0; i <= srcImage.getWidth() / currWidth; i++) {
            for (int j = 0; j <= srcImage.getHeight() / currHeight; j++) {
                int topLeftSquareXCoordinate = i * currWidth;
                int topLeftSquareYCoordinate = j * currHeight;
                ProcessFragmentParameters input = new ProcessFragmentParameters(srcImage, topLeftSquareXCoordinate, topLeftSquareYCoordinate, currWidth, currHeight);
                BufferedImage fragment = processFragment.apply(input);
                result.add(new ImageFragment(fragment, topLeftSquareXCoordinate, topLeftSquareYCoordinate));
            }
        }
        BufferedImage resultFrame = imageProcessingUtils.joinImageFragments(result, srcImage.getWidth(), srcImage.getHeight());
        return resultFrame;
    }


}
