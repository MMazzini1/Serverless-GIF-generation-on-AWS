package lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import lambda.imageprocessing.gifgeneration.CyclicGifGenerator;
import lambda.imageprocessing.gifgeneration.RecursiveGifGenerator;

public class CreateCyclicGif implements RequestHandler<SNSEvent, String> {
    public static final String PROCESSED_IMAGES_BUCKET = "image-processing-app-destination";
    public static final String BLUR_GIF_PREFIX = "resized3-";
    private final GIFGenerationLambdaHandler gifGenerationLambda;


    public CreateCyclicGif() {
        this.gifGenerationLambda = new GIFGenerationLambdaHandler(new CyclicGifGenerator(), PROCESSED_IMAGES_BUCKET,  BLUR_GIF_PREFIX);
    }


    @Override
    public String handleRequest(SNSEvent snsEvent, Context context) {
        return gifGenerationLambda.handleRequest(snsEvent, context);

    }


}
