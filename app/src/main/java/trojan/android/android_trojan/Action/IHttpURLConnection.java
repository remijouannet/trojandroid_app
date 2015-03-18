package trojan.android.android_trojan.Action;

import java.net.URL;

/**
 * Created by hoodlums on 18/03/15.
 */
public interface IHttpURLConnection {
    public String getHttp(URL url, String auth);
    public String postHttp(URL url, String data, String auth);
    public String postHttpJSON(URL url, String json, String auth);
    public String postHttpFile(URL url, String path, String auth);
}
