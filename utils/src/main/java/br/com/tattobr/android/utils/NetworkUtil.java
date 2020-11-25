package br.com.tattobr.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class NetworkUtil {
    public static String getIpAddress(Context context) {
        String ipAddressString;

        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

            // Convert little-endian to big-endianif needed
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                ipAddress = Integer.reverseBytes(ipAddress);
            }

            byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();


            try {
                ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
            } catch (UnknownHostException ex) {
                ipAddressString = null;
            }
        } catch (Throwable t) {
            ipAddressString = null;
        }

        return ipAddressString;
    }

    public static long getEstimatedTimeToTransfer(Context context, long totalSize) {
        Integer linkSpeed = 0;
        long convertToBit = totalSize * 8; // Conversão de byte para bit
        long mbps = (convertToBit / (1000000));

        /*
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            linkSpeed = wifiInfo.getLinkSpeed();
        }
        */

        linkSpeed = 30; // Será sempre um valor fixo tanto para wifi quanto para wifi direct

        return 5 + (mbps / linkSpeed);
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            // Conectado na internet
            return (networkInfo.getType() == ConnectivityManager.TYPE_WIFI); // Verifica se a conexão é WiFi
        }
        return false;
    }

    public static String getNameWifiConnected(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }
}
