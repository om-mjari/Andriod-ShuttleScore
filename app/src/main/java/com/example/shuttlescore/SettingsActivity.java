package com.example.shuttlescore;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "selected_theme";

    Button btnApply;
    RadioGroup radioGroup;
    private ImageView back,themeImage;
    //private RadioGroup themeGroup;
    private RadioButton themePink, themeGreen, themeYellow, themeBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing or show a toast
                // Toast.makeText(Login.this, "Back is disabled on login screen", Toast.LENGTH_SHORT).show();
            }
        });

        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        radioGroup = findViewById(R.id.themeGroup);
        btnApply = findViewById(R.id.btnApply);
        themeBlue = findViewById(R.id.themeBlue);
        themeYellow = findViewById(R.id.themeYellow);
        themeGreen = findViewById(R.id.themeGreen);
        themePink = findViewById(R.id.themePink);
        back = findViewById(R.id.back);
        themeImage = findViewById(R.id.themeImage);
        setSelectedThemeRadio();
        onclick();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.themeBlue) {
                themeImage.setImageResource(R.drawable.bluetheme);
            } else if (checkedId == R.id.themeYellow) {
                themeImage.setImageResource(R.drawable.orangetheme);
            } else if (checkedId == R.id.themeGreen) {
                themeImage.setImageResource(R.drawable.greentheme);
            } else if (checkedId == R.id.themePink) {
                themeImage.setImageResource(R.drawable.pinktheme);
            }
        });


        btnApply.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            String selectedTheme;

            if (selectedId == R.id.themeBlue) {
                selectedTheme = "AppTheme";
            } else if (selectedId == R.id.themePink) {
                selectedTheme = "PinkTheme";
            } else if (selectedId == R.id.themeGreen) {
                selectedTheme = "GreenTheme";
            } else if (selectedId == R.id.themeYellow) {
                selectedTheme = "YellowTheme";
            } else {
                selectedTheme = "AppTheme"; // fallback/default
            }

            ThemeUtils.saveTheme(this, selectedTheme);
            recreate(); // Recreate activity to apply new theme
        });

        // Navigation drawer functionality

    }

    private void setSelectedThemeRadio() {
        String savedTheme = ThemeUtils.getSavedTheme(this);

        switch (savedTheme) {
            case "PinkTheme":
                themePink.setChecked(true);
                break;
            case "GreenTheme":
                themeGreen.setChecked(true);
                break;
            case "YellowTheme":
                themeYellow.setChecked(true);
                break;
            case "AppTheme":
            default:
                themeBlue.setChecked(true);
                break;
        }
    }


    private void onclick(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }}