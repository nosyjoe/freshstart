package de.philippengel.android.freshstart.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

/**
 * Created by philipp on 13.03.15.
 */
public class Tools {

    private static float scale = -1;

    public static int toDDP(int dip) {
        if (scale < 0) {
            scale = Resources.getSystem().getDisplayMetrics().density;
        }

        return (int) (dip * scale + 0.5f);
    }

    public static boolean isDebuggable(Context context) {
        boolean debug = false;
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null && packageInfo.applicationInfo != null) {
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                debug = true;
            } else
                debug = false;
        }
        return debug;
    }

}
