package com.yashuthar.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    TextView resultTextview;
    public void findWeather(View view){
        Log.i("City Name", cityName.getText().toString());
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);
        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(),"UTF-8");
            DownLoadTask task = new DownLoadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=7ce7bcf35bd01ee881fe4e83c74f5a19");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public class DownLoadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;
            String result = "";
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while(data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return  result;
            } catch (Exception e) {

                Toast.makeText(MainActivity.this, "Could not find weather", Toast.LENGTH_SHORT).show();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String message = "";
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherinfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherinfo);
                for(int i = 0; i < arr.length(); i++)
                {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";
                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if(main != "" && description != "")
                    {
                        message += main + ": " + description + "\r\n";
                    }
                    if(message != "")
                    {
                        resultTextview.setText(message);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Could not find weather", Toast.LENGTH_SHORT).show();
                    }

                }

            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, "Could not find weather", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = (EditText) findViewById(R.id.cityName);
        resultTextview = (TextView) findViewById(R.id.resultTextview);
    }
}