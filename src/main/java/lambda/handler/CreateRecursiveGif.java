package lambda.handler;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import lambda.imageprocessing.gifgeneration.BlurGifGenerator;
import lambda.imageprocessing.gifgeneration.RecursiveGifGenerator;


public class CreateRecursiveGif implements RequestHandler<SNSEvent, String> {
    public static final String PROCESSED_IMAGES_BUCKET = "image-processing-app-destination";
    public static final String BLUR_GIF_PREFIX = "resized2-";
    private final GIFGenerationLambdaHandler gifGenerationLambda;



    public CreateRecursiveGif() {
        this.gifGenerationLambda = new GIFGenerationLambdaHandler(new RecursiveGifGenerator(), PROCESSED_IMAGES_BUCKET,  BLUR_GIF_PREFIX);
    }


    @Override
    public String handleRequest(SNSEvent snsEvent, Context context) {
       return gifGenerationLambda.handleRequest(snsEvent, context);

    }


}
