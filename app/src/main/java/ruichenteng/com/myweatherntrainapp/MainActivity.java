package ruichenteng.com.myweatherntrainapp;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

public class MainActivity extends Activity {
    private String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=Melbourne,au";
    private String quoteUrl = "http://www.iheartquotes.com/api/v1/random";
    private String description;
    private String wind;
    private int temp_max;
    private int temp_min;
    private TextView tv,tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        String pathName = "/storage/extSdCard/tiffany-ring1.jpg";
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeFile(pathName);
        BitmapDrawable bd = new BitmapDrawable(res, bitmap);
        View view = findViewById(R.id.view);
        view.setBackground(bd);
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
                tv.setText("Exception!");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tv.setText("Melbourne Weather:\nDescription: " + description
                        + "\nHigh:" + temp_max + "\u00b0" + "C"
                        + "\nLow:" + temp_min + "\u00b0" + "C"
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
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                quote = sb.toString();
            }catch(Exception e){
                tv2.setText("Exception Quote");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tv2.setText("Quote of the day:\n" + quote.split("\n\n")[0].replace("&quot;", "\""));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
