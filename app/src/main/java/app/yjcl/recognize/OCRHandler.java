package app.yjcl.recognize;

import android.content.Context;
import android.graphics.Region;
import android.util.Log;

import org.json.*;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

import org.json.JSONObject;

/**
 * Created by Jeff on 2017-11-11.
 */

public class OCRHandler {
<<<<<<< HEAD
    private static final String subscriptionKey = "f37f9bb130094c6a81ee64e6b6a97be7";
    private static final String uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/ocr";
=======
    public static final String subscriptionKey = "f37f9bb130094c6a81ee64e6b6a97be7";
    public static final String uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/ocr";
    //public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public final Context context;
>>>>>>> 9ae265612f5a59a185f886b17ba4034c30143869

    private static AsyncHttpClient httpClient;
    private byte[] input;
    private static final String imageURL = "http://www.folgerdigitaltexts.org/Images/grab6.png";
    OCRHandler(byte[] bytes){
        input = bytes;
        httpClient = new AsyncHttpClient();
    }
    OCRHandler(){
        httpClient = new AsyncHttpClient();
    }

    protected void processImage(){
        RequestParams params = new RequestParams();
        params.put("language", "unk");
        params.put("detectOrientation", "true");
        httpClient.addHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

        try{
<<<<<<< HEAD
//          StringEntity entity = new StringEntity("{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/2/23/Space_Needle_2011-07-04.jpg\"}");
            ByteArrayEntity entity = new ByteArrayEntity(input);
            httpClient.post(null, uriBase, entity, "application/octet-stream", new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    String result = response.toString();
                    Log.e("REST Result:", result);
=======
            entity = new StringEntity("{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/1/12/Broadway_and_Times_Square_by_night.jpg\"}");
            httpClient.post(null, uriBase, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.e("Success", "success");
                    JSONObject result= null;
                    try {
                        result = new JSONObject(new String(responseBody));
                        String resultString = result.toString();
                        Log.e("Test", resultString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

>>>>>>> 9ae265612f5a59a185f886b17ba4034c30143869
                }
                public void onFailure(JSONObject errorResponse, Throwable error) {
                    Log.e("ERROR", "failure in HTTP Request", error);
                }
            });
        }
        catch(Exception e){
            Log.e("ERROR:","Exception");
        }
    }
}