package com.tungkon.zbio.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.tungkon.zbio.Activity.DashBoardActivity;
import com.tungkon.zbio.Adapter.NotificationAdapter;
import com.tungkon.zbio.Model.Notification;
import com.tungkon.zbio.Model.User;
import com.tungkon.zbio.R;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    public static RecyclerView recyclerView;
    public static NotificationAdapter notificationAdapter;
    public static ArrayList<Notification> notificationArrayList;
    SharedPreferences preferences;
    ShimmerFrameLayout shimmerFrameLayout;
    SwipeRefreshLayout swpie_notification_layout;
    LinearLayout ln_no_notification;
    int page=2;
    private Socket socket;
    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_notification, container, false);
        // Inflate the layout for this fragment
        realtimeNoti();
        ln_no_notification = view.findViewById(R.id.ln_no_notification);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_notification);
        swpie_notification_layout = view.findViewById(R.id.swpie_notification_layout);
        swpie_notification_layout.setColorSchemeColors(Color.parseColor("#6fbe44"));
        swpie_notification_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 500ms
                        swpie_notification_layout.setRefreshing(false);
                        shimmerFrameLayout.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.startShimmer();
                        getNoti();
                    }
                }, 500);


                Log.d("refesh","Done");


            }
        });

        recyclerView = view.findViewById(R.id.rc_view_notification);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(true);
        preferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        getNoti();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (! recyclerView.canScrollVertically(1)){ //1 for down
//                    Toast.makeText(getContext(),"Load more "+page,Toast.LENGTH_SHORT).show();
                    LoadMoreNoti();
                }
            }
        });
        return view;
    }
    public void getNoti(){
        notificationArrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/notification",response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray notis = object.getJSONArray("notifications");
                    for(int i=0;i<notis.length();i++){
                        Notification notification = new Gson().fromJson(notis.get(i).toString(), Notification.class);
                        notificationArrayList.add(notification);
                    }
                    if(notis.length()==0){
                        ln_no_notification.setVisibility(View.VISIBLE);
                    }
                    notificationAdapter = new NotificationAdapter(getContext(),notificationArrayList);
                    recyclerView.setAdapter(notificationAdapter);
                    notificationAdapter.notifyDataSetChanged();
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
                map.put("Authorization","Bearer "+token);
                return map;
            }


        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
    public void LoadMoreNoti(){
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/notification?page="+page,response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray notis = object.getJSONArray("notifications");
                    for(int i=0;i<notis.length();i++){
                        Notification notification = new Gson().fromJson(notis.get(i).toString(), Notification.class);
                        notificationArrayList.add(notification);
                    }
                    if(notis.length()!=0){
                        notificationAdapter.notifyItemRangeInserted(notificationArrayList.size(),notis.length());
                        page++;
                    }else{

                        Toast.makeText(getContext(),"Bạn đã xem hết thông báo!",Toast.LENGTH_LONG).show();
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
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
    public void realtimeNoti(){
        try {
            socket = IO.socket("http://chatzbio.herokuapp.com/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(getContext(),"catch",Toast.LENGTH_LONG).show();
        }

        socket.connect();
        JSONObject obj = new JSONObject();
        try {
            obj.put("romID", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            obj.put("message", "chao ban" );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("client_send_roomID", obj);
        socket.on("server_send_noti",onDataGetNoti);
    }
    private Emitter.Listener onDataGetNoti = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object =(JSONObject) args[0];
                    try {
                        Integer notiID =object.getInt("notiID");
                        Toast.makeText(getContext(),notiID,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
