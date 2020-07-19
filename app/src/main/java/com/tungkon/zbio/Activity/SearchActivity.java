package com.tungkon.zbio.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.tungkon.zbio.Adapter.PostsAdapter;
import com.tungkon.zbio.Adapter.SearchUserAdapter;
import com.tungkon.zbio.Model.Post;
import com.tungkon.zbio.Model.User;
import com.tungkon.zbio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity{
    ImageButton btnback;
    EditText edit_key_search;
    TextView txt_option_user_search;
    SharedPreferences preferences;
    ArrayList<User> arrayListUser;
    ArrayList<Post> arrayListPost;
    PostsAdapter postsAdapter;
    SearchUserAdapter searchUserAdapter;
    RecyclerView rc_user,rc_post;
    LinearLayout ln_no_history,ln_search_no_result;
    CardView cardView_search_user,cardView_search_post;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        txt_option_user_search =findViewById(R.id.txt_option_user_search);
        rc_user = findViewById(R.id.rc_view_search_user);
        rc_post = findViewById(R.id.rc_view_search_post);
        ln_no_history = findViewById(R.id.ln_no_history);
        ln_search_no_result = findViewById(R.id.ln_search_no_result);
        cardView_search_post = findViewById(R.id.cardview_search_post);
        cardView_search_user = findViewById(R.id.cardview_search_user);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_search);
        shimmerFrameLayout.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        rc_user.setLayoutManager(layoutManager);
        rc_post.setLayoutManager(layoutManager1);
        preferences = getSharedPreferences("user",MODE_PRIVATE);
        edit_key_search = findViewById(R.id.edit_key_search);
        edit_key_search.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        edit_key_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(edit_key_search.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Vui lòng nhập từ khóa tìm kiếm ", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Đang tìm kiếm cho từ khóa : " + edit_key_search.getText(), Toast.LENGTH_LONG).show();
                        shimmerFrameLayout.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.startShimmer();
                        ln_search_no_result.setVisibility(View.GONE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edit_key_search.getWindowToken(), 0);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Search();
                            }
                        }, 500);

                    }
                    return true;
                }
                return false;
            }
        });
        btnback = findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    public void Search(){
        arrayListUser = new ArrayList<>();
        arrayListPost = new ArrayList<>();
        String key_search = edit_key_search.getText().toString();
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/search?key_search="+key_search,response -> {
            try {
                edit_key_search.clearFocus();
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray users = object.getJSONArray("search_users");
                    JSONArray posts = object.getJSONArray("search_posts");
                    if(users.length()!=0) {
                        cardView_search_user.setVisibility(View.VISIBLE);
                        for (int i = 0; i < users.length(); i++) {
                            User user = new Gson().fromJson(users.get(i).toString(), User.class);
                            arrayListUser.add(user);

                        }
                        ln_no_history.setVisibility(View.GONE);
                        searchUserAdapter = new SearchUserAdapter(getApplicationContext(), arrayListUser);
                        rc_user.setAdapter(searchUserAdapter);
                        searchUserAdapter.notifyDataSetChanged();
                    }else{
                        arrayListUser.clear();
                        searchUserAdapter = new SearchUserAdapter(getApplicationContext(), arrayListUser);
                        rc_user.setAdapter(searchUserAdapter);
                        searchUserAdapter.notifyDataSetChanged();
                        ln_no_history.setVisibility(View.GONE);
                        cardView_search_user.setVisibility(View.GONE);

                    }

                    if(posts.length()!=0) {
                        cardView_search_post.setVisibility(View.VISIBLE);
                        for (int i = 0; i < posts.length(); i++) {
                            Post post = new Gson().fromJson(posts.get(i).toString(), Post.class);
                            arrayListPost.add(post);

                        }
                        ln_no_history.setVisibility(View.GONE);
                        postsAdapter = new PostsAdapter(getApplicationContext(),arrayListPost);
                        rc_post.setAdapter(postsAdapter);
                        postsAdapter.notifyDataSetChanged();
                    }else{
                        cardView_search_post.setVisibility(View.GONE);
                        arrayListPost.clear();
                        postsAdapter = new PostsAdapter(getApplicationContext(), arrayListPost);
                        rc_post.setAdapter(postsAdapter);
                        postsAdapter.notifyDataSetChanged();
                        ln_no_history.setVisibility(View.GONE);

                    }
                    if(posts.length()==0&&users.length()==0){
                        ln_search_no_result.setVisibility(View.VISIBLE);
                    }
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
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization", "Bearer "+token);
                return map;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

}
