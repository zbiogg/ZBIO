package com.tungkon.zbio.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tungkon.zbio.Fragment.HomeFragment;
import com.tungkon.zbio.Fragment.NotificationFragment;
import com.tungkon.zbio.Fragment.MenuFragment;
import com.tungkon.zbio.R;
import com.tungkon.zbio.Fragment.RqFriendFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class DashBoardActivity extends AppCompatActivity {
    ActionBar actionBar;
    Context context;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    TextView txtTitleToolbar;
    SharedPreferences preferences;
    ImageView imgLogoToolBar;
    ImageButton btn_menu,btn_search;
    public static BottomNavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        FloatingActionButton btnewpost;
        btnewpost = findViewById(R.id.btn_newpost);
        toolbar = findViewById(R.id.toolbar_dashboard);
        txtTitleToolbar = toolbar.findViewById(R.id.txtTitleToolbar);
        imgLogoToolBar = toolbar.findViewById(R.id.imgLogoToolbar);
        btn_menu = toolbar.findViewById(R.id.btn_menu_item);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this,MenuItemActivity.class));
            }
        });
        btn_search = toolbar.findViewById(R.id.btn_search_toolbar);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });
        preferences = getSharedPreferences("user",MODE_PRIVATE);
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

    }

    public BottomNavigationView.OnNavigationItemSelectedListener selectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment menu;
        Fragment friends;
        Fragment notifications;
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Fragment home = (Fragment) getSupportFragmentManager().findFragmentByTag("frmhome");
//        if(home!=null){
//            home.onActivityResult(2,resultCode,data);
//        }
//    }
}
