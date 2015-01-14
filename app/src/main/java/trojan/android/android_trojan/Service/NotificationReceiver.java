package trojan.android.android_trojan.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Remi on 10/12/2014.
 */
public class NotificationReceiver extends Service {
    private final static String TAG = "NotificationReceiver";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "notification");
        Intent svc = new Intent(this, BackgroundService.class);
        stopService(svc);
        Toast.makeText(NotificationReceiver.this, TAG, Toast.LENGTH_SHORT).show();
        onDestroy();
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
