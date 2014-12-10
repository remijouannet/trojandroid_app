package trojan.android.android_trojan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Remi on 10/12/2014.
 */
public class NotificationReceiver extends Activity {
    private final static String TAG = "NotificationReceiver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "notification");
        Intent svc = new Intent(this, BackgroundService.class);
        stopService(svc);
        finish();
    }
}
