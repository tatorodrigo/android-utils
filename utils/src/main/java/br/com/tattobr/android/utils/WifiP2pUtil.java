package br.com.tattobr.android.utils;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;

import java.lang.reflect.Method;

public class WifiP2pUtil {
    public static boolean enable(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel) {
        boolean success = false;
        //possível habilitar apenas antes do JB
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            try {
                Method enableP2p = wifiP2pManager.getClass().getMethod("enableP2p", WifiP2pManager.Channel.class);
                enableP2p.invoke(wifiP2pManager, channel);
                success = true;
            } catch (Exception e) {

            }
        }
        return success;
    }

    public static boolean disable(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel) {
        boolean success = false;
        //possível desabilitar apenas antes do JB
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            try {
                Method disableP2p = wifiP2pManager.getClass().getMethod("disableP2p", WifiP2pManager.Channel.class);
                disableP2p.invoke(wifiP2pManager, channel);
                success = true;
            } catch (Exception e) {

            }
        }
        return success;
    }

    public static boolean isWifiDirectSupported(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return isWifiDirectSupportedByHardwareFeature(context);
        } else {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            return wifiManager.isP2pSupported() || isWifiDirectSupportedByHardwareFeature(context);
        }
    }

    private static boolean isWifiDirectSupportedByHardwareFeature(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo info : features) {
                    if (info != null && "android.hardware.wifi.direct".equalsIgnoreCase(info.name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
