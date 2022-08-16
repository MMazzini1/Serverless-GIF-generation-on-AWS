package lambda.handler;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lambda.imageprocessing.ImageTypeUtils;
import lambda.imageprocessing.gifgeneration.GifGenerator;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.amazonaws.services.s3.event.S3EventNotification.parseJson;

public class GIFGenerationLambdaHandler implements GifGenerationLambda{


    public static final String GIF_MIME_TYPE = "image/gif";
    private final String destinationBucket;
    private final String gifTypePrefix;

    private Gson gson;
    private ImageTypeUtils imageUtils;
    private S3Client s3Client;
    private GifGenerator gifGenerator;
    private final Region region;


    public GIFGenerationLambdaHandler(GifGenerator blurGifGenerator, String destinationBucket, String gifTypePrefix) {
        this.gifGenerator = blurGifGenerator;
        this.destinationBucket = destinationBucket;
        this.gifTypePrefix = gifTypePrefix;
        this.imageUtils = new ImageTypeUtils();
        this.region = Region.US_EAST_1;
        this.s3Client = S3Client.builder().region(this.region).build();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }


    @Override
    public String handleRequest(SNSEvent snsEvent, Context context) {

        List<SNSEvent.SNSRecord> snsRecordList = snsEvent.getRecords();
        SNSEvent.SNSRecord snsRecord = snsRecordList.get(0);

        context.getLogger().log(gson.toString());

        S3EventNotification s3eventNotifcation = parseJson(snsRecord.getSNS().getMessage());

        S3EventNotification.S3EventNotificationRecord record = s3eventNotifcation.getRecords().get(0);
        String srcBucket = record.getS3().getBucket().getName();
        String srcKey = record.getS3().getObject().getUrlDecodedKey();
        String imageType = imageUtils.determineImageType(srcKey);

        String mimeType = imageUtils.getMimeType(imageType);
        if (imageType.equals("")) {
            return "";
        }

        context.getLogger().log("Src bucket: " + srcBucket + " key:" + srcKey + " Img type: " + imageType + " Mime type: " + mimeType);


        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(srcBucket)
                .key(srcKey)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);


        try {

            BufferedImage srcImage = ImageIO.read(response);
            try {
                String dstBucket = destinationBucket;
                String dstKey = gifTypePrefix + srcKey;


                ByteArrayOutputStream gif = gifGenerator.generateGif(srcImage);


                PutObjectRequest objectRequest = PutObjectRequest.builder()
                        .bucket(dstBucket)
                        .contentType(GIF_MIME_TYPE)
                        .key(dstKey)
                        .build();

                s3Client.putObject(objectRequest, RequestBody.fromBytes(gif.toByteArray()));


            } catch (AmazonServiceException e) {
                context.getLogger().log("Write failed");
                context.getLogger().log(e.getErrorMessage());
                System.exit(1);
            }
        } catch (IOException e) {
            context.getLogger().log("Error reading image");
            e.printStackTrace();
        }
        return "OK";
    }

}
