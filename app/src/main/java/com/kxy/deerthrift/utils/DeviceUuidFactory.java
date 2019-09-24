package com.kxy.deerthrift.utils;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * 获取uuid
 */
public class DeviceUuidFactory {
    protected static final String PREFS_DEVICE_ID = "device_id";
    //    protected static UUID uuid;
    protected static UUID uuid;

    private static DeviceUuidFactory deviceUuidFactory;
    public static DeviceUuidFactory getInstance(Context context){
        if (deviceUuidFactory == null){
            deviceUuidFactory = new DeviceUuidFactory(context);
        }
        return deviceUuidFactory;
    }

    public DeviceUuidFactory(Context context) {
        if (uuid == null) {
            synchronized (DeviceUuidFactory.class) {
                if (uuid == null) {
                    final String id = PreferencesUtils.getString(context, PREFS_DEVICE_ID, null);
                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        uuid = UUID.fromString(id);
                    } else {
                        try {
                            /*===============================================（3）获取ANDROID_ID====================================================*/
                            /*缺陷：
                             * 厂商定制系统的Bug：不同的设备可能会产生相同的ANDROID_ID：9774d56d682e549c。
                             * 厂商定制系统的Bug：有些设备返回的值为null。
                             * 设备差异：对于CDMA设备，ANDROID_ID和TelephonyManager.getDeviceId() 返回相同的值。
                             * 在Android <=2.1 or Android >=2.3的版本是可靠、稳定的，但在2.2的版本并不是100%可靠的
                             * ANDROID_ID是设备第一次启动时产生和存储的64bit的一个数，当设备被wipe（刷机）后该数重置
                             * */
                            String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

                            if (!"9774d56d682e549c".equals(androidId) && !StringUtils.isEmpty(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                            } else {
                                /*===============================================（4）Installtion ID : UUID====================================================*/
                                /*该方法无需访问设备的资源，也跟设备类型无关。
                                 * 这种方式的原理是在程序安装后第一次运行时生成一个ID，该方式和设备唯一标识不一样，不同的应用程序会产生不同的ID，同一个程序重新安装也会不同。
                                 * 所以这不是设备的唯一ID，但是可以保证每个用户的ID是不同的。可以说是用来标识每一份应用程序的唯一ID（即Installtion ID），可以用来跟踪应用的安装数量等。
                                 * */
                                String installationId = new Installation().id(context);
                                if (!StringUtils.isEmpty(installationId)){
                                    uuid = UUID.nameUUIDFromBytes(installationId.getBytes("utf8"));
                                }else {
                                    uuid = UUID.nameUUIDFromBytes(getPesudoUniqueID().getBytes());
                                }

                            }
                        } catch (UnsupportedEncodingException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        // Write the value out to the prefs file
                        PreferencesUtils.putString(context, PREFS_DEVICE_ID, uuid.toString());
                    }
                }
            }
        }
    }




    /**
     * The IMEI: 仅仅只对Android手机有效
     * 采用此种方法，需要在AndroidManifest.xml中加入一个许可：android.permission.READ_PHONE_STATE，并且用
     * 户应当允许安装此应用。作为手机来讲，IMEI是唯一的，它应该类似于 359881030314356（除非你有一个没有量产的手
     * 机（水货）它可能有无效的IMEI，如：0000000000000）。
     *
     * @return imei
     */
    public String getIMEI(Context mContext) {
        TelephonyManager TelephonyMgr = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String szImei = TelephonyMgr.getDeviceId();
        return szImei;
    }

    /**
     * Pseudo-Unique ID, 这个在任何Android手机中都有效
     * 有一些特殊的情况，一些如平板电脑的设置没有通话功能，或者你不愿加入READ_PHONE_STATE许可。而你仍然想获得唯
     * 一序列号之类的东西。这时你可以通过取出ROM版本、制造商、CPU型号、以及其他硬件信息来实现这一点。这样计算出
     * 来的ID不是唯一的（因为如果两个手机应用了同样的硬件以及Rom 镜像）。但应当明白的是，出现类似情况的可能性基
     * 本可以忽略。大多数的Build成员都是字符串形式的，我们只取他们的长度信息。我们取到13个数字，并在前面加上“35
     * ”。这样这个ID看起来就和15位IMEI一样了。
     *
     * @return PesudoUniqueID
     */
    public String getPesudoUniqueID() {
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits
        return m_szDevIDShort;
    }

    /**
     * The Android ID
     * 通常被认为不可信，因为它有时为null。开发文档中说明了：这个ID会改变如果进行了出厂设置。并且，如果某个
     * Andorid手机被Root过的话，这个ID也可以被任意改变。无需任何许可。
     *
     * @return AndroidID
     */
    public String getAndroidID(Context mContext) {
        String m_szAndroidID = Secure.getString(mContext.getContentResolver(),
                Secure.ANDROID_ID);
        return m_szAndroidID;
    }

    /**
     * The WLAN MAC Address string
     * 是另一个唯一ID。但是你需要为你的工程加入android.permission.ACCESS_WIFI_STATE 权限，否则这个地址会为
     * null。Returns: 00:11:22:33:44:55 (这不是一个真实的地址。而且这个地址能轻易地被伪造。).WLan不必打开，
     * 就可读取些值。
     *
     * @return m_szWLANMAC
     */
    public String getWLANMACAddress(Context mContext) {
        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        return m_szWLANMAC;
    }

    /**
     * 只在有蓝牙的设备上运行。并且要加入android.permission.BLUETOOTH 权限.Returns: 43:25:78:50:93:38 .
     * 蓝牙没有必要打开，也能读取。
     *
     * @return m_szBTMAC
     */
    public String getBTMACAddress() {
        BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String m_szBTMAC = m_BluetoothAdapter.getAddress();

        return m_szBTMAC;
    }

    /**
     * Combined Device ID  getPesudoUniqueID=358616696646247=====getWLANMACAddress=02:00:00:00:00:00======getBTMACAddress=02:00:00:00:00:00
     * 综上所述，我们一共有五种方式取得设备的唯一标识。它们中的一些可能会返回null，或者由于硬件缺失、权限问题等
     * 获取失败。但你总能获得至少一个能用。所以，最好的方法就是通过拼接，或者拼接后的计算出的MD5值来产生一个结果。
     * 通过算法，可产生32位的16进制数据:9DDDF85AFF0A87974CE4541BD94D5F55
     *
     * @return
     */
    public String getUniqueID(Context mContext) {
//        String m_szLongID = getIMEI() + getPesudoUniqueID()
//                + getAndroidID() + getWLANMACAddress() + getBTMACAddress();
        String m_szLongID = getPesudoUniqueID() + getWLANMACAddress(mContext) + getBTMACAddress();
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID;
    }




    public UUID getDeviceUuid() {
        return uuid;
    }


    /**这种方式是通过在程序安装后第一次运行后生成一个ID实现的
     * 但该方式跟设备唯一标识不一样，不同的应用程序会产生不同的ID，同一个程序重新安装也会不同。所以这不是设备的唯一ID，但是可以保证每个用户的ID是不同的。
     * 因此经常用来标识在某个应用中的唯一ID（即Installtion ID），或者跟踪应用的安装数量。
     * 很幸运的，Google Developer Blog提供了这样的一个框架：*/
    class Installation {
        private String sID = null;
        private final String INSTALLATION = "INSTALLATION";

        public synchronized String id(Context context) {
            if (sID == null) {
                File installation = new File(context.getFilesDir(), INSTALLATION);
                try {
                    if (!installation.exists())
                    {
                        writeInstallationFile(installation);
                    }
                    sID = readInstallationFile(installation);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return sID;
        }

        private String readInstallationFile(File installation) throws IOException {
            RandomAccessFile f = new RandomAccessFile(installation, "r");
            byte[] bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            f.close();
            return new String(bytes);
        }

        private void writeInstallationFile(File installation) throws IOException {
            FileOutputStream out = new FileOutputStream(installation);
            String id = UUID.randomUUID().toString();
            out.write(id.getBytes());
            out.close();
        }
    }
}