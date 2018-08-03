package com.frogshealth.lan.transmission.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**********************************************************************
 * WIFI工具类。
 *
 * @类名 WifiUtils
 * @包名 com.frogshealth.lan.transmission.utils
 * @author yuanjf
 * @创建日期 18/8/2
 ***********************************************************************/
public class WifiUtils {

    /**
     * 获取内网IP
     *
     * @return 内网IP
     */
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
