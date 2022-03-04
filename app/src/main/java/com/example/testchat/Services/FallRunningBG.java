package com.example.testchat.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.testchat.R;
import com.example.testchat.Views.FallDetectionActivity;
import com.example.testchat.Views.MainActivity;
import com.example.testchat.Views.prise;

public class FallRunningBG extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, FallDetectionActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"android")
                .setSmallIcon(R.drawable.icon_splash)
                .setContentTitle("AieuxScope Fall Detection")
                .setContentText("Safety tracking is now running in the BG")
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher_background,"OK",pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);



        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1233,builder.build());

    }
}
