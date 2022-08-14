package lambda.imageprocessing.gifgeneration;

import java.awt.image.BufferedImage;
import java.util.function.Function;

public interface ProcessFragment extends Function<FrameGeneratorHelper.ProcessFragmentParameters, BufferedImage> {
}
