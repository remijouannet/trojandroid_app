package trojan.android.android_trojan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Jean-Laurent on 26/11/2014.
 */
public class second_activity extends Activity {
    private static final String TAG = "second_activity";
    Context context;
    private Button button21;
    private Button button22;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        button21 = (Button) findViewById(R.id.button21);
        button22 = (Button) findViewById(R.id.button22);
        button21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, GetLocation()[0]+";"+ GetLocation()[1]);
            }
        });

        button22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Screenshot();
            }
        });

    }

    public double[] GetLocation(){
        LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Location location = locManager.getLastKnownLocation(locManager.getBestProvider(new Criteria(), false));
        double[] result = new double[2];

        try{
            Log.d(TAG, location.getAltitude()+";"+ location.getLongitude());
            result[0] = location.getAltitude();
            result[1] = location.getLongitude();
        }catch (Exception ex){
            Log.d(TAG, ex.getMessage());
            result[0] = 0;
            result[1] = 0;
        }
        return result;
    }

    public void Screenshot(){


    }
}
