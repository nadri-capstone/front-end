package com.example.nadri4_edit1;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
public class ReqServer {
    public static void request(Context context, long data){
        String url = "http://20.227.167.127:18923/";
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        JSONObject reqJson = new JSONObject();
        try {
            reqJson.put("android_id", android_id);
            reqJson.put("time_data", data);
        } catch (JSONException e) {
            Log.d("HWA", "JSON 오류");
        } catch (Exception e){
            Log.d("HWA", e + "");
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, reqJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("HWA", "응답: " + response);
            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HWA", "에러: " + error);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }
}