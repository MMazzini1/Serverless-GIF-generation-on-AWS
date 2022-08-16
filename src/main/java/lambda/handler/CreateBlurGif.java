package lambda.handler;


import com.amazonaws.services.lambda.runtime.Context;


import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import lambda.imageprocessing.gifgeneration.BlurGifGenerator;


public class CreateBlurGif implements RequestHandler<SNSEvent, String> {

    public static final String PROCESSED_IMAGES_BUCKET = "image-processing-app-destination";
    public static final String BLUR_GIF_PREFIX = "resized-";
    private final GIFGenerationLambdaHandler gifGenerationLambda;

    public CreateBlurGif() {
        this.gifGenerationLambda = new GIFGenerationLambdaHandler(new BlurGifGenerator(), PROCESSED_IMAGES_BUCKET,  BLUR_GIF_PREFIX);
    }

    @Override
    public String handleRequest(SNSEvent snsEvent, Context context) {
       return gifGenerationLambda.handleRequest(snsEvent, context);

    }

}
