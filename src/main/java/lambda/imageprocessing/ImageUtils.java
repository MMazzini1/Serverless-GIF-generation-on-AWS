package lambda.imageprocessing;

import lambda.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtils {


    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    private final String JPG_TYPE = (String) "jpg";
    private final String JPG_MIME = (String) "image/jpeg";
    private final String PNG_TYPE = (String) "png";
    private final String PNG_MIME = (String) "image/png";


    public String determineImageType(String srcKey){
        Matcher matcher = Pattern.compile(".*\\.([^\\.]*)").matcher(srcKey);
        if (!matcher.matches()) {
            logger.info("Unable to infer image type for key " + srcKey);
            return "";
        }
        String imageType = matcher.group(1);
        if (!(JPG_TYPE.equals(imageType)) && !(PNG_TYPE.equals(imageType))) {
            logger.info("Skipping non-image " + srcKey);
            return "";
        }
        return imageType;
    }


    public String getMimeType(String imageType){
        if (JPG_TYPE.equals(imageType)) {
           return JPG_MIME;
        }
        if (PNG_TYPE.equals(imageType)) {
            return PNG_MIME;
        }
        logger.info("Non image type, returning empty metadata " + imageType);
        return "";
    }

}
