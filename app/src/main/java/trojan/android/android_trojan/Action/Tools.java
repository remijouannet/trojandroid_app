package trojan.android.android_trojan.Action;


public class Tools {


    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
