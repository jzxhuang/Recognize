package app.yjcl.recognize;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    private int[] intArr;
    private Rectangle[] rectArr;
    private String[] strArr;
    private boolean searchOnline;
    private boolean copyToClip;
    private String userSelect;
    private Intent intent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        // Get bitmap and boolean data from previous activity
        Bundle extras = getIntent().getExtras();
//        Uri uri = Uri.parse(extras.getString("data"));

        Bitmap bitmapImage = (Bitmap) extras.getParcelable("data");

        // Convery bitmap into byte array for processing
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        imageView = (ImageView) findViewById(R.id.ocrImage);
        imageView.setImageBitmap(bitmapImage);

        // Send a POST request to Microsoft
        POST(byteArray);
    }

    // Web search methods.  Currently only searchWeb works
    public void searchWeb(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // Does not work
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // The POST method does most of the calculations in app.  It is run asynchronously
    private void POST(byte[] bytes) {
        // Make a POST request to Microsoft's OCR API
        httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("language", "en");
        params.put("detectOrientation", "true");
        httpClient.addHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

        try {
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

                    // Concatenates text strings from the JSON object returned by Microsoft
                    try {
                        for (int i = 0; i < regions.length(); i++) {
                            for (int j = 0; j < regions.getJSONObject(i).getJSONArray("lines").length(); j++) {
                                for (int k = 0; k < regions.getJSONObject(i).getJSONArray("lines").getJSONObject(j).getJSONArray("words").length(); k++) {
                                    s.append(regions.getJSONObject(i).getJSONArray("lines").getJSONObject(j).getJSONArray("words").getJSONObject(k).getString("text"));
                                    boxes.append(regions.getJSONObject(i).getJSONArray("lines").getJSONObject(j).getJSONArray("words").getJSONObject(k).getString("boundingBox"));
                                    s.append(' ');
                                    boxes.append(' ');
                                }
                            }
                        }

                        String textOut = s.toString();
                        strArr = textOut.split("\\s");

                        // converting boxes (str array) to int array to rect array
                        String boxOut = boxes.toString().replace(',', ' ');
                        String[] splitOut = boxOut.split("\\s+");
                        int[] numOut = new int[splitOut.length];

                        // Error checking to prevent app from crashing if OCR fails to recognize text
                        if (splitOut[0].toString() == "") {
                            Toast.makeText(OcrActivity.this, "Image Recognition Failed", Toast.LENGTH_SHORT).show();
                            Intent restart = new Intent(OcrActivity.this, MainActivity.class);
                            startActivity(restart);
                        } else {
                            for (int i = 0; i < splitOut.length; i++) {
                                numOut[i] = Integer.parseInt(splitOut[i]);
                            }
                            Rectangle[] rectArray = new Rectangle[numOut.length / 4];
                            for (int i = 0; i < numOut.length / 4; i++) {
                                rectArray[i] = new Rectangle();
                                rectArray[i].setBounds(numOut[i], numOut[i + 1], numOut[i + 2], numOut[i + 3]);
                                //canvas.drawRect(numOut[i], numOut[i+1], numOut[i+2], numOut[i+3]);
                            }
                            rectArr = rectArray;
                            intArr = numOut;

//                        Paint myPaint = new Paint();
//                        myPaint.setStyle(Paint.Style.STROKE);
//                        myPaint.setColor(Color.rgb(255, 0, 0));
//                        myPaint.setStrokeWidth(10);
//                        Bitmap bitmapOverlay = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
//                        cv = new Canvas(bitmapOverlay);
//                        for(int i = 0; i < intArr.length/4; i++){
//                            cv.drawRect(numOut[i]*Scalex, numOut[i+1]*Scaley, numOut[i+2]*Scalex, numOut[i+3]*Scaley, myPaint);
//                        }
//                        imageViewOverlay.setImageBitmap(bitmapOverlay);

                            Log.e("Result", s.toString());
                            Log.e("Boxes", boxOut);
                            Log.e("Array", splitOut[2]);

                            intent = new Intent(OcrActivity.this, MainActivity.class);
                            AlertDialog.Builder alertBuilder1 = new AlertDialog.Builder(OcrActivity.this);
                            final AlertDialog.Builder alertBuilder2 = new AlertDialog.Builder(OcrActivity.this);
                            alertBuilder1.setTitle("Select your text");
                            alertBuilder1.setSingleChoiceItems(strArr, -1, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface d, int i){
                                    userSelect = strArr[i];
                                }
                            })
                                    .setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(intent);
                                        }
                                    })
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            alertBuilder2.setMessage(userSelect)
                                                    .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            searchWeb(userSelect);
                                                        }
                                                    })
                                                    .setNeutralButton("Copy to Clipboard", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            ClipboardManager clipboard = (ClipboardManager)
                                                                    getSystemService(OcrActivity.CLIPBOARD_SERVICE);
                                                            ClipData clip = ClipData.newPlainText("Link", userSelect);
                                                            clipboard.setPrimaryClip(clip);
                                                            Toast.makeText(OcrActivity.this, "Copied " + userSelect +" to clipboard!", Toast.LENGTH_SHORT).show();
                                                            startActivity(intent);
                                                        }
                                                    });
                                            AlertDialog alert2 = alertBuilder2.create();
                                            alert2.show();
                                        }
                                    });
                            AlertDialog alert1 = alertBuilder1.create();
                            alert1.show();

                            alertBuilder2.setTitle("What would you like to do?");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(JSONObject errorResponse, Throwable error) {
                    Log.e("ERROR", "failure in HTTP Request", error);
                }
            });
        } catch (Exception e) {
            Log.e("ERROR:", "Exception");
        }
    }
}