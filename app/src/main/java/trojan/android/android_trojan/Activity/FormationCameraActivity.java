package trojan.android.android_trojan.Activity;

import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by hoodlums on 28/01/15.
 */
public class FormationCameraActivity extends Activity implements SurfaceHolder.Callback

        public void surfaceChanged(SurfaceHolder holder, int format, int width,int height)
        public void surfaceCreated(SurfaceHolder holder)
        public void surfaceDestroyed(SurfaceHolder holder)

private Camera camera;
private SurfaceView surfaceCamera;
private Boolean isPreview;
