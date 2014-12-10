package trojan.android.android_trojan;

import android.app.Activity;
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
    }
}
