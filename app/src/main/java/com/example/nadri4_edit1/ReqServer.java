package com.example.ndritest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ReqServer {
    Context c;

    public ReqServer(Context context) {
        c = context;
    }

    //페이지 정보 요청하기
    public static void reqGetPages(Context context){
        //android_id 가져와서 ip 주소랑 합치기
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = context.getString(R.string.ipAddress) + android_id;

        //요청 만들기
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            //서버에서 응답받았을 때 동작할 함수
            @Override
            public void onResponse(String response) {
                try {
                    JsonElement jsonReq = JsonParser.parseString(response);
                    if (jsonReq.isJsonObject() == true) {
                        Log.d("HWA", "JsonObject " + jsonReq);
                    } else if (jsonReq.isJsonArray() == true) {
                        Log.d("HWA", "JsonArray " + jsonReq);
                    } else {
                        Log.d("HWA", "Else:" + jsonReq);
                    }
                } catch (Exception e) {
                    Log.d("HWA", "GET 에러: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HWA", "GET 에러: " + error);
            }
        });

        //큐에 넣어 서버로 응답 전송
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    //작성한 페이지 보내기
    @SuppressLint("RestrictedApi")
    public static void reqPostPages(Context context, ArrayList<Uri> photos){
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = context.getString(R.string.ipAddress) + android_id;

        //서버로 보낼 Json
        JSONArray reqJsonArr = new JSONArray();

        try{
            Log.d("HWA", "선택한 사진 개수: " + photos.size());

            for(int i = 0; i<photos.size(); i++){
                //사진 정보 가져오기
                InputStream inputStream = context.getContentResolver().openInputStream(photos.get(i));
                ExifInterface exif = new ExifInterface(inputStream);

                JSONObject photoJson = new JSONObject();
                photoJson.put("id", photos.get(i));

                //날짜시간 정보 가져오기
                Long dateTime = exif.getDateTime();
                photoJson.put("datetime", dateTime);

                //위치 정보 가져오기
                double latLong[] = exif.getLatLong();
                //경도, 위도, 주소를 담을 Json
                JSONObject location = new JSONObject();
                if(latLong != null){
                    Geocoder gCoder = new Geocoder(context);
                    List<Address> addressList = gCoder.getFromLocation(latLong[0], latLong[1], 1);
                    
                    location.put("lat", latLong[0]);
                    location.put("long", latLong[1]);
                    location.put("address", addressList.get(0).getAddressLine(0));

                    photoJson.put("location", location);
                }
                Log.d("HWA", "photoJson" + i + ": " + photoJson);

                reqJsonArr.put(photoJson);
                /*
                String timeData = exif.getAttribute(ExifInterface.TAG_DATETIME);
                String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                Log.d("HWA", "timeData: "+ timeData + " lat:" + lat + " lng: " + lng);*/
            }
            Log.d("HWA", "reqJsonArr: " + reqJsonArr);
        } catch (JSONException | IOException e) {
            Log.d("HWA", "POST 에러: " + e);
        }


        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.POST, url, reqJsonArr, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("HWA", "POST 응답: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HWA", "POST 에러: " + error);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }
}
