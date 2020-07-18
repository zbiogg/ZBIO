package com.tungkon.zbio.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tungkon.zbio.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {
    private ProgressDialog dialog;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        dialog = new ProgressDialog(SplashScreen.this);
        dialog.setCancelable(false);
        preferences =getSharedPreferences("user", Context.MODE_PRIVATE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                checkLogin();

            }
        }, 500);

    }
    public void checkLogin(){
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/checklogin", response -> {
            try {
                JSONObject object = new JSONObject(response);
                Log.d("qqqqq", String.valueOf(object));
                if(object.getBoolean("success")){
                    String message = object.getString("message");
                    if(message.equals("token alive")){
                        startActivity(new Intent(getApplicationContext(),DashBoardActivity.class));
                    }else{
                        startActivity(new Intent(SplashScreen.this,LoginActivity.class));

                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        },error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token","");
                HashMap<String, String> map= new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
}
