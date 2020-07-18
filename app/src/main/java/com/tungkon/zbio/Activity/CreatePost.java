package com.tungkon.zbio.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tungkon.zbio.Fragment.HomeFragment;
import com.tungkon.zbio.Model.Post;
import com.tungkon.zbio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreatePost extends AppCompatActivity implements View.OnClickListener {
    private ImageButton  btn_cancelpost, btn_pick_image, btn_open_camera;
    private Button btn_post_confirm;
    private EditText edit_post_content;
    private ImageView img_post_Image,imgUserAvt;
    private TextView txtUserName;
    private int IMAGE_PICK_CODE = 3;
    private int CAMERA_PICK_CODE = 4;
    private static final int GALLERY_CHANGE_POST = 2;
    private Bitmap bitmap = null;
    private SharedPreferences preferences,preferences_back2step;
    HomeFragment homeFragment = new HomeFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        btn_cancelpost = findViewById(R.id.btnback);
        btn_open_camera = findViewById(R.id.btn_camera);
        btn_pick_image = findViewById(R.id.btn_image);
        img_post_Image = findViewById(R.id.img_post_image);
        btn_post_confirm = findViewById(R.id.btn_post_confirm);
        edit_post_content = findViewById(R.id.edit_post_content);
        edit_post_content.requestFocus();
        imgUserAvt = findViewById(R.id.imgUserAvt);
        txtUserName = findViewById(R.id.txtUserName);
        btn_post_confirm.setOnClickListener(this);
        btn_pick_image.setOnClickListener(this);
        btn_open_camera.setOnClickListener(this);
        btn_cancelpost.setOnClickListener(this);
        img_post_Image.setImageURI(getIntent().getData());
        if(getIntent().getData()!=null){
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
                if(!bitmapToString(bitmap).isEmpty()){
                    btn_post_confirm.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selector_normal));
                    btn_post_confirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    btn_post_confirm.setClickable(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("testfff","nônnon");
            }
        }{
            if(getIntent().getBooleanExtra("picked_camera",false)){
                byte[] byteArray = getIntent().getByteArrayExtra("image_camera_data");
                bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                img_post_Image.setImageBitmap(bitmap);
                btn_post_confirm.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selector_normal));
                btn_post_confirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                btn_post_confirm.setClickable(true);
            }
        }


        edit_post_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length()>0 ||!bitmapToString(bitmap).isEmpty()) {
                    btn_post_confirm.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selector_normal));
                    btn_post_confirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    btn_post_confirm.setClickable(true);
                }else{
                    btn_post_confirm.setBackground(null);
                    btn_post_confirm.setTextColor(Color.rgb(197,187,187));
                    btn_post_confirm.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        if(preferences.getString("img_avt","").equals("null")){
            Picasso.get().load("https://zbiogg.com/img/avt/"+"avt-default.png").placeholder(R.color.bgshimmer).into(imgUserAvt);
        }else{
            Picasso.get().load("https://zbiogg.com/img/avt/"+preferences.getString("img_avt","")).placeholder(R.color.bgshimmer).into(imgUserAvt);
        }
        txtUserName.setText(preferences.getString("lastName","")+" "+preferences.getString("firstName",""));

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnback:
                super.onBackPressed();
                break;
            case R.id.btn_image:
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, 3);
                break;
            case R.id.btn_post_confirm:
                if(!edit_post_content.getText().toString().isEmpty()||!bitmapToString(bitmap).isEmpty()){
                    ConfimPost();
                }
                break;
            case R.id.btn_camera:
                Intent opencamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(opencamera,4);
                break;
        }
    }
    private void ConfimPost(){
        finish();
        HomeFragment.lnloadingnewpost.setVisibility(View.VISIBLE);
        HomeFragment.scroll_home.postDelayed(new Runnable() {
            @Override
            public void run() {
                HomeFragment.scroll_home.fullScroll(View.FOCUS_UP);
                StringRequest request = new StringRequest(Request.Method.POST,"https://zbiogg.com/api/posts",response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        Log.d("tob1", String.valueOf(object));
                        if(object.getBoolean("success")){
                            JSONArray postObject = object.getJSONArray("data");
                            Log.d("tob", String.valueOf(postObject));
                            Post post = new Gson().fromJson(postObject.get(0).toString(), Post.class);
                            HomeFragment.arrayListposts.add(post);
                            HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                            HomeFragment.recyclerView.scrollToPosition(HomeFragment.arrayListposts.size()-1);
                            HomeFragment.lnloadingnewpost.setVisibility(View.GONE);
                        }else{
                            Log.d("tob", String.valueOf(object));
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

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map= new HashMap<>();
                        map.put("post_Content",edit_post_content.getText().toString().trim());
                        map.put("post_Image",bitmapToString(bitmap));
                        return map;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                RequestQueue queue = Volley.newRequestQueue(CreatePost.this);
                queue.add(request);
            }
        },500);
    }

    private String bitmapToString(Bitmap bitmap){
        if(bitmap!=null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte [] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array,Base64.DEFAULT);
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3 && resultCode== RESULT_OK){
            Uri imgUri = data.getData();
            img_post_Image.setImageURI(imgUri);
            Log.d("qqqqq","deo hieu");
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imgUri);
                if(!bitmapToString(bitmap).isEmpty()){
                    btn_post_confirm.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selector_normal));
                    btn_post_confirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    btn_post_confirm.setClickable(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(requestCode==4 && resultCode==RESULT_OK){
            Log.d("qqqqq","deo hieu");
                bitmap = (Bitmap) data.getExtras().get("data");
                if(!bitmapToString(bitmap).isEmpty()){
                    img_post_Image.setImageBitmap(bitmap);
                    btn_post_confirm.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selector_normal));
                    btn_post_confirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    btn_post_confirm.setClickable(true);
                }

        }
    }
}
