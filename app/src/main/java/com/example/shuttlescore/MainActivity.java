package com.example.shuttlescore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private boolean isDialogShowing = false;
    LinearLayout homeside,setting,share,about,logout,profile,startmatch,addtournament;
    ImageView menu;
    Toolbar toolbar;
    private NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if (!ConnectivityUtils.isConnected(this)) {
            startActivity(new Intent(this, NoInternetActivity.class));
            finish();
            return;
        }

        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);


        ThemeUtils.applyTheme(this);

        SharedPreferences sh = getSharedPreferences("login",MODE_PRIVATE);
        String cnt = sh.getString("contact","");
        if(cnt.isEmpty()){
            Intent i = new Intent(MainActivity.this,Login.class);
            startActivity(i);
        }
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);




        NavigationView navigationView = findViewById(R.id.navigationView);

        View headerView = navigationView.getHeaderView(0);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        drawerLayout = findViewById(R.id.drawerLayout);
        homeside = headerView.findViewById(R.id.homeside);
        profile = headerView.findViewById(R.id.profile);
        startmatch = headerView.findViewById(R.id.startmatch);
        addtournament = headerView.findViewById(R.id.addtournament);
        setting = headerView.findViewById(R.id.setting);
        share = headerView.findViewById(R.id.share);
        about = headerView.findViewById(R.id.about);
        logout = headerView.findViewById(R.id.logout);


        toolbar = findViewById(R.id.toolbar);
        menu = toolbar.findViewById(R.id.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });
        homeside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this,Profile.class);
            }
        });
        startmatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this,addmatchfinal.class);
            }
        });
        addtournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, AddTournament.class);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this,SettingsActivity.class);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAppLink();
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this,AboutActivity.class);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor ed = sh.edit();
                ed.remove("contact");
                ed.commit();
                Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this,Login.class);
                startActivity(i);
            }
        });


        replaceFragment(new HomeFragment());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.tennis) {
                replaceFragment(new MyBadmintonFragment());
            } else if (item.getItemId() == R.id.looking) {
                replaceFragment(new LookingFragment());
            } else if (item.getItemId() == R.id.help) {
                replaceFragment(new HelpFragment());
            } else if (item.getItemId() == R.id.search) {
                replaceFragment(new SearchFragment());
            }

            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (!isDialogShowing) {
            isDialogShowing = true;
            showExitDialog();
        }
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finishAffinity();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                    isDialogShowing = false;
                })
                .setOnDismissListener(dialog -> isDialogShowing = false)
                .show();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity,secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    protected void onPause(){
        super.onPause();
        closeDrawer(drawerLayout);
    }
    private void shareAppLink() {
        String appLink = "https://play.google.com/store/apps/details?id=com.example.sidebar";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Download our app here: " + appLink);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
}