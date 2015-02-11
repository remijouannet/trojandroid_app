package trojan.android.android_trojan.Action;


import android.app.ActivityManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class Tools {
    private static final String TAG = Tools.class.getSimpleName();

    public static void sleep(long time){
        try {
            Log.d(TAG, String.valueOf(time));
            Thread.sleep(time);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isScreenOn(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= 20){
            if (powerManager.isInteractive()){
                return true;
            }else {
                return false;
            }
        }else if (android.os.Build.VERSION.SDK_INT < 20){
            if (powerManager.isScreenOn()){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }
}
