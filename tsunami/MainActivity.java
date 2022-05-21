package com.example.tsunami;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView textViewTitle;
    private TextView textViewDate;
    private TextView textViewTsunami;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?minmagnitude=6&format=geojson";
        URL url = createURL(USGS_REQUEST_URL);
        initViews();
        new TsunamiAsyncTask().execute(url);
    }

    private URL createURL(String usgsRequestUrl) {
        URL url = null;

        try {
            url = new URL(usgsRequestUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private String makeHttpRequset(URL url) {
        String jsonResponse = null;
        final int TIME_OUT = 15_000;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            //Request
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(TIME_OUT);
            httpURLConnection.setReadTimeout(TIME_OUT);

            //Connection
            httpURLConnection.connect();

            //Response
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Toast.makeText(this, "Error: Response Code: " +
                        httpURLConnection.getResponseCode(), Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) {
        StringBuilder jsonResponse = new StringBuilder();

        if (inputStream != null) {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    jsonResponse.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonResponse.toString();
    }

    private Earthquake getFeaturesFromJson(String jsonResponse) {
        Earthquake earthquake = null;

        if (!jsonResponse.equals("") && jsonResponse != null) {
            try {
                JSONObject root = new JSONObject(jsonResponse);
                JSONArray features = root.getJSONArray("features");

                if (features.length() > 0) {
                    JSONObject firstElement = features.getJSONObject(0);
                    JSONObject properties = firstElement.getJSONObject("properties");

                    String title = properties.getString("title");
                    long time = properties.getLong("time");
                    int tsunami = properties.getInt("tsunami");
                    String url = properties.getString("url");

                    earthquake = new Earthquake(title, time, tsunami, url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return earthquake;
    }

    private void initViews() {
        textViewTitle = findViewById(R.id.text_view_title);
        textViewDate = findViewById(R.id.text_view_date);
        textViewTsunami = findViewById(R.id.text_view_tsunami);
    }

    private void updateUI(Earthquake earthquake) {
        String title = earthquake.getTitle();
        textViewTitle.setText(title);
        updateTitleURL(earthquake.getUrl());

        String time = getTimeString(earthquake.getTime());
        textViewDate.setText(time);

        String tsunami = getTsunamiString(earthquake.getTsunami());
        textViewTsunami.setText(tsunami);
    }

    private void updateTitleURL(String url) {
        textViewTitle.setOnClickListener(view -> {
            WebActivity.setUrl(url);
            Intent navigateIntent = new Intent(this, WebActivity.class);
            startActivity(navigateIntent);
        });
    }

    private String getTsunamiString(int tsunami) {
        switch (tsunami) {
            case 1:
                return "yes";
            case 0:
                return "no";
            default:
                return "not available";
        }
    }

    private String getTimeString(long time) {
        String pattern = "EEE, dd MMM YYYY 'at' hh:mm:ss z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(time));
    }

    class TsunamiAsyncTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            String jsonResponse = makeHttpRequset(urls[0]);
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            super.onPostExecute(jsonResponse);

            Earthquake earthquake = getFeaturesFromJson(jsonResponse);
            updateUI(earthquake);
        }
    }
}