package com.tungkon.zbio.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tungkon.zbio.Adapter.UserPostAdapter;
import com.tungkon.zbio.Model.Post;
import com.tungkon.zbio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyProfileActivity extends AppCompatActivity {
    private ImageView imgAvt,imgCover;
    private TextView txtFullName,txtgender,txtdob,txtcity,txtemail,txtphone,txtPostQty,txtLikeQty,txtCmtQty;
    private ArrayList<Post> arrayListUserPost;
    private SharedPreferences preferences;
    private UserPostAdapter userPostAdapter;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout lncity;
    private SwipeRefreshLayout swiperf_profile;
    private RelativeLayout layoutprofile;
    private ImageButton btnback,btn_search_toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        btnback = findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_search_toolbar = findViewById(R.id.btn_search_toolbar);
        btn_search_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SearchActivity.class));
            }
        });
        imgAvt = findViewById(R.id.imgUserAvt);
        imgCover = findViewById(R.id.imgUserCover);
        txtFullName = findViewById(R.id.txtUserName);
        recyclerView = findViewById(R.id.rc_view_myposts);
        txtPostQty= findViewById(R.id.txtpostqty);
        txtLikeQty= findViewById(R.id.txtlikeqty);
        txtCmtQty= findViewById(R.id.txtcmtqty);
        txtgender = findViewById(R.id.txtgender);
        txtdob = findViewById(R.id.txtdob);
        txtcity = findViewById(R.id.txtcity);
        txtemail = findViewById(R.id.txtemail);
        txtphone = findViewById(R.id.txtphone);
        lncity = findViewById(R.id.lncity);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_profile);
        layoutprofile = findViewById(R.id.layoutprofle);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        if(savedInstanceState!=null){
            Log.d("sfdsfsd","retrerfesd");
        }else{
            Log.d("sfdsfsd","ádsadsa");
            shimmerFrameLayout.startShimmer();
            getData();
        }
        swiperf_profile = findViewById(R.id.swpie_profile_layout);
        swiperf_profile.setColorSchemeColors(Color.parseColor("#6fbe44"));
        swiperf_profile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 500ms
                        swiperf_profile.setRefreshing(false);
                        shimmerFrameLayout.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.startShimmer();
                        getData();
                    }
                }, 500);


                Log.d("refesh","Done");


            }
        });


    }
    private void getData(){
        arrayListUserPost = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/myposts", response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray posts = object.getJSONArray("data");
                    Log.d("test123", String.valueOf(posts));
                    int likeqty = 0;
                    int cmtqty = 0;
                    for(int i=0;i<posts.length();i++){
                        JSONObject p = posts.getJSONObject(i);
                        Post post = new Gson().fromJson(posts.get(i).toString(), Post.class);
                        arrayListUserPost.add(post);
                        likeqty+=(arrayListUserPost.get(i).getLikeQty());
                        cmtqty+=(arrayListUserPost.get(i).getCmtQty());

                    }
                    txtPostQty.setText(arrayListUserPost.size()+"");
                    txtLikeQty.setText(likeqty+"");
                    txtCmtQty.setText(cmtqty+"");
                    if(preferences.getString("img_avt","").equals("null")){
                        Picasso.get().load("https://zbiogg.com/img/avt/"+"avt-default.png").placeholder(R.color.bgshimmer).into(imgAvt);
                    }else{
                        Picasso.get().load("https://zbiogg.com/img/avt/"+preferences.getString("img_avt","")).placeholder(R.color.bgshimmer).into(imgAvt);
                    }
                    if(preferences.getString("img_cover","").equals("null")){
                        Picasso.get().load("https://zbiogg.com/img/cover/"+"cover-default.png").placeholder(R.color.bgshimmer).into(imgCover);
                    }else{
                        Picasso.get().load("https://zbiogg.com/img/cover/"+preferences.getString("img_cover","")).placeholder(R.color.bgshimmer).into(imgCover);
                    }
                    txtFullName.setText(preferences.getString("lastName","")+" "+preferences.getString("firstName",""));

                    txtgender.setText(preferences.getString("gender",""));
                    txtdob.setText(preferences.getString("doB",""));
                    if(preferences.getString("city","").equals("null")){
                        lncity.setVisibility(View.GONE);
                    }else{
                        txtcity.setText(preferences.getString("city",""));
                    }

                    txtemail.setText(preferences.getString("email",""));
                    txtphone.setText(preferences.getString("phone",""));
                    shimmerFrameLayout.stopShimmer();
                    layoutprofile.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.setVisibility(View.GONE);
                    userPostAdapter=new UserPostAdapter(getApplicationContext(),arrayListUserPost);
                    recyclerView.setAdapter(userPostAdapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setNestedScrollingEnabled(false);
                    userPostAdapter.notifyDataSetChanged();
                    Log.d("token1", preferences.getString("token",""));
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
