package com.avoscloud.chat.util;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/8.
 */
public class GetCity {
    public static String STATIC_URL = "http://api.map.baidu.com/geocoder/v2/?ak=5FGc6bb3oFOBGjks5Inf6dsX&location=";

    public static String getJson(double lat, double log) {
        StringBuilder builder = new StringBuilder();
        InputStreamReader in = null;
        BufferedReader buff = null;
        if(lat != 0 && log != 0) {
            String newUrl = STATIC_URL + lat + "," + log + "&output=json";
            try {
                URL url = new URL(newUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(1000);
                in = new InputStreamReader(conn.getInputStream(),
                        "utf-8");
                buff = new BufferedReader(in);
                String inputLine = null;
                if(conn.getResponseCode() == 200) {
                    while ((inputLine = buff.readLine()) != null) {
                        builder.append(inputLine);
                    }
                }
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(buff != null) {
                        buff.close();
                    }if (in != null) {
                        in.close();
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    public static String getCity(double lat, double log) {
        String json = getJson(lat, log);
        Gson gson = new Gson();
        Map jo = gson.fromJson(json, Map.class);
        Map result = (Map) jo.get("result");
        Map addressComponent = (Map) result.get("addressComponent");
        return (String) addressComponent.get("city");
    }
}
