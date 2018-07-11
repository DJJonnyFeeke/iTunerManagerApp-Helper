package me.personal.jfeeke.multimediamobileviewer;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GetUrlContentTask extends AsyncTask<String, Integer, String> {
    private GetUrlContentTaskResponse delegate = null;//Call back interface

    GetUrlContentTask(GetUrlContentTaskResponse asyncResponse) {
        delegate = asyncResponse;//Assigning call back interface through constructor
    }

    @Override
    protected String doInBackground(String... urls) {
        URL url = null;
        try {
            url = new URL(urls[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            if (url != null) {
                connection = (HttpURLConnection) url.openConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.setRequestMethod("GET");
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        if (connection != null) {
            connection.setDoOutput(true);
        }
        if (connection != null) {
            connection.setConnectTimeout(5000);
        }
        if (connection != null) {
            connection.setReadTimeout(5000);
        }
        try {
            if (connection != null) {
                connection.connect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = null;
        try {
            if (connection != null) {
                rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder content = new StringBuilder();
        String line;
        try {
            if (rd != null) {
                while ((line = rd.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        // this is executed on the main thread after the process is over
        // update your UI here
        delegate.processFinish(result);
    }
}
