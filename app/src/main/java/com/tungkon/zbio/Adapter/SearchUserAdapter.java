package com.tungkon.zbio.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.tungkon.zbio.Activity.DetailMessengerActivity;
import com.tungkon.zbio.Activity.ViewProfileActivity;
import com.tungkon.zbio.Model.User;
import com.tungkon.zbio.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {
    Context context;
    ArrayList<User> arrayListUser;
    SharedPreferences preferences;

    public SearchUserAdapter(Context context, ArrayList<User> arrayListUser) {
        this.context = context;
        this.arrayListUser = arrayListUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =inflater.inflate(R.layout.item_view_search_user,parent,false);
        context=parent.getContext();
        preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = arrayListUser.get(position);
        Picasso.get().load("https://zbiogg.com/img/avt/"+user.getImgAvt()).into(holder.imgUserAvt);
        holder.txtUserName.setText(user.getLastName()+" "+user.getFirstName());

        switch (user.getStatus_friend()){
            case 0:
                if(user.getActionUserID()==user.getId()) {
                    holder.btn_message.setVisibility(View.GONE);
                    holder.btn_add_friend.setVisibility(View.GONE);
                    holder.btn_comfirm_friend.setVisibility(View.VISIBLE);
                    holder.txt_friend_status.setText(user.getMutual_friends()+" bạn chung");
                }else{
                    holder.btn_add_friend_success.setVisibility(View.VISIBLE);
                    holder.txt_friend_status.setText(user.getMutual_friends()+" bạn chung");
                }
                break;
            case 1:
                holder.txt_friend_status.setText("Bạn bè ( "+user.getMutual_friends()+" bạn chung )");
                holder.btn_message.setVisibility(View.VISIBLE);
                holder.btn_add_friend.setVisibility(View.GONE);
                break;
            case 4:
                holder.txt_friend_status.setText(user.getMutual_friends()+" bạn chung");
                holder.btn_message.setVisibility(View.GONE);
                holder.btn_add_friend.setVisibility(View.VISIBLE);
                break;

        }

        if((user.getCity()+"").equals("null")){
            holder.txt_city.setVisibility(View.GONE);
//            holder.txt_city.setText("Di động: "+user.getPhone());
        }else {
            holder.txt_city.setText("Đến từ " + user.getCity());
        }
        holder.ln_view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewProfileActivity.class);
                i.putExtra("userID",user.getId());
                context.startActivity(i);
            }
        });
        holder.btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailMessengerActivity.class);
                i.putExtra("userID",user.getId());
                context.startActivity(i);
            }
        });

        holder.btn_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest request = new StringRequest(Request.Method.POST, "https://zbiogg.com/api/addFriend", response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object!=null){
                            holder.btn_add_friend.setVisibility(View.GONE);
                            holder.btn_add_friend_success.setVisibility(View.VISIBLE);
                            holder.btn_add_friend.setImageResource(R.drawable.ic_add_friend_success);
                        }else{
                            holder.btn_add_friend.setImageResource(R.drawable.ic_add_friend);
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
                        map.put("receiverID",user.getId()+"");
                        return map;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });
        holder.btn_comfirm_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest requestaccect = new StringRequest(Request.Method.POST,"https://zbiogg.com/api/acceptFriend",response -> {
                    try {
                        JSONObject objectaccept = new JSONObject(response);

                        if(objectaccept.getBoolean("success")){

                            holder.btn_comfirm_friend.setVisibility(View.GONE);
                            holder.btn_message.setVisibility(View.VISIBLE);
                        }else{
                            holder.btn_comfirm_friend.setVisibility(View.VISIBLE);
                            holder.btn_message.setVisibility(View.GONE);
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
                        map.put("senderID",user.getId()+"");
                        return map;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(requestaccect);
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayListUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgUserAvt;
        TextView txtUserName,txt_friend_status,txt_city;
        ImageButton btn_message,btn_add_friend,btn_add_friend_success,btn_comfirm_friend;
        LinearLayout ln_view_profile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvt = (ImageView)itemView.findViewById(R.id.imgUserAvt);
            txtUserName = (TextView)itemView.findViewById(R.id.txtUserName);
            txt_friend_status = (TextView)itemView.findViewById(R.id.txt_friend_status);
            txt_city = (TextView)itemView.findViewById(R.id.txtcity);
            btn_message = (ImageButton)itemView.findViewById(R.id.btn_message);
            btn_add_friend = (ImageButton)itemView.findViewById(R.id.btn_add_friend);
            btn_add_friend_success = (ImageButton)itemView.findViewById(R.id.btn_add_friend_success);
            btn_comfirm_friend = (ImageButton)itemView.findViewById(R.id.btn_comfirm_friend);
            ln_view_profile = (LinearLayout)itemView.findViewById(R.id.ln_view_profile);
        }
    }
}
