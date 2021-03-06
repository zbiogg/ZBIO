package com.tungkon.zbio.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.tungkon.zbio.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button mBtnLogin,mBtnRegister;
    EditText editusername,editpassword;
    private ProgressDialog dialog;
    AlertDialog alertDialog;
    SharedPreferences preferences;
    TextInputLayout usernameinfo,passwordinfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setCancelable(false);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_register);
        editusername = findViewById(R.id.edit_login_username);
        editpassword = findViewById(R.id.edit_login_password);
        if(getIntent().getExtras()!=null){
            editusername.setText(getIntent().getExtras().getString("username"));
            dialog.setMessage("Đăng kí thành công, vui lòng đăng nhập...");
            dialog.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 1000);
        }
        Typeface font_lora= ResourcesCompat.getFont(getApplicationContext(), R.font.lora);
        usernameinfo= (TextInputLayout) findViewById(R.id.usernameinfo);
        usernameinfo.setTypeface(font_lora);
        editusername.setTypeface(font_lora);
        passwordinfo = (TextInputLayout) findViewById(R.id.passwordinfo);
        passwordinfo.setTypeface(font_lora);
        editpassword.setTypeface(font_lora);

//        dialog.setMessage("Vui lòng đăng nhập để sử dụng");
//        dialog.show();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                dialog.dismiss();
//
//            }
//        }, 1000);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
        preferences =getSharedPreferences("user", Context.MODE_PRIVATE);
            mBtnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.setMessage("Đang đăng nhập...");
                    dialog.show();
                    StringRequest request = new StringRequest(Request.Method.POST,"https://zbiogg.com/api/login",response -> {
                        try {
                            JSONObject object = new JSONObject(response);
                            if(object.getBoolean("success")){
                                JSONObject user = object.getJSONObject("user");
                                SharedPreferences userPref = getSharedPreferences("user",MODE_PRIVATE);
                                SharedPreferences.Editor editor = userPref.edit();

                                editor.putString("token",object.getString("token"));
                                editor.putInt("id",user.getInt("id"));
                                editor.putString("firstName",user.getString("firstName"));
                                editor.putString("lastName",user.getString("lastName"));
                                editor.putString("img_avt",user.getString("img_avt"));
                                editor.putString("img_cover",user.getString("img_cover"));
                                editor.putString("gender",user.getString("gender"));
                                editor.putString("doB",user.getString("doB"));
                                editor.putString("city",user.getString("city"));
                                editor.putString("email",user.getString("email"));
                                editor.putString("phone",user.getString("phone"));
                                Log.d("token",object.getString("token"));
                                editor.apply();
                                startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("aaa", "sai tên đn or mk");
                            Toast.makeText(LoginActivity.this,"Tên đăng nhập hoặc mật khẩu không chính xác!",Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    },error -> {
                        error.printStackTrace();
                        dialog.dismiss();

                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String,String> map = new HashMap<>();
                            map.put("username",editusername.getText().toString().trim());
                            map.put("email",editusername.getText().toString().trim());
                            map.put("password",editpassword.getText().toString());
                            return map;

                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(request);

                }
            });

    }

}
