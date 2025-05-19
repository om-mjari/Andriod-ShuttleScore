package com.example.shuttlescore;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.window.SplashScreen;
import android.util.Pair;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class splashscreen extends AppCompatActivity {
    private static int DELAY_TIME = 4000;

    Animation topAnim, bottomAnim;
    ImageView imageView;
    TextView app_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        imageView =findViewById(R.id.imageView2);
        app_name =findViewById(R.id.app_name);

        imageView.setAnimation(topAnim);
        app_name.setAnimation(bottomAnim);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(splashscreen.this, Login.class);

                Pair<View, String>[] pairs = new Pair[2];
                pairs[0] = new Pair<>(imageView, "splash_image");
                pairs[1] = new Pair<>(app_name, "splash_text");

                SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
                String contact = sh.getString("contact", "");
                if (contact.isEmpty()) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(splashscreen.this, pairs);
                    startActivity(i, options.toBundle());
                } else {
                    i = new Intent(splashscreen.this, MainActivity.class);
                    startActivity(i);
                }

                finish();
            }
        }, DELAY_TIME);
    }
}