package com.tungkon.zbio.Activity;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.tungkon.zbio.Adapter.NotificationAdapter;
import com.tungkon.zbio.Fragment.HomeFragment;
import com.tungkon.zbio.Fragment.NotificationFragment;
import com.tungkon.zbio.Fragment.MenuFragment;
import com.tungkon.zbio.Model.Notification;
import com.tungkon.zbio.R;
import com.tungkon.zbio.Fragment.RqFriendFragment;
import com.tungkon.zbio.Service.BgServIce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class DashBoardActivity extends AppCompatActivity {
    ActionBar actionBar;
    Context context;
    public static FragmentManager fragmentManager;
    Toolbar toolbar;
    TextView txtTitleToolbar;
    SharedPreferences preferences;
    ImageView imgLogoToolBar;
    ImageButton btn_mess,btn_search;
    String [] postIDlink,cmtIDlink;
    String senderUserName,send_message;
    private Socket socket;
    public static BottomNavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        FloatingActionButton btnewpost;
        btnewpost = findViewById(R.id.btn_newpost);
        preferences = getSharedPreferences("user",MODE_PRIVATE);
        toolbar = findViewById(R.id.toolbar_dashboard);
        txtTitleToolbar = toolbar.findViewById(R.id.txtTitleToolbar);
        imgLogoToolBar = toolbar.findViewById(R.id.imgLogoToolbar);
        btn_mess = toolbar.findViewById(R.id.btn_menu_item);
        btn_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this,MessengerActivity.class));
            }
        });
        btn_search = toolbar.findViewById(R.id.btn_search_toolbar);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });
        //Bottom Navigation
         navView= findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(selectedListener);
        fragmentManager = getSupportFragmentManager();
        //Fragment mặc định
        txtTitleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP,22f);
        txtTitleToolbar.setText("ZBIOGG");
        fragmentManager.beginTransaction().replace(R.id.content,new HomeFragment(),"frmhome").commit();

        btnewpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CreatePost.class));
            }
        });
        realtimeNoti();


    }

    public BottomNavigationView.OnNavigationItemSelectedListener selectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public  Fragment menu;
        public Fragment friends;
        public Fragment notifications;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            menu = fragmentManager.findFragmentByTag("frmmenu");
            friends = fragmentManager.findFragmentByTag("frmfriends");
            notifications = fragmentManager.findFragmentByTag("frmnotifications");

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    if(notifications!=null){
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("frmnotifications")).commit();
                    }
                    if(menu!=null) {
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("frmmenu")).commit();
                    }
                    if(friends!=null){
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("frmfriends")).commit();
                    }
                    imgLogoToolBar.setVisibility(View.VISIBLE);
                    txtTitleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP,22f);
                    txtTitleToolbar.setText("ZBIOGG");
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("frmhome")).commit();

                    return true;
                case R.id.nav_friends:
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("frmhome")).commit();
                    if(notifications!=null){
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("frmnotifications")).commit();
                    }
                    if(menu!=null) {
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("frmmenu")).commit();
                    }
                    if(friends!=null){
                        imgLogoToolBar.setVisibility(View.GONE);
                        txtTitleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
                        txtTitleToolbar.setText("Bạn bè");
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("frmfriends")).commit();
                    }else{
                        imgLogoToolBar.setVisibility(View.GONE);
                        txtTitleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
                        txtTitleToolbar.setText("Bạn bè");
                        fragmentManager.beginTransaction().add(R.id.content,new RqFriendFragment(),"frmfriends").commit();
                    }
                    return true;
                case R.id.nav_notifications:
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("frmhome")).commit();
                    if(friends!=null){
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("frmfriends")).commit();
                    }
                    if(menu!=null) {
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("frmmenu")).commit();
                    }
                    if(notifications!=null){
                        imgLogoToolBar.setVisibility(View.GONE);
                        txtTitleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
                        txtTitleToolbar.setText("Thông báo");
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("frmnotifications")).commit();
                    }else{
                        imgLogoToolBar.setVisibility(View.GONE);
                        txtTitleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
                        txtTitleToolbar.setText("Thông báo");
                        fragmentManager.beginTransaction().add(R.id.content,new NotificationFragment(),"frmnotifications").commit();
                    }

                    return true;
                case R.id.nav_menu:
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("frmhome")).commit();
                    if(friends!=null){
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("frmfriends")).commit();
                    }
                    if(notifications!=null) {
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("frmnotifications")).commit();
                    }
                    if(menu!=null){
                        imgLogoToolBar.setVisibility(View.GONE);
                        txtTitleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
                        txtTitleToolbar.setText("MENU");
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("frmmenu")).commit();
                    }else{
                        imgLogoToolBar.setVisibility(View.GONE);
                        txtTitleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
                        txtTitleToolbar.setText("MENU");
                        fragmentManager.beginTransaction().add(R.id.content,new MenuFragment(),"frmmenu").commit();
                    }
                    return true;
            }
            return false;
        }
    };
    public void realtimeNoti(){
        try {
            socket = IO.socket("http://chatzbio.herokuapp.com/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.connect();
        socket.emit("noti_client_id",preferences.getInt("id",0));
        socket.on("server_send_noti",onDataGetNoti);

    }
    private Emitter.Listener onDataGetNoti = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object =(JSONObject) args[0];
                    try {
                        Integer notiID =object.getInt("notiID");
                        StringRequest request1 = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/showNoti?notiID="+notiID,response -> {
                            try {
                                JSONObject object1 = new JSONObject(response);
                                if(object1.getBoolean("success")){
                                    JSONArray notis = object1.getJSONArray("noti");
                                    Notification notification = new Gson().fromJson(notis.get(0).toString(), Notification.class);
                                    cmtIDlink = notification.getUrl().split("\\?cmtID=");
                                    postIDlink = notification.getUrl().split("\\/");
                                    senderUserName=notification.getSenderFullname();
                                    send_message=notification.getMessage();
                                    Fragment notifrm = fragmentManager.findFragmentByTag("frmnotifications");
                                    show_Notification();
                                    if(notifrm!=null){
//                                        Toast.makeText(getApplicationContext(),"Roi",Toast.LENGTH_LONG).show();
                                        NotificationFragment.notificationArrayList.add(0,notification);
//                                        NotificationFragment.notificationAdapter.notifyItemInserted(0);
                                        NotificationFragment.notificationAdapter.notifyDataSetChanged();
                                    }else{
//                                        Toast.makeText(getApplicationContext(),"chua",Toast.LENGTH_LONG).show();
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
                                map.put("Authorization","Bearer "+token);
                                return map;
                            }
                        };
                        RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
                        queue1.add(request1);
//                        Toast.makeText(getApplicationContext(),"thong bao vua nhan: "+notiID,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Fragment home = (Fragment) getSupportFragmentManager().findFragmentByTag("frmhome");
//        if(home!=null){
//            home.onActivityResult(2,resultCode,data);
//        }
//    }
@TargetApi(Build.VERSION_CODES.O)
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

public void show_Notification(){
    Intent intent;
    if(cmtIDlink.length>1){
        int cmtID = Integer.parseInt(cmtIDlink[1]);
        intent = new Intent(getApplicationContext(), DetailCmtActivity.class);
        intent.putExtra("cmtID",cmtID);
    }
    else{
        int postID = Integer.parseInt(postIDlink[1]);
         intent = new Intent(getApplicationContext(), DetailPostActivity.class);
        intent.putExtra("postID",postID);
    }

    String CHANNEL_ID="MYCHANNEL";
    NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_LOW);
    PendingIntent pendingIntent= PendingIntent.getActivity(getApplicationContext(),1,intent,0);
    android.app.Notification notification=new android.app.Notification.Builder(getApplicationContext(),CHANNEL_ID)
            .setContentText(senderUserName+""+send_message)
            .setContentTitle("Chào! "+preferences.getString("firstName",""))
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.sym_action_chat,"Thông báo!",pendingIntent)
            .setChannelId(CHANNEL_ID)
            .setSmallIcon(R.drawable.logo).
            setAutoCancel(true)
            .build();
    NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.createNotificationChannel(notificationChannel);
    notificationManager.notify(1,notification);


}

}
