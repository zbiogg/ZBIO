package com.tungkon.zbio.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tungkon.zbio.Activity.AboutUsActivity;
import com.tungkon.zbio.Activity.LoginActivity;
import com.tungkon.zbio.Activity.MenuItemActivity;
import com.tungkon.zbio.Activity.MyProfileActivity;
import com.tungkon.zbio.Adapter.UserPostAdapter;
import com.tungkon.zbio.Model.Post;
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
public class MenuFragment extends Fragment {
    private SharedPreferences preferences;
    private LinearLayout ln_view_profile,lnviewaboutus;
    private Button btn_logout;
    private  ImageView imgUserAvt;
    private  TextView txtUserName;
    private ProgressDialog dialog;
    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewprofile = inflater.inflate(R.layout.fragment_menu, container, false);
        ln_view_profile = viewprofile.findViewById(R.id.lnviewprofile);
        lnviewaboutus = viewprofile.findViewById(R.id.ln_view_about_us);
        btn_logout = viewprofile.findViewById(R.id.btnlogout);
        preferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        imgUserAvt = viewprofile.findViewById(R.id.imgUserAvtMenu);
        txtUserName = viewprofile.findViewById(R.id.txtUserNameMenu);
        preferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        if(preferences.getString("img_avt","").equals("null")){
            Picasso.get().load("https://zbiogg.com/img/avt/"+"avt-default.png").placeholder(R.color.bgshimmer).into(imgUserAvt);
        }else{
            Picasso.get().load("https://zbiogg.com/img/avt/"+preferences.getString("img_avt","")).placeholder(R.color.bgshimmer).into(imgUserAvt);
        }
        txtUserName.setText(preferences.getString("lastName","")+" "+preferences.getString("firstName",""));

        ln_view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyProfileActivity.class));
            }
        });
        lnviewaboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AboutUsActivity.class));
            }
        });


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(getContext());
                dialog.setCancelable(false);
                dialog.setMessage("Đang đăng xuất...");
                dialog.show();
                StringRequest request = new StringRequest(Request.Method.GET,"https://zbiogg.com/api/logout", response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getBoolean("success")){
//                                dialog.setMessage("Đang đăng xuất");
//                                dialog.show();
//                                Handler handler = new Handler();
//                                handler.postDelayed(new Runnable() {
//                                    public void run() {
//                                        dialog.dismiss();
//
//                                    }
//                                },1000);
                            startActivity(new Intent(getContext(),LoginActivity.class));



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
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(request);
            }
        });
        return viewprofile;
    }
}
