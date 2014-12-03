package com.cs371m.ads.karma_farm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.util.Log;

public class RemoteData {

    private static final String TAG = "RemoteData";

    public static HttpURLConnection getConnection(String url){
        System.out.println("URL: " + url);
        HttpURLConnection hcon = null;

        try {
            hcon = (HttpURLConnection) new URL(url).openConnection();
            hcon.setReadTimeout(30000); // Timeout at 30 seconds
            hcon.setRequestProperty("User-Agent", "KarmaFarm v0");

        } catch (MalformedURLException e) {
            Log.e("getConnection()",
                    "Invalid URL: " + e.toString());
        } catch (IOException e) {
            Log.e("getConnection()",
                    "Could not connect: " + e.toString());
        }

        return hcon;
    }

    /**
     * A very handy utility method that reads the contents of a URL
     * and returns them as a String.
     *
     * @param url
     * @return
     */
    public static String readContents(String url){
        HttpURLConnection hcon = getConnection(url);

        if (hcon == null) return null;

        try{
            StringBuffer sb = new StringBuffer(8192);
            String tmp = "";

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            hcon.getInputStream()
                    )
            );

            while((tmp = br.readLine()) != null)
                sb.append(tmp).append("\n");

            br.close();

            return sb.toString();
        }catch(IOException e){
            Log.d(TAG, "READ FAILED: " + e.toString());

            return null;
        }
    }
}