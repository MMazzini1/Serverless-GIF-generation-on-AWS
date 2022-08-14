package lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public interface GifGenerationLambda {


    public String handleRequest(SNSEvent snsEvent, Context context);
}
