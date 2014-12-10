package trojan.android.android_trojan;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Remi on 10/12/2014.
 */
public class BackgroundService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
