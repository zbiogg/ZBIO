package com.tungkon.zbio.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tungkon.zbio.Adapter.PostCmtAdapter;
import com.tungkon.zbio.Fragment.HomeFragment;
import com.tungkon.zbio.Model.Cmt;
import com.tungkon.zbio.Model.Post;
import com.tungkon.zbio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DetailPostActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText edit_cmt_content;
    TextView txtUserNamePost,txtTimePost,txtPostContent,txtLikes,txtCmts,txtUserNameToolbar;
    ImageView imgUserAvtPost,imgPost,imgUserCmtAvt;
    SharedPreferences preferences;
    ArrayList<Cmt> arrayListCmt;
    PostCmtAdapter cmtAdapter;
    ImageButton btnback,btn_cmt_comfirm;
    Button btn_like,btn_cmt;
    ImageButton btn_more;
    int postID = 0;
    int post_liked=0;
    NestedScrollView scroll_postdetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        scroll_postdetail = findViewById(R.id.scroll_detailpost);
        edit_cmt_content = findViewById(R.id.edit_cmt_content);
        txtUserNameToolbar = findViewById(R.id.txtUserNameToolbar);
        txtUserNamePost = findViewById(R.id.txtUserNamePost);
        imgUserAvtPost = findViewById(R.id.imgUserAvtPost);
        imgUserCmtAvt = findViewById(R.id.imgUserCmtAvt);
        txtTimePost = findViewById(R.id.txtTimePost);
        txtPostContent = findViewById(R.id.txtPostContent);
        imgPost = findViewById(R.id.imgPost);
        txtLikes=findViewById(R.id.txt_likes);
        txtCmts=findViewById(R.id.txt_cmts);
        btn_more=findViewById(R.id.btn_more);
        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context contextbs;
                contextbs=v.getContext();
                LayoutInflater inflater = (LayoutInflater) contextbs.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                View view = inflater.inflate (R.layout.bottom_sheet_option_post, null);
                LinearLayout ln_copy_test = (LinearLayout)view.findViewById(R.id.ln_copy_text);
                LinearLayout ln_copy_link = (LinearLayout)view.findViewById(R.id.ln_copy_link);
                LinearLayout ln_edit_post = (LinearLayout)view.findViewById(R.id.ln_edit_post);
                LinearLayout ln_delete_post = (LinearLayout)view.findViewById(R.id.ln_delete_post);

                if(preferences.getInt("id",0)!=getIntent().getExtras().getInt("post_userID")){
                    ln_edit_post.setVisibility(View.GONE);
                    ln_delete_post.setVisibility(View.GONE);
                }
                BottomSheetDialog dialog = new BottomSheetDialog(contextbs);
                dialog.setContentView(view);
                dialog.show();
                ln_copy_test.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(txtPostContent.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(), "Bài viết không có nội dung để sao chép!", Toast.LENGTH_LONG).show();
                        }else {
                            ClipboardManager _clipboard = (ClipboardManager) contextbs.getSystemService(Context.CLIPBOARD_SERVICE);
                            _clipboard.setText(txtPostContent.getText() + "");
                            Toast.makeText(getApplicationContext(), "Đã sao chép nội dung bài viết vào bộ nhớ tạm", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                ln_copy_link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager _clipboard = (ClipboardManager) contextbs.getSystemService(Context.CLIPBOARD_SERVICE);
                        _clipboard.setText("https://zbiogg.com/posts/"+getIntent().getExtras().getInt("postID"));
                        Toast.makeText(getApplicationContext(), "Đã sao chép liên kết bài viết vào bộ nhớ tạm", Toast.LENGTH_LONG).show();
                    }
                });
                ln_edit_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent i = new Intent(getApplicationContext(), EditPostActivity.class);
                        startActivity(i);
                    }
                });
                ln_delete_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        StringRequest request = new StringRequest(Request.Method.POST,"https://zbiogg.com/api/deletePost",response -> {
                            try {
                                JSONObject object = new JSONObject(response);
                                if(object.getBoolean("success")){
                                    Log.d("possssss",getIntent().getExtras().getInt("post_Position")+"");
                                    HomeFragment.arrayListposts.remove(getIntent().getExtras().getInt("post_Position"));
                                    HomeFragment.recyclerView.getAdapter().notifyItemRemoved(getIntent().getExtras().getInt("post_Position"));
                                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                                    finish();

                                    Toast.makeText(getApplicationContext(), "Đã xóa bài viết", Toast.LENGTH_LONG).show();

                                }else{
                                    Toast.makeText(getApplicationContext(), "Lỗi", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Lỗi cacth", Toast.LENGTH_LONG).show();
                            }

                        },error -> {
                            error.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Lỗi errror"+error.toString(), Toast.LENGTH_LONG).show();
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
                                map.put("postID",getIntent().getExtras().getInt("postID")+"");
                                return map;
                            }
                        };
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        queue.add(request);
                    }
                });
            }
        });
        btn_like = findViewById(R.id.btn_like);
        btn_cmt = findViewById(R.id.btn_cmt);
        btn_cmt_comfirm = findViewById(R.id.btn_cmt_comfirm);
        btnback = findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if(getIntent().getIntExtra("focus_editcmt",0)==1){
            edit_cmt_content.requestFocus();
        }
        postID =getIntent().getIntExtra("postID",0);

        recyclerView = findViewById(R.id.rc_view_cmts);
        //
        txtUserNameToolbar.setText((getIntent().getExtras().getString("post_UserName")));
        Picasso.get().load("https://zbiogg.com/img/avt/" +getIntent().getExtras().getString("post_UserAvt")).into(imgUserAvtPost);
        Picasso.get().load("https://zbiogg.com/img/avt/" +preferences.getString("img_avt","")).into(imgUserCmtAvt);
        Picasso.get().load("https://zbiogg.com/img/posts/" +getIntent().getExtras().getString("post_Image")).into(imgPost);
        txtUserNamePost.setText(getIntent().getExtras().getString("post_UserName"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(getIntent().getExtras().getString("post_CreatedAt")).getTime();
            Date today = new Date(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            Log.d("qq", String.valueOf(cal.get(Calendar.YEAR)));
            txtTimePost.setText(getTimeAgo(time));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        int cmt_qty=getIntent().getExtras().getInt("post_cmtqty");
        int like_qty=getIntent().getExtras().getInt("post_likeqty");
        if (like_qty > 0) {
            txtLikes.setText(like_qty + "");

        } else {
            txtLikes.getLayoutParams().height = 0;
            txtLikes.setLayoutParams(txtLikes.getLayoutParams());
            txtLikes.setText(null);

        }

        if (cmt_qty> 0) {
            txtCmts.setText(cmt_qty + " bình luận");
        } else {
            txtCmts.getLayoutParams().height = 0;
            txtCmts.setLayoutParams(txtCmts.getLayoutParams());
            txtCmts.setText(null);

        }
        txtPostContent.setText(getIntent().getExtras().getString("post_Content"));
        txtLikes.setText(getIntent().getExtras().getInt("post_likeqty")+"");
        txtCmts.setText(getIntent().getExtras().getInt("post_cmtqty")+ " bình luận");
        post_liked = getIntent().getIntExtra("post_liked",0);
        if(post_liked==0){
            btn_like.setTextColor(Color.parseColor("#707070"));
        }else{
            btn_like.setTextColor(Color.parseColor("#ff9900"));
            btn_like.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_care, //left
                    0, //top
                    0, //right
                    0 //bottom
            );
            btn_like.setText("Thương thương");
        }
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_like.getText().equals("Thích")){
                    btn_like.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_care, //left
                            0, //top
                            0, //right
                            0 //bottom
                    );
                    btn_like.setText("Thương thương");
                    btn_like.setTextColor(Color.parseColor("#ff9900"));
                }else{
                    btn_like.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like_dark, //left
                            0, //top
                            0, //right
                            0 //bottom
                    );
                    btn_like.setText("Thích");
                    btn_like.setTextColor(Color.parseColor("#707070"));
                }
                StringRequest request = new StringRequest(Request.Method.POST,"https://zbiogg.com/api/likes",response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        int like_qty=object.getInt("like_qty");
                        if(object.getString("message").equals("liked")){
                            txtLikes.setText((getIntent().getExtras().getInt("post_likeqty")+1)+"");
                            txtLikes.getLayoutParams().height= (int) (35*getApplicationContext().getResources().getDisplayMetrics().density);;
                            txtLikes.setLayoutParams(txtLikes.getLayoutParams());
                            txtLikes.setText(like_qty+"");
                        }else{
                            if(like_qty==0){
                                txtLikes.getLayoutParams().height = 0;
                                txtLikes.setLayoutParams(txtLikes.getLayoutParams());
                                txtLikes.setText(null);
                            }else{
                                txtLikes.setText(like_qty+"");
                            }
                        }
                        Log.d("test13213", String.valueOf(object));
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
                        map.put("postID",postID+"");
                        return map;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(request);
            }

        });
        btn_cmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_cmt_content.requestFocus();
            }
        });

        btn_cmt_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_cmt_content.toString()!=""){
                    StringRequest request = new StringRequest(Request.Method.POST,"https://zbiogg.com/api/comments",response -> {
                        try {

                            JSONObject object = new JSONObject(response);
                            Log.d("cmtnew",object+"");
                            JSONArray newcmt = object.getJSONArray("cmt");
                            Cmt cmt = new Gson().fromJson(newcmt.get(0).toString(),Cmt.class);
                            Log.d("kaka123",arrayListCmt+"");
                            arrayListCmt.add(cmt);
                            cmtAdapter.notifyItemInserted(cmtAdapter.getItemCount()-1);
                            cmtAdapter.notifyDataSetChanged();
                            edit_cmt_content.setText("");
                            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                                    .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                            scroll_postdetail.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    scroll_postdetail.fullScroll(View.FOCUS_DOWN);
                                }
                            },500);
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
                            return  map;
                        }

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> map= new HashMap<>();
                            map.put("postID",postID+"");
                            map.put("content_cmt",edit_cmt_content.getText()+"");
                            return  map;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(request);
                }
            }
        });
        GetCmt();

    }
    private void GetCmt(){
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/postcmts?postID="+postID,response -> {
            try {
                JSONObject object = new JSONObject(response);
                JSONArray cmts = object.getJSONArray("cmts");
                Log.d("test1", String.valueOf(cmts));
                arrayListCmt = new ArrayList<>();
                for(int i=0;i<cmts.length();i++){
                    JSONObject p = cmts.getJSONObject(i);
                    Cmt cmt = new Gson().fromJson(cmts.get(i).toString(), Cmt.class);
                    arrayListCmt.add(cmt);
                }
                LinearLayoutManager layoutmanager = new LinearLayoutManager(getApplication());
                layoutmanager.setStackFromEnd(true);
//                layoutmanager.setReverseLayout(true);
                recyclerView.setLayoutManager(layoutmanager);
                cmtAdapter=new PostCmtAdapter(getApplicationContext(),arrayListCmt);
                recyclerView.setAdapter(cmtAdapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);
                cmtAdapter.notifyDataSetChanged();

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


    //time ago
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getTimeAgo(long time) {
        Date timepost = new Date(time);
        Calendar cal = Calendar.getInstance(); cal.setTime(timepost);
        String min = String.valueOf(cal.get(Calendar.MINUTE));
        if(cal.get(Calendar.MINUTE)<10){
            min="0"+cal.get(Calendar.MINUTE);
        }
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = currentDate().getTime();
        if (time > now || time <= 0) {
            return "Vừa xong";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Vừa xong";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1 phút trước";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " phút trước";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 giờ trước";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " giờ trước";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Hôm qua lúc "+cal.get(Calendar.HOUR)+":"+min;
        } else {
            return
                    cal.get(Calendar.DATE)
                            +" thg "+(int)(cal.get(Calendar.MONTH)+1)
                            + " lúc "+cal.get(Calendar.HOUR)+":"
                            +min;
        }
    }

}
