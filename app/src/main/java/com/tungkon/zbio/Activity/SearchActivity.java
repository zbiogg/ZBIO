package com.tungkon.zbio.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.google.gson.Gson;
import com.tungkon.zbio.Adapter.SearchUserAdapter;
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
    SearchUserAdapter searchUserAdapter;
    RecyclerView rc_user;
    LinearLayout ln_no_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        txt_option_user_search =findViewById(R.id.txt_option_user_search);
        rc_user = findViewById(R.id.rc_view_search_user);
        ln_no_history = findViewById(R.id.ln_no_history);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rc_user.setLayoutManager(layoutManager);
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
                        Search();
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
        String key_search = edit_key_search.getText().toString();
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/search?key_search="+key_search,response -> {
            try {

                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray users = object.getJSONArray("search_users");
                    if(users.length()!=0) {
                        txt_option_user_search.setText("Mọi người");
                        txt_option_user_search.setTypeface(txt_option_user_search.getTypeface(), Typeface.BOLD  );
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
                        txt_option_user_search.setTypeface(ResourcesCompat.getFont(getApplicationContext(),R.font.lora));
                        txt_option_user_search.setTypeface(txt_option_user_search.getTypeface(),Typeface.NORMAL);
                        txt_option_user_search.setText("Không tìm thấy kết quả phù hợp!");

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
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization", "Bearer "+token);
                return map;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

}
