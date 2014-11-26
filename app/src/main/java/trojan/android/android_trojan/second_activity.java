package trojan.android.android_trojan;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Jean-Laurent on 26/11/2014.
 */
public class second_activity extends Activity {

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        context = getApplicationContext();

    }

    public void GetLocation(){
        LocationManager locManager = (LocationManager)getSystemService(context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false););
    }


}
