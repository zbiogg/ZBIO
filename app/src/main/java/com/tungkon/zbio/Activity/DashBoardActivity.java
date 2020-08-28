package com.tungkon.zbio.Activity;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
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
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.tungkon.zbio.Adapter.NotificationAdapter;
import com.tungkon.zbio.Fragment.HomeFragment;
import com.tungkon.zbio.Fragment.NotificationFragment;
import com.tungkon.zbio.Fragment.MenuFragment;
import com.tungkon.zbio.Model.Message;
import com.tungkon.zbio.Model.Notification;
import com.tungkon.zbio.Model.Post;
import com.tungkon.zbio.Model.User;
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
import androidx.core.app.NotificationManagerCompat;
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
    TextView txtTitleToolbar, bagde_noti ;
    SharedPreferences preferences;
    ImageView imgLogoToolBar;
    ImageButton btn_mess,btn_search;
    String [] postIDlink,cmtIDlink;
    String senderUserName,send_message;
    BottomNavigationMenuView bottomNavigationMenuView;
    BottomNavigationItemView item_noti;
    View badge;
    private static int noti_Qty=0;
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
         bottomNavigationMenuView = (BottomNavigationMenuView) navView.getChildAt(0);
        View v1 = bottomNavigationMenuView.getChildAt(3);
        item_noti = (BottomNavigationItemView) v1;
        badge = LayoutInflater.from(this)
                .inflate(R.layout.badge_noti_layout, bottomNavigationMenuView, false);
        bagde_noti = badge.findViewById(R.id.notification_badge);
        bagde_noti.setVisibility(View.GONE);
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
        getNoti();


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
        socket.on("server_send_message",onDataGetMess);

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
                                        NotificationFragment.notificationArrayList.add(0,notification);
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
     private  Emitter.Listener onDataGetMess = new Emitter.Listener() {
         @Override
         public void call(Object... args) {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     JSONObject object =(JSONObject) args[0];
                     try {


                             Log.d("messsss", object.getString("fromID") + "");
                             StringRequest request = new StringRequest(Request.Method.GET, "https://zbiogg.com/api/detailmess/" + object.getString("messID"), response -> {
                                 try {
                                     JSONObject objectmess = new JSONObject(response);
                                     if (objectmess.getBoolean("success")) {
                                         JSONArray messArray = objectmess.getJSONArray("mess");
                                         JSONObject userObj = objectmess.getJSONObject("from_user");
                                         Message messdetail = new Gson().fromJson(messArray.get(0).toString(), Message.class);
                                         show_Noti_Message(object.getInt("fromID"),userObj.getString("firstName"),messdetail.getMessage());
                                         if(object.getInt("fromID")==DetailMessengerActivity.userID) {
                                             DetailMessengerActivity.messageArrayList.add(messdetail);
                                             DetailMessengerActivity.detailMessageAdapter.notifyItemInserted(DetailMessengerActivity.messageArrayList.size() - 1);
                                             DetailMessengerActivity.recyclerView.scrollToPosition(DetailMessengerActivity.messageArrayList.size() - 1);
                                         }
                                     }

                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }
                             }, error -> {

                             }) {
                                 @Override
                                 public Map<String, String> getHeaders() throws AuthFailureError {
                                     String token = preferences.getString("token", "");
                                     HashMap<String, String> map = new HashMap<>();
                                     map.put("Authorization", "Bearer " + token);
                                     return map;
                                 }
                             };
                             RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                             queue.add(request);

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
    bagde_noti.setVisibility(View.VISIBLE);
    noti_Qty++;
    if(noti_Qty>99){
    bagde_noti.setText("99+");
    }else {
        bagde_noti.setText(noti_Qty+"");
    }
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

    String CHANNEL_ID="NOTIFICATION";
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
    NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_HIGH);
    PendingIntent pendingIntent= PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    android.app.Notification notification=new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
            .setContentText(senderUserName+""+send_message)
            .setContentTitle("Chào! "+preferences.getString("firstName",""))
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(android.app.Notification.CATEGORY_MESSAGE)
            .addAction(android.R.drawable.sym_action_chat,"Thông báo!",pendingIntent)
            .setChannelId(CHANNEL_ID)
            .setSmallIcon(R.drawable.logo).
            setAutoCancel(true)
            .build();
    notification.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+getPackageName() + "/" + R.raw.messenger);//Here is FILE_NAME is the name of file that you want to play
    MediaPlayer mp = MediaPlayer. create (getApplicationContext(), notification.sound);
    mp.start();
    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    v.vibrate(200);
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
    notificationManager.createNotificationChannel(notificationChannel);
    notificationManager.notify(1,notification);



}
@TargetApi(Build.VERSION_CODES.O)
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public void show_Noti_Message(int userID,String name,String message){
    Intent i = new Intent(getApplicationContext(),DetailMessengerActivity.class);
    i.putExtra("userID",userID);
    String CHANNEL_ID="MESSAGE";
    NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_HIGH);
    PendingIntent pendingIntent= PendingIntent.getActivity(getApplicationContext(),2,i,0);
    android.app.Notification notification=new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
            .setContentText(name+" : "+message)
            .setContentTitle("Bạn có tin nhắn mới!")
            .setContentIntent(pendingIntent)
            .setPriority(android.app.Notification.PRIORITY_MAX)
            .setCategory(android.app.Notification.CATEGORY_MESSAGE)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)
            .addAction(android.R.drawable.sym_action_chat,"Tin nhắn!",pendingIntent)
            .setChannelId(CHANNEL_ID)
            .setSmallIcon(R.drawable.logo).
                    setAutoCancel(true)
            .build();
    notification.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+getPackageName() + "/" + R.raw.messenger);//Here is FILE_NAME is the name of file that you want to play
    MediaPlayer mp = MediaPlayer. create (getApplicationContext(), notification.sound);
    mp.start();
    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    v.vibrate(200);
////    NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
    notificationManager.createNotificationChannel(notificationChannel);
    notificationManager.notify(2,notification);


}
    public void getNoti(){
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/getNotiUnReader",response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
//                    JSONArray notis = object.getJSONArray("notifications");
//                    for(int i=0;i<notis.length();i++){
//                        Notification notification = new Gson().fromJson(notis.get(i).toString(), Notification.class);
//                        if(notification.getStatus()==0){
//                            noti_Qty++;
//                        }
//                    }
                    noti_Qty=object.getInt("qty");
                    if(noti_Qty>99){
                        bagde_noti.setText("99+");
                    }else {
                        bagde_noti.setText(noti_Qty+"");
                    }
                    item_noti.addView(badge);
                    if(noti_Qty!=0) {
                        bagde_noti.setVisibility(View.VISIBLE);
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
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

}
