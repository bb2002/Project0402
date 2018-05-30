package kr.saintdev.project0402.models.components.libs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 5252b on 2018-03-26.
 */

public class NetStatusManager {
    public static boolean isNetworkEnable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo != null) {
            return true;
        } else {
            return false;
        }
    }
}
