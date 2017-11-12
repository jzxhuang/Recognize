package app.yjcl.recognize;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Handler;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;

public class OcrActivity extends AppCompatActivity {
    private static final String subscriptionKey = "f37f9bb130094c6a81ee64e6b6a97be7";
    private static final String uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/ocr";
    private static final String imageURL = "http://www.folgerdigitaltexts.org/Images/grab6.png";
    public JSONObject jsonPOST;
    private static AsyncHttpClient httpClient;
    private byte[] input;
    private Canvas cv;
    private ImageView imageView;
    private ImageView imageViewOverlay;
    private int[] intArr;
    private Rectangle[] rectArr;
    private String[] strArr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        Bitmap bitmapImage = (Bitmap) getIntent().getExtras().getParcelable("data");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);


        byte[] byteArray = stream.toByteArray();



        imageView = (ImageView) findViewById(R.id.ocrImage);
        imageView.setImageBitmap(bitmapImage);

        imageViewOverlay = (ImageView) findViewById(R.id.overlayImage);

//        OCRHandler OCRH = new OCRHandler(byteArray, OcrActivity.this);
//        OCRH.processImage();
//        JSONObject ocrReturn = OCRH.toReturn;
        POST(byteArray);
    }

    private void calculate(JSONObject response){
    }

    private void POST(byte[] bytes){
        httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("language", "en");
        params.put("detectOrientation", "true");
        httpClient.addHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

        try{
//          StringEntity entity = new StringEntity("{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/2/23/Space_Needle_2011-07-04.jpg\"}");
            ByteArrayEntity entity = new ByteArrayEntity(bytes);
            httpClient.post(null, uriBase, entity, "application/octet-stream", new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    jsonPOST = response;
                    Log.e("a", "b");
//                    calculate(jsonPOST);
                    JSONArray regions = null;
                    try {
                        regions = response.getJSONArray("regions");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String result = response.toString();
                    Log.e("REST Result:", result);

                    StringBuilder s = new StringBuilder();
                    StringBuilder boxes = new StringBuilder();

                    try {
                        for (int i = 0; i < regions.length(); i++){
                            for (int j = 0; j < regions.getJSONObject(i).getJSONArray("lines").length(); j++){
                                for (int k=0; k< regions.getJSONObject(i).getJSONArray("lines").getJSONObject(j).getJSONArray("words").length(); k++){
                                    s.append(regions.getJSONObject(i).getJSONArray("lines").getJSONObject(j).getJSONArray("words").getJSONObject(k).getString("text"));
                                    boxes.append(regions.getJSONObject(i).getJSONArray("lines").getJSONObject(j).getJSONArray("words").getJSONObject(k).getString("boundingBox"));
                                    s.append(' ');
                                    boxes.append(' ');
                                }
                            }
                        }
                        //
                        String textOut = s.toString();
                        strArr = textOut.split("\\s");

                        // converting boxes (str array) to int array to rect array
                        String boxOut = boxes.toString().replace(',', ' ');
                        String[] splitOut = boxOut.split("\\s+");
                        int[] numOut = new int[splitOut.length];
                        for(int i =0; i < splitOut.length; i++){
                            numOut[i] = Integer.parseInt(splitOut[i]);
                        }
                        Rectangle[] rectArray = new Rectangle[numOut.length/4];
                        for(int i = 0; i < numOut.length/4; i++){
                            rectArray[i] = new Rectangle();
                            rectArray[i].setBounds(numOut[i], numOut[i+1], numOut[i+2], numOut[i+3]);
                            //canvas.drawRect(numOut[i], numOut[i+1], numOut[i+2], numOut[i+3]);
                        }
                        rectArr = rectArray;
                        intArr = numOut;

                        Paint myPaint = new Paint();
                        myPaint.setStyle(Paint.Style.STROKE);
                        myPaint.setColor(Color.rgb(255, 0, 0));
                        myPaint.setStrokeWidth(10);
                        Bitmap bitmapOverlay = Bitmap.createBitmap(imageView.getHeight(), imageView.getWidth(), Bitmap.Config.ARGB_8888);
                        cv = new Canvas(bitmapOverlay);
                        for(int i = 0; i < intArr.length/4; i++){
                            cv.drawRect(numOut[i], numOut[i+1], numOut[i+2], numOut[i+3], myPaint);
                        }
                        imageViewOverlay.setImageBitmap(bitmapOverlay);

                        Log.e("Result", s.toString());
                        Log.e("Boxes", boxOut);
                        Log.e("Array", splitOut[2]);

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
