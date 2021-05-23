package com.example.progectforsamsung;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView res;
    private Button btn;
    private EditText inpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = findViewById(R.id.res);
        btn = findViewById(R.id.btn);
        inpt = findViewById(R.id.inpt);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inpt.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.nothing_enter, Toast.LENGTH_LONG).show();
                else {
                    String city = inpt.getText().toString();
                    String key = "4206eebd2b6f37a02b12d9731fbf2cec";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key +"&units=metric&lang=ru";

                    new GetURLData().execute(url);
                }
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            res.setText("Wait...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();

                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                res.setText("Температура: " +jsonObject.getJSONObject("main").getDouble("temp") + "\n");
                res.append("Температура по ощущениям: " +jsonObject.getJSONObject("main").getDouble("feels_like") + "\n");
                res.append("Влажность: " +jsonObject.getJSONObject("main").getDouble("humidity") + "\n");
                res.append("Скорость ветра: " +jsonObject.getJSONObject("wind").getDouble("speed") + " m/c" + "\n");
                res.append("Давление: " +jsonObject.getJSONObject("main").getDouble("pressure") + " bar" + "\n");
                res.append("Видимость: " +jsonObject.getDouble("visibility") + " m" + "\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}