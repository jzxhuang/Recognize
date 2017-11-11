package app.yjcl.recognize;

import android.content.Context;
import android.util.Log;

import org.json.*;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

import java.net.URI;

import org.json.JSONObject;

//import okhttp3.*;

/**
 * Created by Jeff on 2017-11-11.
 */

public class OCRHandler {
    public static final String subscriptionKey = "f37f9bb130094c6a81ee64e6b6a97be7";
    public static final String uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/ocr";
    //public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final Context context;

    private static AsyncHttpClient httpClient;
    private byte[] input;
    private static final String imageURL = "http://www.folgerdigitaltexts.org/Images/grab6.png";
    OCRHandler(byte[] bytes, Context context){
        input = bytes;
        httpClient = new AsyncHttpClient();
        this.context = context;
    }
    OCRHandler(Context context){
        this.context = context;
        httpClient = new AsyncHttpClient();
    }

    protected void processImage(){
        //URI uri = httpClient.getURI(uriBase);
        RequestParams params = new RequestParams();
        params.put("language", "unk");
        params.put("detectOrientation", "true");

        //httpClient.addHeader("Content-Type", "application/json");
        httpClient.addHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

        StringEntity entity;

        try{
            entity = new StringEntity("https://upload.wikimedia.org/wikipedia/commons/2/23/Space_Needle_2011-07-04.jpg", "UTF-8");
            httpClient.post(context, uriBase, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.e("Success", "success");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e("ERROR", "failure");
                }


            });
        }
        catch(Exception e){
            Log.e("ERROR:","ERROR");
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