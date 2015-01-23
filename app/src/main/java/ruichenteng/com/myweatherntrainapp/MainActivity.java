package ruichenteng.com.myweatherntrainapp;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

public class MainActivity extends Activity {
    private final String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=Melbourne,au";
    private final String quoteUrl = "http://www.iheartquotes.com/api/v1/random";
    private final int maxImgId = 18;
    Bitmap bitmap;
    private String description;
    private String wind;
    private int temp_max;
    private int temp_min;
    private TextView tv,tv2;
    //private View view;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        //view = (View) findViewById(R.id.view);
        iv = (ImageView) findViewById(R.id.imageView);
        /*load image from SD card
        String pathName = "https://github.com/tengr/MyWeatherNTrainApp/blob/master/app/src/main/res/drawable/pic1.jpg";
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeFile(pathName);
        BitmapDrawable bd = new BitmapDrawable(res, bitmap);
        view.setBackground(bd);
        */
        new loadImage().execute();
        new getWeather().execute();
        new getQuote().execute();
    }


    private class getWeather extends AsyncTask<Void, Void, Void> {
        String jsonString = "";
        String main = "";
        String weather = "";
        @Override
        protected Void doInBackground(Void... params) {

            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost(weatherUrl);
            httpPost.setHeader("Content-type","application/json");
            InputStream inputstream = null;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                inputstream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream,"UTF-8"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jsonString = sb.toString();
                JSONParser parser = new JSONParser();
                JSONObject jObject = (JSONObject) parser.parse(jsonString.trim());
                main = jObject.get("main").toString();
                JSONArray weatherArr = (JSONArray) jObject.get("weather");
                weather = weatherArr.get(0).toString();
                JSONObject weatherJSON = (JSONObject)parser.parse(weather.trim());
                JSONObject tempJson = (JSONObject) parser.parse(main.trim());
                description = weatherJSON.get("description").toString();
                temp_max = (int) Math.round(Double.parseDouble(tempJson.get("temp_max").toString()) - 273);
                temp_min = (int) Math.round(Double.parseDouble(tempJson.get("temp_min").toString()) - 273);
                wind = jObject.get("wind").toString();

            }catch(Exception e){
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tv.setText(description
                        + " ,High:" + temp_max + "\u00b0" + "C"
                        + " ,Low:" + temp_min + "\u00b0" + "C"
                        + "\nWind(m/s):" + wind);
        }

    }


    private class getQuote extends AsyncTask<Void, Void, Void> {
        String quote = "";
        @Override
        protected Void doInBackground(Void... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost(quoteUrl);
            httpPost.setHeader("Content-Type", "text/xml");
            InputStream inputstream = null;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                inputstream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null && !line.contains("[")) {
                    sb.append(line + "\n");
                }
                quote = sb.toString().trim().replace("&quot;", "\"");
            }catch(Exception e){
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tv2.setText(quote);
        }
    }



    private class loadImage extends AsyncTask<Void, Void, Void> {
        BitmapDrawable bd = null;
        Random rd = new Random(System.currentTimeMillis());
        String fileName = "pic" + rd.nextInt(maxImgId) + ".jpg";
        @Override
        protected Void doInBackground(Void... params) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new
                        URL("https://raw.githubusercontent.com/tengr/MyWeatherNTrainApp/master/app/src/main/res/drawable/"
                            + fileName ).getContent());
                Resources res = getResources();
                bd = new BitmapDrawable(res, bitmap);
                }
            catch (Exception e) {
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //view.setBackground(bd);
            iv.setBackground(bd);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
