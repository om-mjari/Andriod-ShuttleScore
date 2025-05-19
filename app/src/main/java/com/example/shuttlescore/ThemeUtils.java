package com.example.shuttlescore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ThemeUtils {
    private static final String PREF_NAME = "theme_pref";
    private static final String KEY_THEME = "selected_theme";

    public static void applyTheme(Activity activity) {
        String theme = getSavedTheme(activity);
        switch (theme) {
            case "YellowTheme":
                activity.setTheme(R.style.YellowTheme);
                break;
            case "GreenTheme":
                activity.setTheme(R.style.GreenTheme);
                break;
            case "PinkTheme":
                activity.setTheme(R.style.PinkTheme);
                break;
            default:
                activity.setTheme(R.style.AppTheme);
                break;
        }
    }

    public static void saveTheme(Context context, String themeName) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_THEME, themeName).apply();
    }

    public static String getSavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_THEME, "AppTheme");
    }
}
