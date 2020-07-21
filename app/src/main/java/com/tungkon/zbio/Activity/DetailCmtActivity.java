package com.tungkon.zbio.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tungkon.zbio.Adapter.ReplyCmtAdapter;
import com.tungkon.zbio.Model.ReplyCmt;
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

public class DetailCmtActivity extends AppCompatActivity {
    ImageView imgUserCmtAvt,imgUserCurrent;
    TextView txt_user_cmt_fullname,txt_cmt_content,txt_time_cmt,txt_reply_cmt;
    String cmtID,cmt_UserAvt,cmt_UserName,cmt_CreatedAt,cmt_Content;
    ImageButton btnback;
    SharedPreferences preferences;
    RecyclerView recyclerView;
    ArrayList<ReplyCmt> replyCmtArrayList;
    ReplyCmtAdapter replyCmtAdapter;
    public  static EditText editText_rep_cmt;
    public  static InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cmt);
        preferences = getSharedPreferences("user", MODE_PRIVATE);
        imgUserCmtAvt= findViewById(R.id.imgUserAvt);
        imgUserCurrent = findViewById(R.id.imgUserCurrent);
        Picasso.get().load("https://zbiogg.com/img/avt/"+preferences.getString("img_avt","")).into(imgUserCurrent);
        txt_user_cmt_fullname = findViewById(R.id.txtUserName);
        txt_cmt_content = findViewById(R.id.txtCmtContent);
        txt_time_cmt = findViewById(R.id.txtTimeCmt);
        recyclerView = findViewById(R.id.rc_view_repcmts);
        editText_rep_cmt= findViewById(R.id.edit_repcmt_content);
        editText_rep_cmt.requestFocus();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        txt_reply_cmt = findViewById(R.id.txt_reply_cmt);
        txt_reply_cmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FocusEdit();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        btnback = findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cmtID=getIntent().getExtras().getInt("cmtID")+"";
        cmt_UserAvt=getIntent().getExtras().getString("cmt_UserAvt");
        cmt_UserName=getIntent().getExtras().getString("cmt_UserName");
        cmt_CreatedAt=getIntent().getExtras().getString("cmt_CreatedAt");
        cmt_Content=getIntent().getExtras().getString("cmt_Content");
        Log.d("zzzzzz",cmtID+"==="+cmt_UserAvt);
        Picasso.get().load("https://zbiogg.com/img/avt/"+cmt_UserAvt).into(imgUserCmtAvt);
        txt_user_cmt_fullname.setText(cmt_UserName);
        txt_cmt_content.setText(cmt_Content);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(cmt_CreatedAt).getTime();
            Date today = new Date(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            Log.d("qq", String.valueOf(cal.get(Calendar.YEAR)));
            txt_time_cmt.setText(getTimeAgo(time));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        getReplyCmt();

    }
    public void getReplyCmt(){
        replyCmtArrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/replycomments?comment_ID="+cmtID,response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray repcmts = object.getJSONArray("replycmts");
                    for(int i=0;i<repcmts.length();i++){
                        ReplyCmt replyCmt = new Gson().fromJson(repcmts.get(i).toString(),ReplyCmt.class);
                        replyCmtArrayList.add(replyCmt);
                    }
                    replyCmtAdapter = new ReplyCmtAdapter(getApplicationContext(),replyCmtArrayList);
                    recyclerView.setAdapter(replyCmtAdapter);
                    replyCmtAdapter.notifyDataSetChanged();
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
    public void FocusEdit(){
        editText_rep_cmt.requestFocus();
        imm.showSoftInput(editText_rep_cmt, InputMethodManager.SHOW_IMPLICIT);
    }
}
