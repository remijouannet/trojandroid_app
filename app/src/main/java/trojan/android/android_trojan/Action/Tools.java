package trojan.android.android_trojan.Action;


import android.app.ActivityManager;
import android.content.Context;

public class Tools {


    public static void sleep(long time){
        try {
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
}
