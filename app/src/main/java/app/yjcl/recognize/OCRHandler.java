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

import java.util.Iterator;

/**
 * Created by Jeff on 2017-11-11.
 */

public class OCRHandler {
    private static final String subscriptionKey = "f37f9bb130094c6a81ee64e6b6a97be7";
    private static final String uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/ocr";

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
//          StringEntity entity = new StringEntity("{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/2/23/Space_Needle_2011-07-04.jpg\"}");
            ByteArrayEntity entity = new ByteArrayEntity(input);
            httpClient.post(null, uriBase, entity, "application/octet-stream", new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray regions = null;
                    try {
                        regions = response.getJSONArray("regions");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String result = response.toString();
                    Log.e("REST Result:", result);

                    StringBuilder s = new StringBuilder();
                    try {
                        for (int i = 0; i < regions.length(); i++){
                            for (int j = 0; j < regions.getJSONObject(i).getJSONArray("lines").length(); j++){
                                for (int k=0; k< regions.getJSONObject(i).getJSONArray("lines").getJSONObject(j).getJSONArray("words").length(); k++){
                                    s.append(regions.getJSONObject(i).getJSONArray("lines").getJSONObject(j).getJSONArray("words").getJSONObject(k).getString("text"));
                                }
                            }
                        }
//                        String modifiedResult = response.getJSONArray("regions").getJSONObject(0).getJSONArray("lines").getJSONObject(0).getJSONArray("words").getJSONObject(0).getString("text");
                        Log.e("Result", s.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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