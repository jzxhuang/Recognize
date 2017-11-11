package app.yjcl.recognize;

import android.util.Log;

import org.json.*;

//import com.loopj.android.http.*;
//import cz.msebera.android.httpclient.Header;
//import cz.msebera.android.httpclient.HttpEntity;
import java.net.URI;

import org.json.JSONObject;

import okhttp3.*;

/**
 * Created by Jeff on 2017-11-11.
 */

public class OCRHandler {
    public static final String subscriptionKey = "f37f9bb130094c6a81ee64e6b6a97be7";
    public static final String uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient httpClient;
    private byte[] input;
    //private static AsyncHttpClient httpClient;
    private static final String imageURL = "http://www.folgerdigitaltexts.org/Images/grab6.png";
    OCRHandler(byte[] bytes){
        input = bytes;
        httpClient = new OkHttpClient();
    }
    protected void processImage(){
        try{
            HttpUrl.Builder urlBuilder = HttpUrl.parse(uriBase).newBuilder();
            urlBuilder.addQueryParameter("language", "unk");
            urlBuilder.addQueryParameter("detectOrientation", "true");
            String url = urlBuilder.build().toString();

            RequestBody body = RequestBody.create(JSON, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/Atomist_quote_from_Democritus.png/338px-Atomist_quote_from_Democritus.png");

            Request request = new Request.Builder()
                    .header("Content-Type", "application/json")
                    .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                    .url(url)
                    .post(body)
                    .build();
            Response response = httpClient.newCall(request).execute();
            if(response.body()!= null){
                Log.e("REST Response: \n", response.body().string());
            }


            //URIBuilder uriBuilder = new URIBuilder(uriBase);

//            uriBuilder.setParameter("language", "unk");
//            uriBuilder.setParameter("detectOrientation ", "true");

            // Request parameters.
            //URI uri = uriBuilder.build();
            //HttpPost request = new HttpPost(uri);

            // Request headers.
            //request.setHeader("Content-Type", "application/json");
//            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            // Request body.
//            StringEntity requestEntity = new StringEntity("{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/Atomist_quote_from_Democritus.png/338px-Atomist_quote_from_Democritus.png\"}");
//            request.setEntity(requestEntity);

            // Execute the REST API call and get the response entity.
//            HttpResponse response = httpClient.execute(request);
//            HttpEntity entity = response.getEntity();
//
//            if(entity != null){
//                // Format and display the JSON response.
//                String jsonString = EntityUtils.toString(entity);
//                JSONObject json = new JSONObject(jsonString);
//                Log.e("REST Response:\n", json.toString(2));
//            }
        }
        catch (Exception e){
            Log.e("Error:", "Exception caught!");
        }
    }


//    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        httpClient.get(getAbsoluteUrl(url), params, responseHandler);
//    }
//
//    private static String getAbsoluteUrl(String relativeUrl) {
//        return imageURL + relativeUrl;
//    }
//
//    protected void processImage() throws JSONException{
//        httpClient.getURI("https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/ocr");
//
//        RequestParams params = new RequestParams();
//        params.put("language", "unk");
//        params.put("detectOrientation", "true");
//
//        httpClient.addHeader("Content-Type", "application/json");
//        httpClient.addHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
//
//        httpClient.post(String "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0", HttpEntity entity, ResponseHandlerInterface responseHandler){
//
//        }
//
//        this.get("", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                //successful JSON object returned
//                try {
//                    String jsonString = new String(responseBody, "UTF-8");
//                    Log.e("JSON: ", jsonString);
//                    JSONObject json = new JSONObject(jsonString);
//                    Log.e("JSON: ", json.toString(2));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                // fail
//                Log.e("JSON: ", "fail");
//            }
//        });
//    }
}