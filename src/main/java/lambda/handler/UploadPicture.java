package lambda.handler;

import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lambda.imageprocessing.ImageUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.MultipartStream;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


public class UploadPicture implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    private static final Logger logger = LoggerFactory.getLogger(UploadPicture.class);
    public static final String UPLOAD_BUCKET_NAME = "image-processing-app-uploads";
    private ImageUtils imageUtils;
    private Gson gson;
    private S3Client s3Client;


    public UploadPicture() {
        this.imageUtils = new ImageUtils();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        Region region = Region.US_EAST_1;
        s3Client = S3Client.builder()
                .region(region)
                .build();

    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        //Create the logger LambdaLogger
        logger.info("Loading Java Lambda handler of Proxy");
        logger.info("API GATEWAY EVENT: " + gson.toJson(event));

        //Log the length of the incoming body      
        logger.info("Byte length: " + String.valueOf(event.getBody().getBytes().length));

        //Create the APIGatewayProxyResponseEvent response     
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        //Set up contentType 
        String contentType = "";

        //Change these values to fit your region and bucket name
        //Every file will be named image.jpg in this example. 
        //You will want to do something different here in production 


        try {
            //Get the uploaded file and decode from base64 
            byte[] bI = Base64.decodeBase64(event.getBody().getBytes());

            //Get the content-type header  
            Map<String, String> hps = event.getHeaders();

            if (hps != null) {
                contentType = hps.get("content-type");
                logger.info("Content type: " + contentType);
            }

            //Extract the boundary
            String[] boundaryArray = contentType.split("=");

            //Transform the boundary to a byte array 
            byte[] boundary = boundaryArray[1].getBytes();

            //Log the extraction for verification purposes 


            //Create a ByteArrayInputStream 
            ByteArrayOutputStream out = readMultiPart(bI, boundary);

            //Log completion of MultipartStream processing 
            logger.info("Data written to ByteStream");

            //Prepare an InputStream from the ByteArrayOutputStream      
            InputStream fis = new ByteArrayInputStream(out.toByteArray());



            String mimeType = URLConnection.guessContentTypeFromStream(fis);
            logger.info("File ctt type: " + mimeType);


            //Put file into S3



            String objectKey = UUID.randomUUID() + "." + imageUtils.getFileTypeFromMimeType(mimeType);

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(UPLOAD_BUCKET_NAME)
                    .key(objectKey)
                    .build();

            s3Client.putObject(objectRequest, RequestBody.fromBytes(out.toByteArray()));



            //Log status 
            logger.info("Object saved in S3");

            buildResponse(response, objectKey);

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but 
            // Amazon S3 couldn't process it, so it returned
            // an error response. 
            logger.error(e.getMessage());
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or
            // the client couldn't parse the response from Amazon S3.      
            logger.error(e.getMessage());
        } catch (IOException e) {
            // Handle MultipartStream class IOException   
            logger.error(e.getMessage());
        }


        //logger.info(response.toString());
        return response;
    }

    private ByteArrayOutputStream readMultiPart(byte[] bI, byte[] boundary) throws IOException {
        ByteArrayInputStream content = new ByteArrayInputStream(bI);

        //Create a MultipartStream to process the form-data
        MultipartStream multipartStream = new MultipartStream(content, boundary, bI.length, null);


        //Create a ByteArrayOutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //Find first boundary in the MultipartStream
        boolean nextPart = multipartStream.skipPreamble();

        //Loop through each segment
        while (nextPart) {
            String header = multipartStream.readHeaders();
            //Log header for debugging
            logger.info("Multipart headers: "+ header);
            //Write out the file to our ByteArrayOutputStream
            multipartStream.readBodyData(out);
            //Get the next part, if any
            nextPart = multipartStream.readBoundary();

        }
        return out;
    }

    private void buildResponse(APIGatewayProxyResponseEvent response, String fileObjKeyName) {
        response.setStatusCode(200);
        Map<String, String> responseBody = new HashMap<String, String>();
        responseBody.put("id", fileObjKeyName);
        responseBody.put("Status", "File stored in S3");
        String responseBodyString = gson.toJson(responseBody);
        response.setBody(responseBodyString);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Control-Allow-Origin","*");
        response.setHeaders(headers);
    }

}

