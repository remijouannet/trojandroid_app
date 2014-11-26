package trojan.android.android_trojan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Jean-Laurent on 26/11/2014.
 */
public class second_activity extends Activity {
    private static final String TAG = "second_activity";
    Context context;
    private Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        context = getApplicationContext();
        button1 = (Button) findViewById(R.id.b1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetLocation();
            }
        });

    }

    public void GetLocation(){
        LocationManager locManager = (LocationManager)getSystemService(context.LOCATION_SERVICE);
        Location location = locManager.getLastKnownLocation(locManager.getBestProvider(new Criteria(), false));
        Log.d(TAG, location.getAltitude()+";"+ location.getLongitude());
    }


}
