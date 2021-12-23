package extractor;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> responseHeaders = new HashMap<>();
        LambdaLogger logger = context.getLogger();
        responseHeaders.put("Content-Type", "application/json");
        responseHeaders.put("X-Custom-Header", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(responseHeaders);
        Gson gson = new Gson();
        String requestBodyString = input.getBody();
        RequestBody requestBody = gson.fromJson(requestBodyString, RequestBody.class);

        try {
            Extractor extractor = new Extractor(new URL(requestBody.url));
            List<ExamSession> exams = extractor.extract_csv_to_ExamSessions();
            logger.log("Started extraction");
            return response
                    .withStatusCode(200)
                    .withBody(gson.toJson(new ResponseBody(exams)));

        } catch (Exception e) {
            return response
                    .withBody(gson.toJson("something went wrong"))
                    .withStatusCode(500);
        }
    }

}
