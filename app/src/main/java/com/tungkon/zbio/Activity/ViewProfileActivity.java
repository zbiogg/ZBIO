package com.tungkon.zbio.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
    private TextView txtUserName,txtFullName,txtgender,txtdob,txtcity,txtemail,txtphone,txtPostQty,txtLikeQty,txtCmtQty,txtUserNameToolbar;
    private ArrayList<Post> arrayListUserPost1;
    private SharedPreferences preferences;
    private UserPostAdapter userPostAdapter;
    private RecyclerView recyclerView;
    private LinearLayout lncity;
    private ImageButton btnback,btn_search_toolbar;
    private RelativeLayout layoutprofile;
    private ShimmerFrameLayout shimmerFrameLayout;
    private SwipeRefreshLayout swiperf_profile;
    private CardView btn_go_to_message,btn_add_friend,btn_option_friend,btn_more,btn_comfirm_friend,btn_add_success_friend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
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
        shimmerFrameLayout =findViewById(R.id.shimmer_view_profile);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        imgAvt = findViewById(R.id.imgUserAvt);
        imgCover = findViewById(R.id.imgUserCover);
        txtUserNameToolbar = findViewById(R.id.txtUserNameToolbar);
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
        btn_go_to_message = findViewById(R.id.btn_go_to_message);
        btn_add_friend = findViewById(R.id.btn_add_friend);
        btn_option_friend = findViewById(R.id.btn_option_friend);
        btn_more = findViewById(R.id.btn_more);
        btn_comfirm_friend = findViewById(R.id.btn_comfirm_friend);
        btn_add_success_friend = findViewById(R.id.btn_add_success_friend);
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        layoutprofile = findViewById(R.id.layoutprofle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        getData();
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
        Log.d("test123", "aasdasd");
        arrayListUserPost1 = new ArrayList<>();
        int userID = getIntent().getIntExtra("userID",0);
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/userprofile/"+userID, response -> {
            try {

                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray userarray = object.getJSONArray("user");
                    JSONObject user = userarray.getJSONObject(0);
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
                    txtUserNameToolbar.setText(user.getString("lastName")+" "+user.getString("firstName"));
                    switch (user.getInt("status_friend")){
                        case 0:
                            if(user.getInt("action_userID")==user.getInt("id")){
                                btn_comfirm_friend.setVisibility(View.VISIBLE);
                                btn_add_friend.setVisibility(View.GONE);
                            }else{
                                btn_add_success_friend.setVisibility(View.VISIBLE);
                                btn_add_friend.setVisibility(View.GONE);
                            }
                            break;
                        case 1:
                            btn_add_friend.setVisibility(View.GONE);
                            btn_option_friend.setVisibility(View.VISIBLE);
                            btn_go_to_message.setVisibility(View.VISIBLE);
                            break;
                    }

                    btn_go_to_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(),"hihi",Toast.LENGTH_SHORT).show();
                        }
                    });
                    btn_more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Context contextbs;
                            contextbs=v.getContext();
                            LayoutInflater inflater = (LayoutInflater) contextbs.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                            View view = inflater.inflate (R.layout.bottom_sheet_more_profile, null);
                            LinearLayout ln_copy_link_profile = (LinearLayout)view.findViewById(R.id.ln_copy_link_profile);
                            LinearLayout ln_report_user = (LinearLayout)view.findViewById(R.id.ln_report_user);
                            LinearLayout ln_block_user = (LinearLayout)view.findViewById(R.id.ln_block_user);
                            BottomSheetDialog dialog = new BottomSheetDialog(contextbs);
                            dialog.setContentView(view);
                            dialog.show();
                            ln_copy_link_profile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ClipboardManager _clipboard = (ClipboardManager) contextbs.getSystemService(Context.CLIPBOARD_SERVICE);
                                    _clipboard.setText("https://zbiogg.com/profile?id="+userID);
                                    Toast.makeText(getApplicationContext(), "Đã sao chép liên kết bài viết vào bộ nhớ tạm", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
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
                    layoutprofile.setVisibility(View.VISIBLE);
                    userPostAdapter=new UserPostAdapter(getApplicationContext(),arrayListUserPost1);
                    recyclerView.setAdapter(userPostAdapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setNestedScrollingEnabled(false);
                    userPostAdapter.notifyDataSetChanged();
                    Log.d("token1", preferences.getString("token",""));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
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
