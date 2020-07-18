package com.tungkon.zbio.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.tungkon.zbio.Model.User;
import com.tungkon.zbio.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SuggestAdapter extends RecyclerView.Adapter<SuggestAdapter.ViewHolder> {
    Context context;
    ArrayList<User> arrayListSuggest;
    SharedPreferences preferences;

    public SuggestAdapter(Context context, ArrayList<User> arrayListSuggest) {
        this.context = context;
        this.arrayListSuggest = arrayListSuggest;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_view_suggest_friends,parent,false);
        preferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User sg = arrayListSuggest.get(position);
        Picasso.get().load("https://zbiogg.com/img/avt/"+sg.getImgAvt()).into(holder.imgUserAvt);
        holder.txtUserFullName.setText(sg.getLastName()+" "+sg.getFirstName());
        holder.btn_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ln_option_friend.setVisibility(View.GONE);
                holder.ln_cancel_friend.setVisibility(View.VISIBLE);
                StringRequest request = new StringRequest(Request.Method.POST, "https://zbiogg.com/api/addFriend",response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object!=null){
                            holder.ln_option_friend.setVisibility(View.GONE);
                            holder.ln_cancel_friend.setVisibility(View.VISIBLE);
                        }else{
                            holder.ln_option_friend.setVisibility(View.GONE);
                            holder.ln_cancel_friend.setVisibility(View.VISIBLE);
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
                        map.put("receiverID",sg.getId()+"");
                        return map;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });
        holder.btn_cancel_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ln_cancel_friend.setVisibility(View.GONE);
                holder.ln_option_friend.setVisibility(View.VISIBLE);
                StringRequest request = new StringRequest(Request.Method.POST,"https://zbiogg.com/api/cancelAddFriend",response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getBoolean("success")){
                            holder.ln_cancel_friend.setVisibility(View.GONE);
                            holder.ln_option_friend.setVisibility(View.VISIBLE);
                        }else{
                            holder.ln_cancel_friend.setVisibility(View.VISIBLE);
                            holder.ln_option_friend.setVisibility(View.GONE);
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
                        map.put("receiverID",sg.getId()+"");
                        return map;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });
        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayListSuggest.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,arrayListSuggest.size());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListSuggest.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserAvt;
        TextView txtUserFullName;
        Button btn_add_friend,btn_remove,btn_cancel_friend;
        LinearLayout ln_cancel_friend,ln_option_friend;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvt = (ImageView)itemView.findViewById(R.id.imgUserAvt);
            txtUserFullName = (TextView)itemView.findViewById(R.id.txtUserName);
            btn_add_friend = (Button)itemView.findViewById(R.id.btn_add_friend);
            btn_remove = (Button)itemView.findViewById(R.id.btn_remove);
            btn_cancel_friend = (Button)itemView.findViewById(R.id.btn_cancel);
            ln_cancel_friend = (LinearLayout)itemView.findViewById(R.id.ln_cancel_friend);
            ln_option_friend = (LinearLayout)itemView.findViewById(R.id.ln_option_friend);

        }
    }
}
