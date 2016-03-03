package tinker.unityplugin;

import android.widget.Toast;
import android.app.Activity;

import com.unity3d.player.UnityPlayer;
/**
 * Created by sonny.kurniawan on 2016/03/01.
 */
public class NativePlugin {
    public static void showToast(final String message) {
        final Activity activity = UnityPlayer.currentActivity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
