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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.tungkon.zbio.Adapter.RequestFriendsAdapter;
import com.tungkon.zbio.Adapter.SuggestAdapter;
import com.tungkon.zbio.Model.User;
import com.tungkon.zbio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RqFriendFragment extends Fragment {
    RecyclerView rc_rqfriend, rc_sgfriend;
    ArrayList<User> arrayListRqFriends,arrayListSgFriends;
    SharedPreferences preferences;
    RequestFriendsAdapter requestFriendsAdapter;
    SuggestAdapter suggestAdapter;
    SwipeRefreshLayout swipe_friend;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout ln_shimmer_rq;
    int check_load_rq,check_load_sg;
    public RqFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rq_friend, container, false);
        swipe_friend = view.findViewById(R.id.swpie_rq_layout);
        swipe_friend.setColorSchemeColors(Color.parseColor("#6fbe44"));
        shimmerFrameLayout=view.findViewById(R.id.shimmer_view_request);

        swipe_friend.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRequestFriends();
                getSuggestFriends();
                swipe_friend.setRefreshing(false);
                Log.d("refesh","Done");

            }
        });
        // Inflate the layout for this fragment
        rc_rqfriend = view.findViewById(R.id.rc_view_rqfriends);
        rc_sgfriend = view.findViewById(R.id.rc_view_suggestfriends);
        LinearLayoutManager rqlayoutManager = new LinearLayoutManager(getActivity());
        LinearLayoutManager sglayoutManager = new LinearLayoutManager(getActivity());
        rc_rqfriend.setLayoutManager(rqlayoutManager);
        rc_sgfriend.setLayoutManager(sglayoutManager);
        preferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        getRequestFriends();
        getSuggestFriends();

        return view;
    }
    public void ShimmerStop(){
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
    }
    public  void getRequestFriends(){
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/requestFriends",response -> {
            arrayListRqFriends = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(response);
                Log.d("zbiogg123",object+"");
                JSONArray rqs = object.getJSONArray("data");
                for(int i=0;i<rqs.length();i++){
                    User user = new Gson().fromJson(rqs.get(i).toString(), User.class);
                    arrayListRqFriends.add(user);
                }
                requestFriendsAdapter = new RequestFriendsAdapter(getContext(),arrayListRqFriends);
                rc_rqfriend.setAdapter(requestFriendsAdapter);
                rc_rqfriend.setHasFixedSize(true);
                rc_rqfriend.setNestedScrollingEnabled(false);
                requestFriendsAdapter.notifyDataSetChanged();
//                ShimmerStop();
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
    public  void getSuggestFriends(){
        StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/suggestFriends",response -> {
            arrayListSgFriends = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(response);
                Log.d("zbiogg123",object+"");
                JSONArray sgs = object.getJSONArray("data");
                for(int i=0;i<sgs.length();i++){
                    User user = new Gson().fromJson(sgs.get(i).toString(), User.class);
                    arrayListSgFriends.add(user);
                    Log.d("eeeee",arrayListSgFriends.get(i).getImgAvt()+"");
                }
                suggestAdapter = new SuggestAdapter(getContext(),arrayListSgFriends);
                rc_sgfriend.setAdapter(suggestAdapter);
                rc_sgfriend.setHasFixedSize(true);
                rc_sgfriend.setNestedScrollingEnabled(false);
                suggestAdapter.notifyDataSetChanged();
                ShimmerStop();

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

}
