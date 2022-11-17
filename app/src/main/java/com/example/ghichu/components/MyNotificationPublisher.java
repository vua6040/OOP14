package com.example.ghichu.components;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.example.ghichu.R;

public class MyNotificationPublisher extends BroadcastReceiver {
<<<<<<< Updated upstream
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String KEY_MESSAGE = "key_message";
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_EXPAND = "key_expand";
    public static final String KEY_SOUND = "key_sound";
    public static final String KEY_MULTIPLE = "key_multiple";
    public static final String CHANNEL_ID = "channel_id";
    public static final String APP_NAME = "NotificationApp";
=======
    public static String NOTIFICATION_ID = "notification_id";
    public static String KEY_MESSAGE = "key_message";
    public static String KEY_TITLE = "key_title";
    public static String KEY_EXPAND = "key_expand";
    public static String KEY_SOUND = "key_sound";
    public static String KEY_MULTIPLE = "key_multiple";
    public static String CHANNEL_ID = "Chanel_1";
    public static String APP_NAME = "NotificationApp";
>>>>>>> Stashed changes

    @Override
    public void onReceive(final Context context, Intent intent) {

        System.out.println("RECIVE");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
        String message = intent.getStringExtra(KEY_MESSAGE);
        String title = intent.getStringExtra(KEY_TITLE);
//        boolean isEnabledExpand = intent.getBooleanExtra(KEY_EXPAND, false);
//        boolean isEnableSound = intent.getBooleanExtra(KEY_SOUND, false);
//        boolean isEnabledMultiple = intent.getBooleanExtra(KEY_MULTIPLE, false);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add_alert_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(message));

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);

            builder.setGroup(APP_NAME);
            builder.setGroupSummary(true);

        notificationManager.notify(notificationId, builder.build());
    }
}
