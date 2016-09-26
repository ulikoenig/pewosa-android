package de.ulikoenig.pewosa;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


        if ((remoteMessage.getData() != null) && (remoteMessage.getData().get("msgtype") != null)) {

            if (remoteMessage.getData().get("msgtype").equals("newFirstReleaseRequest")) {
                int id = extractID(remoteMessage);
                String title = remoteMessage.getData().get("pmTitle");
                if ((id > 0) && (title != null)) {
                    newFirstReleaseRequest(title, id);
                }
            }
            if (remoteMessage.getData().get("msgtype").equals("removeFirstReleaseRequest")) {
                int id = extractID(remoteMessage);
                if (id > 0) {
                    removeReleaseRequest(id);
                }
            }

            //Ids von 2. Freigabe *(-1) um Racecondition beim löschen der alten notification zu vermeiden.

            if (remoteMessage.getData().get("msgtype").equals("newSecondReleaseRequest")) {
                int id = extractID(remoteMessage);
                String title = remoteMessage.getData().get("pmTitle");
                String firstReleaseingUsername = remoteMessage.getData().get("firstReleaseingUsername");
                if ((id > 0) && (title != null) && (firstReleaseingUsername != null)) {
                    newSecondReleaseRequest(title, id, firstReleaseingUsername);
                }
            }
            if (remoteMessage.getData().get("msgtype").equals("removeSecondReleaseRequest")) {
                int id = Integer.parseInt(remoteMessage.getData().get("id"));
                removeReleaseRequest(id * (-1));
            }
        }


    }

    private int extractID(RemoteMessage remoteMessage) {
        String sid = remoteMessage.getData().get("id");
        int id = 0;
        if (sid != null) {
            id = Integer.parseInt(sid);
        }
        return id;
    }


    private void newFirstReleaseRequest(String titel, int id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("NotificationMessageType", "newFirstReleaseRequest");
        intent.putExtra("NotificationMessageID", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        long[] vibrate = {0, 300, 1000, 150, 1000, 300};

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("Bitte um 1. Freigabe:")
                .setContentText(titel)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setVibrate(vibrate);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notificationBuilder.build());
    }


    private void newSecondReleaseRequest(String titel, int id, String firstReleaseingUsername) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("NotificationMessageType", "newSecondReleaseRequest");
        intent.putExtra("NotificationMessageID", id);
        intent.putExtra("firstReleaseingUsername", firstReleaseingUsername);
        long[] vibrate = {0, 150, 1000, 300, 1000, 150};

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id * (-1), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("Bitte um 2. Freigabe:")
                .setContentText(titel)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setVibrate(vibrate);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id * (-1), notificationBuilder.build());
    }

    private void removeReleaseRequest(int id) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}