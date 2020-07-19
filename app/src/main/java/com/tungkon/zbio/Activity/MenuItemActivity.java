package com.tungkon.zbio.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.tungkon.zbio.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MenuItemActivity extends AppCompatActivity {
    Button btnlogout;
    ImageButton btnback;
    SharedPreferences preferences;
    ProgressDialog dialog;
    ImageView imgUserAvt;
    TextView txtUserName;
    LinearLayout lnviewprofile,lnviewaboutus;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);
        imgUserAvt = findViewById(R.id.imgUserAvtMenu);
        txtUserName = findViewById(R.id.txtUserNameMenu);
        btnlogout = findViewById(R.id.btnlogout);
        btnback =findViewById(R.id.btnback);
        fragmentManager = getSupportFragmentManager();
        lnviewprofile = findViewById(R.id.lnviewprofile);
        lnviewaboutus = findViewById(R.id.ln_view_about_us);
        lnviewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuItemActivity.this,MyProfileActivity.class);
                startActivity(i);
            }
        });
        lnviewaboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuItemActivity.this,AboutUsActivity.class));
            }
        });
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        if(preferences.getString("img_avt","").equals("null")){
            Picasso.get().load("https://zbiogg.com/img/avt/"+"avt-default.png").placeholder(R.color.bgshimmer).into(imgUserAvt);
        }else{
            Picasso.get().load("https://zbiogg.com/img/avt/"+preferences.getString("img_avt","")).placeholder(R.color.bgshimmer).into(imgUserAvt);
        }
        txtUserName.setText(preferences.getString("lastName","")+" "+preferences.getString("firstName",""));

        dialog = new ProgressDialog(MenuItemActivity.this);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItemActivity.super.onBackPressed();
            }
        });
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/logout", response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getBoolean("success")){
//                                dialog.setMessage("Đang đăng xuất");
//                                dialog.show();
//                                Handler handler = new Handler();
//                                handler.postDelayed(new Runnable() {
//                                    public void run() {
//                                        dialog.dismiss();
//
//                                    }
//                                },1000);
                            startActivity(new Intent(MenuItemActivity.this,LoginActivity.class));



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
        });
    }
}
