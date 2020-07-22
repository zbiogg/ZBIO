package com.tungkon.zbio.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.tungkon.zbio.Model.User;
import com.tungkon.zbio.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestFriendsAdapter extends RecyclerView.Adapter<RequestFriendsAdapter.ViewHolder>{
    Context context;
    ArrayList<User> arrayListRqFriends;
    SharedPreferences preferences;

    public RequestFriendsAdapter(Context context, ArrayList<User> arrayListRqFriends) {
        this.context = context;
        this.arrayListRqFriends = arrayListRqFriends;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_view_rqfriend,parent,false);
        preferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User rq = arrayListRqFriends.get(position);
        Picasso.get().load("https://zbiogg.com/img/avt/"+rq.getImgAvt()).into(holder.imgUserAvt);
        holder.txtUserFullName.setText(rq.getLastName()+" "+rq.getFirstName());
        holder.txt_mutural_friends.setText(rq.getMutual_friends()+" bạn chung");
        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ln_friend_status.setVisibility(View.VISIBLE);
                holder.ln_option_friend.setVisibility(View.GONE);
                StringRequest requestaccect = new StringRequest(Request.Method.POST,"https://zbiogg.com/api/acceptFriend",response -> {
                    try {
                        Toast.makeText(context,"oke ne",Toast.LENGTH_SHORT).show();
                        JSONObject objectaccept = new JSONObject(response);
                        if(objectaccept.getBoolean("success")){
                            holder.ln_friend_status.setVisibility(View.VISIBLE);
                            holder.ln_option_friend.setVisibility(View.GONE);
                        }else{
                            holder.ln_friend_status.setVisibility(View.GONE);
                            holder.ln_option_friend.setVisibility(View.VISIBLE);
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

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map= new HashMap<>();
                        map.put("senderID",rq.getId()+"");
                        return map;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(requestaccect);
            }
        });
        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.txt_friend_status.setText("Đã gỡ lời mời kết bạn");
                holder.ln_option_friend.setVisibility(View.GONE);
                holder.ln_friend_status.setVisibility(View.VISIBLE);
                StringRequest request = new StringRequest(Request.Method.POST,"https://zbiogg.com/api/deleteRequest",response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getBoolean("success")){
                            holder.txt_friend_status.setText("Đã gỡ lời mời kết bạn");
                            holder.ln_option_friend.setVisibility(View.GONE);
                            holder.ln_friend_status.setVisibility(View.VISIBLE);
                        }else{
                            holder.ln_option_friend.setVisibility(View.VISIBLE);
                            holder.ln_friend_status.setVisibility(View.GONE);
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

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map= new HashMap<>();
                        map.put("senderID",rq.getId()+"");
                        return map;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListRqFriends.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {
        ImageView imgUserAvt;
        TextView txtUserFullName,txt_friend_status,txt_mutural_friends;
        Button btn_accept,btn_remove;
        LinearLayout ln_friend_status,ln_option_friend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvt = (ImageView)itemView.findViewById(R.id.imgUserAvt);
            txtUserFullName = (TextView)itemView.findViewById(R.id.txtUserName);
            btn_accept = (Button)itemView.findViewById(R.id.btn_accept);
            btn_remove = (Button)itemView.findViewById(R.id.btn_remove);
            ln_friend_status = (LinearLayout)itemView.findViewById(R.id.ln_fiend_status);
            ln_option_friend = (LinearLayout)itemView.findViewById(R.id.ln_option_friend);
            txt_friend_status = (TextView)itemView.findViewById(R.id.txt_friend_status);
            txt_mutural_friends = (TextView)itemView.findViewById(R.id.txt_mutural_friends);
        }
    }

}
