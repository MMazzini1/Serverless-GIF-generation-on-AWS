package lambda.handler;


// import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import com.amazonaws.services.s3.event.S3EventNotification;

import java.util.List;

public class DeserializeSNSTest {

    public static final String PROCESSED_IMAGES_BUCKET = "image-processing-app-destination";


    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Test
    public void test(){

        String s = "{\n" +
                "    \"Records\": [\n" +
                "        {\n" +
                "            \"eventVersion\": \"2.1\",\n" +
                "            \"eventSource\": \"aws:s3\",\n" +
                "            \"awsRegion\": \"us-east-1\",\n" +
                "            \"eventTime\": \"2022-08-14T05:00:53.633Z\",\n" +
                "            \"eventName\": \"ObjectCreated:Put\",\n" +
                "            \"userIdentity\": {\n" +
                "                \"principalId\": \"AWS:AIDATM7ZUNFTJ54BC2FYS\"\n" +
                "            },\n" +
                "            \"requestParameters\": {\n" +
                "                \"sourceIPAddress\": \"190.16.185.178\"\n" +
                "            },\n" +
                "            \"responseElements\": {\n" +
                "                \"x-amz-request-id\": \"47A8FMQMJ67FJDMJ\",\n" +
                "                \"x-amz-id-2\": \"bWqpRWcxzmT3H+4ESOScpxGhjU1XpizWn8oeWFelYyQ+y0E98yrZHyOBfDAebWLJheHQ+4h8LPeDoQmjnkJVaxdjC1zPThoV\"\n" +
                "            },\n" +
                "            \"s3\": {\n" +
                "                \"s3SchemaVersion\": \"1.0\",\n" +
                "                \"configurationId\": \"ImageUpload\",\n" +
                "                \"bucket\": {\n" +
                "                    \"name\": \"image-processing-app-uploads\",\n" +
                "                    \"ownerIdentity\": {\n" +
                "                        \"principalId\": \"A1UFSTJEKRMP0I\"\n" +
                "                    },\n" +
                "                    \"arn\": \"arn:aws:s3:::image-processing-app-uploads\"\n" +
                "                },\n" +
                "                \"object\": {\n" +
                "                    \"key\": \"bitmap2.png\",\n" +
                "                    \"size\": 2616445,\n" +
                "                    \"eTag\": \"fe2fbb1ad6233d99bd3acbe222db4493\",\n" +
                "                    \"versionId\": \"Xjdr33opf12cQ_FTZHWv2m1_KXa9z1YR\",\n" +
                "                    \"sequencer\": \"0062F88185837FC714\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        S3EventNotification s3EventNotification = S3EventNotification.parseJson(s);
        S3EventNotification s3eventNotifcation = gson.fromJson(s, S3EventNotification.class);



    }


}
