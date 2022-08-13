package lambda.handler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;


import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lambda.imageprocessing.ImageProcessing;
import lambda.imageprocessing.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


public class Handler implements RequestHandler<S3Event, String> {
    public static final String PROCESSED_IMAGES_BUCKET = "image-processing-app-destination";


    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    private static final float MAX_WIDTH = 200;
    private static final float MAX_HEIGHT = 200;
    private ImageUtils imageUtils;
    private S3Client s3Client;


    public Handler() {
        imageUtils = new ImageUtils();
        Region region = Region.US_EAST_1;
        s3Client = S3Client.builder()
                .region(region)
                .build();

    }


    @Override
    public String handleRequest(S3Event s3event, Context context) {
        //TODO move out initialization of Handler


        ImageProcessing imageProcessing = new ImageProcessing();

        logger.info("Starting request handler");
        logger.info("Event: " + gson.toJson(s3event));


        S3EventNotificationRecord record = s3event.getRecords().get(0);
        String srcBucket = record.getS3().getBucket().getName();
        // Object key may have spaces or unicode non-ASCII characters.
        String srcKey = record.getS3().getObject().getUrlDecodedKey();
        String imageType = imageUtils.determineImageType(srcKey);

        String mimeType = imageUtils.getMimeType(imageType);
        if (imageType.equals("")) {
            return "";
        }

        logger.info("Src bucket: " + srcBucket + " key:" + srcKey + " Img type: " + imageType + " mime type: " + mimeType);



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


                int srcHeight = srcImage.getHeight();
                int srcWidth = srcImage.getWidth();
                // Infer the scaling factor to avoid stretching the image
                // unnaturally
                float scalingFactor = Math.min(MAX_WIDTH / srcWidth, MAX_HEIGHT
                        / srcHeight);
                int width = (int) (scalingFactor * srcWidth);
                int height = (int) (scalingFactor * srcHeight);

                BufferedImage resizedImage = new BufferedImage(width, height,
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D g = resizedImage.createGraphics();
                // Fill with white before applying semi-transparent (alpha) images
                g.setPaint(Color.white);
                g.fillRect(0, 0, width, height);
                // Simple bilinear resize
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(srcImage, 0, 0, width, height, null);
                g.dispose();

                // Re-encode image to target format
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, imageType, os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());
                // Set Content-Length and Content-Type

                PutObjectRequest objectRequest = PutObjectRequest.builder()
                        .bucket(dstBucket)
                        .contentType(mimeType)
                        .key(dstKey)
                        .build();

                s3Client.putObject(objectRequest, RequestBody.fromInputStream(is, os.size()));


            } catch (AmazonServiceException e) {
                logger.info("Write failed");
                logger.error(e.getErrorMessage());
                System.exit(1);
            }
        } catch (IOException e) {
            logger.info("Error reading image");
            e.printStackTrace();
        }


        return "Okss";

        /*
        try {
            logger.info("hii");
            logger.info("EVENT: " + gson.toJson(s3event));



            S3EventNotificationRecord record = s3event.getRecords().get(0);
            String srcBucket = record.getS3().getBucket().getName();

            // Object key may have spaces or unicode non-ASCII characters.
            String srcKey = record.getS3().getObject().getUrlDecodedKey();


            String dstBucket = "image-processing-app-destination";
            String dstKey = "resized-" + srcKey;


            // Infer the image type.
         *//*   Matcher matcher = Pattern.compile(".*\\.([^\\.]*)").matcher(srcKey);
            if (!matcher.matches()) {
                logger.info("Unable to infer image type for key " + srcKey);
                return "";
            }
            String imageType = matcher.group(1);
            if (!(JPG_TYPE.equals(imageType)) && !(PNG_TYPE.equals(imageType))) {
                logger.info("Skipping non-image " + srcKey);
                return "";
            }*//*

            // Download the image from S3 into a stream
            AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(
                    srcBucket, srcKey));
            InputStream objectData = s3Object.getObjectContent();

            // Read the source image
          *//*  BufferedImage srcImage = ImageIO.read(objectData);
            int srcHeight = srcImage.getHeight();
            int srcWidth = srcImage.getWidth();
            // Infer the scaling factor to avoid stretching the image
            // unnaturally
            float scalingFactor = Math.min(MAX_WIDTH / srcWidth, MAX_HEIGHT
                    / srcHeight);
            int width = (int) (scalingFactor * srcWidth);
            int height = (int) (scalingFactor * srcHeight);

            BufferedImage resizedImage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            // Fill with white before applying semi-transparent (alpha) images
            g.setPaint(Color.white);
            g.fillRect(0, 0, width, height);
            // Simple bilinear resize
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImage, 0, 0, width, height, null);
            g.dispose();

            // Re-encode image to target format
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, imageType, os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            // Set Content-Length and Content-Type
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(os.size());
            if (JPG_TYPE.equals(imageType)) {
                meta.setContentType(JPG_MIME);
            }
            if (PNG_TYPE.equals(imageType)) {
                meta.setContentType(PNG_MIME);
            }
*//*
            // Uploading to S3 destination bucket
            logger.info("Writing to: " + dstBucket + "/" + dstKey);
            try {
                s3Client.putObject(dstBucket, dstKey, objectData, new ObjectMetadata());
            }
            catch(AmazonServiceException e)
            {
                logger.info("Write failed");
                logger.error(e.getErrorMessage());
                System.exit(1);
            }
            logger.info("Successfully resized " + srcBucket + "/"
                    + srcKey + " and uploaded to " + dstBucket + "/" + dstKey);
            return "Ok";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/


    }
}
