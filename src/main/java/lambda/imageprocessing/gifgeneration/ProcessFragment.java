package lambda.imageprocessing.gifgeneration;

import lambda.imageprocessing.gifgeneration.FrameGeneratorHelper.ProcessFragmentParameters;

import java.awt.image.BufferedImage;
import java.util.function.Function;

public interface ProcessFragment extends Function<ProcessFragmentParameters, BufferedImage> {
}
