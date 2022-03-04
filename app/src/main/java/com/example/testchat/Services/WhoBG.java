package com.example.testchat.Services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.testchat.R;
import com.example.testchat.Views.AcceloGraph;
import com.example.testchat.Views.FallDetectionActivity;

public class WhoBG extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AcceloGraph.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"android")
                .setSmallIcon(R.drawable.icon_splash)
                .setContentTitle("AieuxScope Who ?")
                .setContentText("Who ? is now running in the BG")
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher_background,"OK",pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);



        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1238,builder.build());

    }
}
