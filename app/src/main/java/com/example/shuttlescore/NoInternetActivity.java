package com.example.shuttlescore;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NoInternetActivity extends AppCompatActivity {
    private Button retryButton;
    private boolean isDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.applyTheme(this);
        setContentView(R.layout.activity_no_internet);

        retryButton = findViewById(R.id.retryButton);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    Intent intent = new Intent(NoInternetActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!isDialogShowing) {
            showExitDialog();
        }
    }

    private void showExitDialog() {
        isDialogShowing = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(NoInternetActivity.this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
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

    public boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
