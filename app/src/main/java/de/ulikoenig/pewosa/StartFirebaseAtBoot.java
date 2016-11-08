package de.ulikoenig.pewosa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Start the service when the device boots.
 *
 * @author vikrum
 *
 */
public class StartFirebaseAtBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("pewosa","StartFirebaseAtBoot.onReceive("+context.toString()+","+intent.toString()+")");
        android.os.Debug.waitForDebugger();
        Log.d("pewosa","StartFirebaseAtBoot.onReceive android.os.Debug.waitForDebugger done");
        Intent firebaseService = new Intent(context, MyFirebaseInstanceIDService.class);
        context.startService(firebaseService);
        context.startService(new Intent(context,MyFirebaseMessagingService.class));
        Log.d("pewosa","StartFirebaseAtBoot.onReceive done");
    }
}