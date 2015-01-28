package trojan.android.android_trojan.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class ActionService extends Service {
    private final static String TAG = "ActionService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
