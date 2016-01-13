package br.com.tattobr.android.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppPackageUtil {
    // Retorna a versão do aplicativo
    public static String getVersionName(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        String packageName = activity.getPackageName();
        String versionName;
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "1.0";
            // Caso ocarra uma excessão, será retornado a versão 1.0
        }
        return versionName;
    }
}