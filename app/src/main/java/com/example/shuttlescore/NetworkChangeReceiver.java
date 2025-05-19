package com.example.shuttlescore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ConnectivityUtils.isConnected(context)) {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(context, NoInternetActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
