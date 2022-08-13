package lambda.imageprocessing;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePortion {
    BufferedImage imageSegment;
    int upperLeftCornerXCoordinate;
    int upperLeftCornerYCoordinate;
    Color colorToApply;

    public ImagePortion(BufferedImage imageSegment, int xCoordinate, int yCoordinate) {
        this.imageSegment = imageSegment;
        this.upperLeftCornerXCoordinate = xCoordinate;
        this.upperLeftCornerYCoordinate = yCoordinate;
    }


    public ImagePortion(BufferedImage imageSegment, int upperLeftCornerXCoordinate, int upperLeftCornerYCoordinate, Color colorToApply) {
        this.imageSegment = imageSegment;
        this.upperLeftCornerXCoordinate = upperLeftCornerXCoordinate;
        this.upperLeftCornerYCoordinate = upperLeftCornerYCoordinate;
        this.colorToApply = colorToApply;
    }
}
