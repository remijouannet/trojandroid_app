package trojan.android.android_trojan.Action;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends Service {
    private final static String TAG = "NotificationReceiver";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        Intent svc = new Intent(this, BackgroundService.class);
        stopService(svc);
        Toast.makeText(NotificationService.this, TAG, Toast.LENGTH_SHORT).show();
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

}
