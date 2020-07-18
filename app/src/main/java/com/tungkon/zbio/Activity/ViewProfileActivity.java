package com.tungkon.zbio.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class ViewProfileActivity extends AppCompatActivity {
     ImageView imgAvt,imgCover;
    private TextView txtUserName,txtFullName,txtgender,txtdob,txtcity,txtemail,txtphone,txtPostQty,txtLikeQty,txtCmtQty;
    private ArrayList<Post> arrayListUserPost1;
    private SharedPreferences preferences;
    private UserPostAdapter userPostAdapter;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerFrameLayout1;
    private LinearLayout lncity;
    private ImageButton btn_menu;
    private RelativeLayout layoutprofile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        imgAvt = findViewById(R.id.imgUserAvt);
        imgCover = findViewById(R.id.imgUserCover);
        txtFullName = findViewById(R.id.txtUserName);
        txtUserName = findViewById(R.id.txtUserNameToolbar);
        recyclerView = findViewById(R.id.rc_view_myposts);
        txtPostQty=findViewById(R.id.txtpostqty);
        txtLikeQty=findViewById(R.id.txtlikeqty);
        txtCmtQty=findViewById(R.id.txtcmtqty);
        txtgender = findViewById(R.id.txtgender);
        txtdob = findViewById(R.id.txtdob);
        txtcity = findViewById(R.id.txtcity);
        txtemail =findViewById(R.id.txtemail);
        txtphone =findViewById(R.id.txtphone);
        lncity = findViewById(R.id.lncity);
        btn_menu =findViewById(R.id.btn_menu_item);
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        shimmerFrameLayout1 = findViewById(R.id.shimmer_view_profile);
        layoutprofile = findViewById(R.id.layoutprofle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        shimmerFrameLayout1.startShimmer();
        getData();
    }
    private void getData(){
        Log.d("test123", "aasdasd");
        arrayListUserPost1 = new ArrayList<>();
        int userID = getIntent().getIntExtra("userID",0);
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/userprofile/"+userID, response -> {
            try {

                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONObject user = object.getJSONObject("user");
                    JSONArray posts = object.getJSONArray("data");
                    Log.d("test123", String.valueOf(posts));
                    int likeqty = 0;
                    int cmtqty = 0;
                    for(int i=0;i<posts.length();i++){
                        JSONObject p = posts.getJSONObject(i);
                        Post post = new Gson().fromJson(posts.get(i).toString(), Post.class);
                        arrayListUserPost1.add(post);
                        likeqty+=(arrayListUserPost1.get(i).getLikeQty());
                        cmtqty+=(arrayListUserPost1.get(i).getCmtQty());

                    }
                    txtPostQty.setText(arrayListUserPost1.size()+"");
                    txtLikeQty.setText(likeqty+"");
                    txtCmtQty.setText(cmtqty+"");
                    if(user.getString("img_avt").equals("null")){
                        Picasso.get().load("https://zbiogg.com/img/avt/"+"avt-default.png").placeholder(R.color.bgshimmer).into(imgAvt);
                    }else{
                        Picasso.get().load("https://zbiogg.com/img/avt/"+user.getString("img_avt")).placeholder(R.color.bgshimmer).into(imgAvt);
                    }
                    if(user.getString("img_cover").equals("null")){
                        Picasso.get().load("https://zbiogg.com/img/cover/"+"cover-default.png").placeholder(R.color.bgshimmer).into(imgCover);
                    }else{
                        Picasso.get().load("https://zbiogg.com/img/cover/"+user.getString("img_cover")).placeholder(R.color.bgshimmer).into(imgCover);
                    }
                    txtFullName.setText(user.getString("lastName")+" "+user.getString("firstName"));

                    txtgender.setText(user.getString("gender"));
                    txtdob.setText(user.getString("doB"));
                    if(user.getString("city").equals("null")){
                        lncity.setVisibility(View.GONE);
                    }else{
                        txtcity.setText(user.getString("city"));
                    }

                    txtemail.setText(user.getString("email"));
                    txtphone.setText(user.getString("phone"));
                    shimmerFrameLayout1.stopShimmer();
                    layoutprofile.setVisibility(View.VISIBLE);
                    shimmerFrameLayout1.setVisibility(View.GONE);
                    userPostAdapter=new UserPostAdapter(getApplicationContext(),arrayListUserPost1);
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
