package com.tungkon.zbio.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tungkon.zbio.Activity.CreatePost;
import com.tungkon.zbio.Activity.MenuItemActivity;
import com.tungkon.zbio.Adapter.PostsAdapter;
import com.tungkon.zbio.Model.Post;
import com.tungkon.zbio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public static ImageView imgUserAvt;
    Button btn_pick_camera, btn_add_image;
    public static ShimmerFrameLayout shimmerFrameLayout;
    public static RecyclerView recyclerView;
     public  static ArrayList<Post> arrayListposts;
    PostsAdapter postsAdapter;
    SharedPreferences preferences;
    public static NestedScrollView scroll_home;
    public static LinearLayout lnloadingnewpost,lncreatepost,lnloadmore;
    private SwipeRefreshLayout swiperf_home;
    boolean isLoading =false;
    int page =2;
    static  boolean loadmore=true;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.rc_view_posts);
        imgUserAvt = view.findViewById(R.id.imgUserAvtCreatePost);
        lnloadmore =view.findViewById(R.id.ln_load_more_spinner);
        lnloadmore.setVisibility(View.GONE);
        scroll_home = view.findViewById(R.id.scroll_home);
        scroll_home.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                if (!scroll_home.canScrollVertically(1)) {
                    lnloadmore.setVisibility(View.VISIBLE);
                    getLoadMore();
                    Toast.makeText(getContext(),"trang so: "+page,Toast.LENGTH_SHORT).show();
                }
                if (!scroll_home.canScrollVertically(-1)) {
                    // top of scroll view


                }
            }
        });
        preferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_add_image = view.findViewById(R.id.btn_add_image);
        btn_pick_camera = view.findViewById(R.id.btn_pick_camera);
        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, 2);
            }
        });
        btn_pick_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent opencamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(opencamera,1);
            }
        });
        swiperf_home = view.findViewById(R.id.swpie_home_layout);
        swiperf_home.setColorSchemeColors(Color.parseColor("#6fbe44"));
        swiperf_home.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 500ms
                        swiperf_home.setRefreshing(false);
                        shimmerFrameLayout.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.startShimmer();
                        GetData1();
                    }
                }, 500);


                Log.d("refesh","Done");


            }
        });

        if(preferences.getString("img_avt","").equals("null")){
            Picasso.get().load("https://zbiogg.com/img/avt/"+"avt-default.png").into(imgUserAvt);
        }else{
            Picasso.get().load("https://zbiogg.com/img/avt/"+preferences.getString("img_avt","")).into(imgUserAvt);
        }
        LinearLayoutManager layoutmanager = new LinearLayoutManager(getActivity());
//        layoutmanager.setStackFromEnd(true);
//        layoutmanager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutmanager);
        recyclerView.setNestedScrollingEnabled(true);
        shimmerFrameLayout=view.findViewById(R.id.shimmer_view_container);
        lnloadingnewpost = view.findViewById(R.id.lnloadingpost);
        lncreatepost = view.findViewById(R.id.lncreatepost);
        lncreatepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreatePost.class));
            }
        });
        onResume();
        GetData1();
        return view;

    }
    private void GetData1(){
        arrayListposts = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/posts", response -> {
            try {
                JSONObject object = new JSONObject(response);
                    JSONArray posts = object.getJSONArray("data");
                    Log.d("test1", String.valueOf(posts));
                    for(int i=0;i<posts.length();i++){
                        JSONObject p = posts.getJSONObject(i);
                        Post post = new Gson().fromJson(posts.get(i).toString(), Post.class);
                        arrayListposts.add(post);
                    }
                    postsAdapter=new PostsAdapter(getContext(),arrayListposts);
                    recyclerView.setAdapter(postsAdapter);
                    postsAdapter.notifyDataSetChanged();
                    page=2;



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
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
    public void getLoadMore(){
        StringRequest requestloadmore = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/posts?page="+page,response -> {
            try {
                JSONObject objectloadmore = new JSONObject(response);
                JSONArray posts = objectloadmore.getJSONArray("data");
                Log.d("test1", String.valueOf(posts));
                for(int i=0;i<posts.length();i++){
                    JSONObject p = posts.getJSONObject(i);
                    Post post = new Gson().fromJson(posts.get(i).toString(), Post.class);
                    arrayListposts.add(post);
                }
                if(posts.length()!=0){
                postsAdapter.notifyItemRangeInserted(arrayListposts.size(),posts.length());
                    page++;
                }else{
                    lnloadmore.setVisibility(View.GONE);
                    Toast.makeText(getContext(),"Bạn đã xem hết bài viết!",Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        },error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                String token=preferences.getString("token","");
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };
        RequestQueue queue1 = Volley.newRequestQueue(getContext());
        queue1.add(requestloadmore);
    }


    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        Log.d("load xonh","oke");
    }
    public void NewPost(){
        Log.d("test", "oke");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode==2 && resultCode==Activity.RESULT_OK){
                Uri imgUri = data.getData();
                Intent i = new Intent(getContext(), CreatePost.class);
                i.setData(imgUri);
                startActivity(i);
            }else if(requestCode==1 && resultCode==Activity.RESULT_OK){
                Uri imgUri = data.getData();
                Log.d("datauri",imgUri+"");
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Intent i = new Intent(getContext(), CreatePost.class);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                byte [] array = byteArrayOutputStream.toByteArray();
                i.putExtra("image_camera_data",array);
                i.putExtra("picked_camera",true);
                startActivity(i);
            }


    }
}
