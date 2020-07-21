package com.tungkon.zbio.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tungkon.zbio.Activity.CreatePost;
import com.tungkon.zbio.Activity.DetailCmtActivity;
import com.tungkon.zbio.Activity.DetailPostActivity;
import com.tungkon.zbio.Model.Cmt;
import com.tungkon.zbio.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PostCmtAdapter extends RecyclerView.Adapter<PostCmtAdapter.ViewHolder>{
    Context context ;
    ArrayList<Cmt> cmtArrayList;

    public PostCmtAdapter(Context context, ArrayList<Cmt> cmtArrayList) {
        this.context = context;
        this.cmtArrayList = cmtArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_view_cmts,parent,false);
        context=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cmt cmt = cmtArrayList.get(position);
        holder.txtUserName.setText(cmt.getUserfullname());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(cmt.getCreatedAt()).getTime();
            Date today = new Date(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            Log.d("qq", String.valueOf(cal.get(Calendar.YEAR)));
            holder.txtTimeCmt.setText(getTimeAgo(time));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.txtCmtContent.setText(cmt.getContentCmt());
        Picasso.get().load("https://zbiogg.com/img/avt/"+cmt.getUserAvt()).into(holder.imgUserAvt);
        holder.txt_detailcmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(context,DetailCmtActivity.class);
                i.putExtra("cmtID",cmt.getId());
                i.putExtra("cmt_UserAvt",cmt.getUserAvt());
                i.putExtra("cmt_UserName",cmt.getUserfullname());
                i.putExtra("cmt_CreatedAt",cmt.getCreatedAt());
                i.putExtra("cmt_Content",cmt.getContentCmt());
                context.startActivity(i);

            }
        });
        if(cmt.getRepcmtQty()>0) {
            holder.txt_view_all_repcmt.setText("Xem " + cmt.getRepcmtQty() + " câu trả lời...");
        }else{
            holder.ln_view_detail_cmt.setVisibility(View.GONE);
        }
        holder.ln_view_detail_cmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(context,DetailCmtActivity.class);
                i.putExtra("cmtID",cmt.getId());
                i.putExtra("cmt_UserAvt",cmt.getUserAvt());
                i.putExtra("cmt_UserName",cmt.getUserfullname());
                i.putExtra("cmt_CreatedAt",cmt.getCreatedAt());
                i.putExtra("cmt_Content",cmt.getContentCmt());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cmtArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgUserAvt;
        TextView txtUserName,txtCmtContent,txtTimeCmt,txt_detailcmt,txt_view_all_repcmt;
        LinearLayout ln_view_detail_cmt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvt = (ImageView)itemView.findViewById(R.id.imgUserAvt);
            txtUserName = (TextView)itemView.findViewById(R.id.txtUserName);
            txtCmtContent = (TextView)itemView.findViewById(R.id.txtCmtContent);
            txtTimeCmt = (TextView)itemView.findViewById(R.id.txtTimeCmt);
            txt_detailcmt = (TextView)itemView.findViewById(R.id.txt_detailcmt);
            txt_view_all_repcmt=(TextView)itemView.findViewById(R.id.txt_view_all_repcmt);
            ln_view_detail_cmt=(LinearLayout)itemView.findViewById(R.id.ln_view_detail_cmt);
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
