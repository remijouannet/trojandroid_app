package trojan.android.android_trojan;

/**
 * Created by Remi on 26/11/2014.
 */
public class Tools {


    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
