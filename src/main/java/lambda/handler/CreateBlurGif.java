package lambda.handler;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;


import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import lambda.imageprocessing.gifgeneration.BlurGifGenerator;
import lambda.imageprocessing.gifgeneration.GifGenerator;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lambda.imageprocessing.ImageTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


public class CreateBlurGif implements RequestHandler<S3Event, String> {
    public static final String PROCESSED_IMAGES_BUCKET = "image-processing-app-destination";


    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LoggerFactory.getLogger(CreateBlurGif.class);
    private ImageTypeUtils imageUtils;
    private S3Client s3Client;
    private GifGenerator blurGifGenerator;


    public CreateBlurGif() {
        imageUtils = new ImageTypeUtils();
        Region region = Region.US_EAST_1;
        s3Client = S3Client.builder()
                .region(region)
                .build();
        blurGifGenerator = new BlurGifGenerator();

    }


    @Override
    public String handleRequest(S3Event s3event, Context context) {

        logger.info("Creating Blur Gif");
        logger.info("Event: " + gson.toJson(s3event));


        S3EventNotificationRecord record = s3event.getRecords().get(0);
        String srcBucket = record.getS3().getBucket().getName();
        String srcKey = record.getS3().getObject().getUrlDecodedKey();
        String imageType = imageUtils.determineImageType(srcKey);

        String mimeType = imageUtils.getMimeType(imageType);
        if (imageType.equals("")) {
            return "";
        }

        logger.info("Src bucket: " + srcBucket +
                " key:" + srcKey +
                " Img type: " + imageType +
                " Mime type: " + mimeType);


        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(srcBucket)
                .key(srcKey)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);


        try {

            BufferedImage srcImage = ImageIO.read(response);
            try {
                String dstBucket = PROCESSED_IMAGES_BUCKET;
                String dstKey = "resized-" + srcKey;



                ByteArrayOutputStream gif = blurGifGenerator.generateGif(srcImage);


                PutObjectRequest objectRequest = PutObjectRequest.builder()
                        .bucket(dstBucket)
                        .contentType("image/gif")
                        .key(dstKey)
                        .build();

                s3Client.putObject(objectRequest,RequestBody.fromBytes(gif.toByteArray()));


            } catch (AmazonServiceException e) {
                logger.info("Write failed");
                logger.error(e.getErrorMessage());
                System.exit(1);
            }
        } catch (IOException e) {
            logger.info("Error reading image");
            e.printStackTrace();
        }


        return "OK";


    }
}
