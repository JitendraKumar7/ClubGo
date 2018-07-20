package in.clubgo.app.listener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.defuzed.clubgo.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import in.clubgo.app.home.ActivityHome;
import in.clubgo.app.utility.Constants;

/**
 * Created by Jitendra Soam on 11/8/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional

        //Log.d(TAG, "From: " + remoteMessage.getFrom());
        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody() + " J");

        //Calling method to generate notification
        //sendNotification(remoteMessage.getNotification().getBody());

        Log.d(TAG, "title: " + remoteMessage.getData().get("title"));
        Log.d(TAG, "message: " + remoteMessage.getData().get("body"));
        Log.d(TAG, "message: " + remoteMessage.getData().get("image"));
        showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("image"));
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void showNotification(String title, String message, final String image) {

        Intent intent = new Intent(this, ActivityHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Default stuff; making and showing notification
        final Context context = getApplicationContext();
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher) // Needed for the notification to work/show!!
                .setContentTitle(title)
                .setColor(this.getResources().getColor(R.color.transparent))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setStyle(new NotificationCompat.BigPictureStyle())
                .build();

        final int notifId = 1337;
        notificationManager.notify(notifId, notification);

        // Get RemoteView and id's needed
        final RemoteViews contentView = notification.contentView;
        final int iconId = android.R.id.icon;

        // Uncomment for BigPictureStyle, Requires API 16!
        final RemoteViews bigContentView = notification.bigContentView;
        final int bigIconId = getResources().getIdentifier("android:id/big_picture", null, null);

        // Use Picasso with RemoteViews to load image into a notification
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Picasso.with(context)
                        .load(Constants.BaseImage + image)
                        .resize(600, 400)
                        .into(bigContentView, bigIconId, notifId, notification);
            }
        });

        /*Picasso.with(getApplicationContext())
                .load("http://i.stack.imgur.com/CE5lz.png")
                .into(bigContentView, bigIconId, notifId, notification);*/
    }

    private void sendNotification(String messageBody) {

        Intent intent = new Intent(this, ActivityHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle("ClubGo")
                .setContentText(messageBody)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.color.transparent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            builder.setColor(this.getResources().getColor(R.color.colorPrimary))
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(999, builder.build());
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, ActivityHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSound(defaultSoundUri)
                .setContentText(messageBody)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(android.R.color.transparent)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(777, notificationBuilder.build());
    }


}
