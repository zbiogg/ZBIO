package com.tungkon.zbio.Adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.tungkon.zbio.Activity.DetailPostActivity;
import com.tungkon.zbio.Activity.EditPostActivity;
import com.tungkon.zbio.Activity.ViewProfileActivity;
import com.tungkon.zbio.Fragment.HomeFragment;
import com.tungkon.zbio.Model.Post;
import com.tungkon.zbio.R;

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

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    Context context;
    ArrayList<Post> postArrayList;
    LinearLayout lnfirst,lnnormal;
    SharedPreferences preferences;

    public PostsAdapter(Context context, ArrayList<Post> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;

    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.itemview_post,parent,false);
        context=parent.getContext();
        lnnormal = view.findViewById(R.id.lnnormal);
        preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Post post = postArrayList.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                long time = sdf.parse(post.getCreatedAt()).getTime();
                Date today = new Date(time);
                Calendar cal = Calendar.getInstance();
                cal.setTime(today);
                Log.d("qq", String.valueOf(cal.get(Calendar.YEAR)));
                holder.txtTimePost.setText(getTimeAgo(time));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.txtUserName.setText(post.getUserfullname());
            holder.txtUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ViewProfileActivity.class);
                    i.putExtra("userID",post.getUserID());
                    context.startActivity(i);
                }
            });
            holder.txtPostContent.setText(post.getPostContent());
            Picasso.get().load("https://zbiogg.com/img/avt/" + post.getUserAvt()).into(holder.imgUserAvt);
            holder.imgUserAvt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ViewProfileActivity.class);
                    i.putExtra("userID",post.getUserID());
                    context.startActivity(i);
                }
            });
            if(!String.valueOf(post.getPostImage()).equals("null")){
                Picasso.get().load("https://zbiogg.com/img/posts/" + post.getPostImage()).placeholder(R.color.bgshimmer).into(holder.imgPost);
            }

            if (post.getLikeQty() > 0) {
                holder.txt_likes.setText(post.getLikeQty().toString() + "");

            } else {
                holder.txt_likes.getLayoutParams().height = 0;
                holder.txt_likes.setLayoutParams(holder.txt_likes.getLayoutParams());
                holder.txt_likes.setText(null);

            }

            if (post.getCmtQty() > 0) {
                holder.txt_cmts.setText(post.getCmtQty().toString() + " bình luận");
            } else {
                holder.txt_cmts.getLayoutParams().height = 0;
                holder.txt_cmts.setLayoutParams(holder.txt_likes.getLayoutParams());
                holder.txt_cmts.setText(null);

            }
            holder.ln_detailpost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DetailPostActivity.class);
                    i.putExtra("postID",post.getPostID());
                    i.putExtra("post_UserAvt",post.getUserAvt());
                    i.putExtra("post_UserName",post.getUserfullname());
                    i.putExtra("post_CreatedAt",post.getCreatedAt());
                    i.putExtra("post_Content",post.getPostContent());
                    i.putExtra("post_Image",post.getPostImage());
                    i.putExtra("post_likeqty",post.getLikeQty());
                    i.putExtra("post_cmtqty",post.getCmtQty());
                    i.putExtra("post_liked",post.getLiked());
                    context.startActivity(i);
                }
            });

            if(post.getLiked()==0){
                holder.btn_like.setTextColor(Color.parseColor("#707070"));
            }else{
                holder.btn_like.setTextColor(Color.parseColor("#ff9900"));
                holder.btn_like.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_care, //left
                        0, //top
                        0, //right
                        0 //bottom
                );
                holder.btn_like.setText("Thương thương");
            }
            holder.btn_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.btn_like.getText().equals("Thích")){

                        holder.btn_like.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_care, //left
                                0, //top
                                0, //right
                                0 //bottom
                        );
                        holder.btn_like.setText("Thương thương");
                        post.setLiked(1);
                        post.setLikeQty(post.getLikeQty()+1);
                        holder.btn_like.setTextColor(Color.parseColor("#ff9900"));
                    }else{
                        holder.btn_like.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_like_dark, //left
                                0, //top
                                0, //right
                                0 //bottom
                        );
                        holder.btn_like.setText("Thích");
                        post.setLiked(0);
                        post.setLikeQty(post.getLikeQty()-1);
                        holder.btn_like.setTextColor(Color.parseColor("#707070"));
                    }
                    StringRequest request = new StringRequest(Request.Method.POST,"https://zbiogg.com/api/likes",response -> {
                        try {
                            JSONObject object = new JSONObject(response);
                            int like_qty=object.getInt("like_qty");
                                if(object.getString("message").equals("liked")){
                                    holder.txt_likes.setText((post.getLikeQty()+1)+"");
                                    holder.txt_likes.getLayoutParams().height= (int) (35*context.getResources().getDisplayMetrics().density);;
                                    holder.txt_likes.setLayoutParams(holder.txt_likes.getLayoutParams());
                                    holder.txt_likes.setText(like_qty+"");
                                }else{
                                    if(like_qty==0){
                                        holder.txt_likes.getLayoutParams().height = 0;
                                        holder.txt_likes.setLayoutParams(holder.txt_likes.getLayoutParams());
                                        holder.txt_likes.setText(null);
                                    }else{
                                        holder.txt_likes.setText(like_qty+"");
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
                            map.put("postID",post.getPostID().toString());
                            return map;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(context);
                    queue.add(request);
                }

            });
            holder.btn_cmt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i =new Intent(context,DetailPostActivity.class);
                    i.putExtra("postID",post.getPostID());
                    i.putExtra("post_UserAvt",post.getUserAvt());
                    i.putExtra("post_UserName",post.getUserfullname());
                    i.putExtra("post_CreatedAt",post.getCreatedAt());
                    i.putExtra("post_Content",post.getPostContent());
                    i.putExtra("post_Image",post.getPostImage());
                    i.putExtra("post_likeqty",post.getLikeQty());
                    i.putExtra("post_cmtqty",post.getCmtQty());
                    i.putExtra("focus_editcmt",1);
                    i.putExtra("post_liked",post.getLiked());
                    context.startActivity(i);
                }
            });

            holder.btn_more.setOnClickListener(new View.OnClickListener() {
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
                    if(preferences.getInt("id",0)!=post.getUserID()){
                        ln_edit_post.setVisibility(View.GONE);
                        ln_delete_post.setVisibility(View.GONE);
                    }
                    BottomSheetDialog dialog = new BottomSheetDialog(contextbs);
                    dialog.setContentView(view);
                    dialog.show();
                    ln_copy_test.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(holder.txtPostContent.getText().toString().equals("")){
                                Toast.makeText(context, "Bài viết không có nội dung để sao chép!", Toast.LENGTH_LONG).show();
                            }else {
                                ClipboardManager _clipboard = (ClipboardManager) contextbs.getSystemService(Context.CLIPBOARD_SERVICE);
                                _clipboard.setText(holder.txtPostContent.getText() + "");
                                Toast.makeText(context, "Đã sao chép nội dung bài viết vào bộ nhớ tạm", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    ln_copy_link.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager _clipboard = (ClipboardManager) contextbs.getSystemService(Context.CLIPBOARD_SERVICE);
                            _clipboard.setText("https://zbiogg.com/posts/"+post.getPostID());
                            Toast.makeText(context, "Đã sao chép liên kết bài viết vào bộ nhớ tạm", Toast.LENGTH_LONG).show();
                        }
                    });
                    ln_edit_post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent i = new Intent(context, EditPostActivity.class);
                            context.startActivity(i);
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
                                        postArrayList.remove(position);
                                        notifyItemRemoved(position);
                                        holder.itemView.setVisibility(View.GONE);
                                        Toast.makeText(context, "Đã xóa bài viết", Toast.LENGTH_LONG).show();

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
                                    map.put("postID",post.getPostID().toString());
                                    return map;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(context);
                            queue.add(request);
                        }
                    });
                }
            });



    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }





    public class ViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmerFrameLayout;
        LinearLayout ln_detailpost;
        TextView txtUserName;
        TextView txtTimePost;
        TextView txtPostContent;
        ImageView imgUserAvt;
        ImageView imgPost;
        TextView txt_likes;
        TextView txt_cmts;
        Button btn_like,btn_cmt;
        ImageButton btn_more;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ln_detailpost = (LinearLayout)itemView.findViewById(R.id.ln_detailpost);
            btn_like = (Button)itemView.findViewById(R.id.btn_like);
            btn_cmt = (Button)itemView.findViewById(R.id.btn_cmt);
            btn_more = (ImageButton) itemView.findViewById(R.id.btn_more);
            txtUserName = (TextView)itemView.findViewById(R.id.txtUserName);
            txtTimePost = (TextView)itemView.findViewById(R.id.txtTimePost);
            txtPostContent = (TextView)itemView.findViewById(R.id.txtPostContent);
            imgUserAvt = (ImageView) itemView.findViewById(R.id.imgUserAvt);
            imgPost = (ImageView) itemView.findViewById(R.id.imgPost);
            txt_likes = (TextView)itemView.findViewById(R.id.txt_likes);
            txt_cmts =  (TextView)itemView.findViewById(R.id.txt_cmts);
        }

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

}
