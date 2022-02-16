package com.example.testchat.Services;

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
import com.example.testchat.Views.MainActivity;
import com.example.testchat.Views.prise;

public class    AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, prise.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Intent ii = new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);
        PendingIntent pandingIntent = PendingIntent.getActivity(context,0,ii,0);

        Vibrator vib = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vib.vibrate(1000);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"android")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Android Alarm Manager")
                .setContentText("Il est temps pour vous de prendre un verre d'eau")
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher_background,"yes",pendingIntent)
                .addAction(R.drawable.ic_launcher_background,"no",pandingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123,builder.build());

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();


        // r.play();


    }
}
