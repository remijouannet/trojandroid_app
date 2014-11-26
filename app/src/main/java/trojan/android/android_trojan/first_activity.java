package trojan.android.android_trojan;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by Jean-Laurent on 26/11/2014.
 */
public class first_activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);
        /*
        Intent sms = new Intent(Intent.ACTION_SENDTO,
                Uri.parse("smsto:0628470850");
                sms.putExtra("sms_body", "Test Message !");
                startActivity(sms);
        )
        */

    }
}

