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
    Map<String, String> responseHeaders = new HashMap<>();

    Gson gson = new Gson();
    Extractor extractor = new Extractor();
    Map<String, ResponseBody> cache = new HashMap<>();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("start handleRequest");
        String requestBodyString = input.getBody();
        RequestBody requestBody = gson.fromJson(requestBodyString, RequestBody.class);

        responseHeaders.put("Content-Type", "application/json");
        responseHeaders.put("X-Custom-Header", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(responseHeaders);

        // check if the url is in the cache
        if (cache.containsKey(requestBody.url)) {
            logger.log("url is in the cache");
            response.setBody(gson.toJson(cache.get(requestBody.url)));
            response.setStatusCode(200);
            return response;
        } else {
            logger.log("url is not in the cache");
            try {
                extractor.loadURL(new URL(requestBody.url));
                List<ExamSession> exams = extractor.extract_csv_to_ExamSessions();
                ResponseBody responseBody = new ResponseBody(exams);
                cache.put(requestBody.url, responseBody);
                logger.log("done cache result");
                return response
                        .withStatusCode(200)
                        .withBody(gson.toJson(responseBody));

            } catch (Exception e) {
                return response
                        .withBody(gson.toJson("something went wrong"))
                        .withStatusCode(500);
            }
        }
    }

}
