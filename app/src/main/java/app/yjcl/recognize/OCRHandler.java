package app.yjcl.recognize;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Region;
import android.net.Uri;
import android.support.constraint.solver.widgets.Rectangle;
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
    private static final String imageURL = "http://www.folgerdigitaltexts.org/Images/grab6.png";
    public JSONObject toReturn;
    private static AsyncHttpClient httpClient;
    private byte[] input;
    Context context;

    OCRHandler(byte[] bytes, Context context){
        input = bytes;
        httpClient = new AsyncHttpClient();
        this.context = context;
    }
    OCRHandler(){
        httpClient = new AsyncHttpClient();
    }

    protected void processImage(){
        RequestParams params = new RequestParams();
        params.put("language", "en");
        params.put("detectOrientation", "true");
        httpClient.addHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

        try{
//          StringEntity entity = new StringEntity("{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/2/23/Space_Needle_2011-07-04.jpg\"}");
            ByteArrayEntity entity = new ByteArrayEntity(input);
            httpClient.post(null, uriBase, entity, "application/octet-stream", new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    toReturn = response;
                    Log.e("a", "b");
                }
                public void onFailure(JSONObject errorResponse, Throwable error) {
                    Log.e("ERROR", "failure in HTTP Request", error);
                }
            });
        }
        catch(Exception e){
            Log.e("ERROR:","Exception");
        }
//        return toReturn;
    }

    public void searchWeb(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}