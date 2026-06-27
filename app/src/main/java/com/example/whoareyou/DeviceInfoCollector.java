package com.example.whoareyou;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.UiModeManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;

public class DeviceInfoCollector {

    private Context context;

    public DeviceInfoCollector(Context context) {
        this.context = context;
    }

    public List<InfoItem> collectAllInfo() {
        List<InfoItem> infoList = new ArrayList<>();
        infoList.addAll(getDeviceHardwareInfo());
        infoList.addAll(getSystemDeepInfo());
        infoList.addAll(getNetworkDeepInfo());
        infoList.addAll(getStorageInfo());
        infoList.addAll(getDisplayInfo());
        infoList.addAll(getUserBehaviorInfo());
        infoList.addAll(getAppEnvironmentInfo());
        infoList.addAll(getTimeLocationInfo());
        infoList.addAll(getSensorInfo());
        infoList.addAll(getSecurityInfo());
        return infoList;
    }

    private List<InfoItem> getDeviceHardwareInfo() {
        List<InfoItem> items = new ArrayList<>();

        items.add(new InfoItem(
            "Device Brand",
            Build.MANUFACTURER,
            "Device manufacturer identification. Used by advertisers for device classification and market analysis; rogue apps may exploit model-specific vulnerabilities",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "Device Model",
            Build.MODEL,
            "Exact device model. Used for device value assessment, consumer capability profiling; some older models have known vulnerabilities",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "Device Codename",
            Build.DEVICE,
            "Internal device codename. Used for precise device identification; attackers may search for specific device vulnerabilities",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "Product Name",
            Build.PRODUCT,
            "Commercial product name. Used for market segmentation and user group analysis",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "Hardware Platform",
            Build.HARDWARE,
            "Low-level hardware identifier. Used for performance optimization and driver compatibility analysis",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "Device ID (Android ID)",
            getAndroidId(),
            "Core privacy data! Used for cross-app user tracking, ad attribution, and account linking. Changes only after factory reset",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_DANGER
        ));

        items.add(new InfoItem(
            "CPU Cores",
            String.valueOf(Runtime.getRuntime().availableProcessors()),
            "Computational capability assessment. Used to determine device performance level, game compatibility; infers user consumption level",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "CPU Architecture",
            Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "Unknown",
            "Processor architecture. Used to select correct malicious code version; determines security feature support; rogue apps may target specific architectures",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);

            items.add(new InfoItem(
                "Total Memory",
                formatSize(memoryInfo.totalMem),
                "Device memory capacity. Used for performance grading, app recommendation; infers device price range and user spending ability",
                InfoItem.CATEGORY_DEVICE,
                InfoItem.LEVEL_INFO
            ));

            items.add(new InfoItem(
                "Available Memory",
                formatSize(memoryInfo.availMem),
                "Current available memory. Used to determine device load status; infers user habits (multi-tasking or not)",
                InfoItem.CATEGORY_DEVICE,
                InfoItem.LEVEL_INFO
            ));
        }

        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent != null) {
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

            float batteryPct = level * 100 / (float)scale;
            String chargingStatus = status == BatteryManager.BATTERY_STATUS_CHARGING ? "Charging" :
                                   status == BatteryManager.BATTERY_STATUS_FULL ? "Full" : "Discharging";
            String plugType = plugged == BatteryManager.BATTERY_PLUGGED_AC ? "AC" :
                             plugged == BatteryManager.BATTERY_PLUGGED_USB ? "USB" :
                             plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS ? "Wireless" : "Not Connected";

            items.add(new InfoItem(
                "Battery Level",
                String.format("%.0f%% (%s)", batteryPct, chargingStatus),
                "Battery status. Used to infer usage scenarios (charging may mean at home/office); advertisers use for push timing",
                InfoItem.CATEGORY_DEVICE,
                InfoItem.LEVEL_INFO
            ));

            items.add(new InfoItem(
                "Charging Method",
                plugType,
                "Charging type. Infers user location: USB may indicate at computer, AC may indicate at home or office",
                InfoItem.CATEGORY_DEVICE,
                InfoItem.LEVEL_INFO
            ));
        }

        return items;
    }

    private List<InfoItem> getSystemDeepInfo() {
        List<InfoItem> items = new ArrayList<>();

        items.add(new InfoItem(
            "Android Version",
            Build.VERSION.RELEASE,
            "System version. Used for compatibility checking, feature limitation; older versions may have known security vulnerabilities; rogue apps may exploit old version vulnerabilities",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "SDK Version",
            String.valueOf(Build.VERSION.SDK_INT),
            "API level. Precisely determines available features and security; low SDK means missing new security mechanisms",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "Security Patch Level",
            Build.VERSION.SECURITY_PATCH,
            "Important security indicator! Determines if device has fixed known vulnerabilities. Rogue apps may exploit unfixed vulnerabilities",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        items.add(new InfoItem(
            "Baseband Version",
            Build.getRadioVersion(),
            "Communication module version. Specific versions may have baseband vulnerabilities that can be remotely exploited",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        items.add(new InfoItem(
            "Kernel Version",
            System.getProperty("os.version", "Unknown"),
            "Linux kernel version. Used to determine kernel vulnerability availability; old kernels may have privilege escalation vulnerabilities; rogue apps may exploit kernel vulnerabilities for higher permissions",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        items.add(new InfoItem(
            "Build Fingerprint",
            Build.FINGERPRINT,
            "Complete system build identifier. Used for precise system source identification, determining if official ROM; traces system tampering history",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "Bootloader Version",
            Build.BOOTLOADER,
            "Bootloader version. Determines if device can be unlocked, if rooted",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "System Uptime",
            formatUptime(SystemClock.elapsedRealtime()),
            "Device running duration. Infers user habits: short time may mean just powered on, long time may mean rare restarts",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "System Language",
            Locale.getDefault().getDisplayLanguage() + " (" + Locale.getDefault().getLanguage() + ")",
            "User language preference. Used for content recommendation, regional services; infers possible nationality or residence",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "System Region",
            Locale.getDefault().getCountry(),
            "Country/region code. Used for regional restrictions, content moderation; infers user's country",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        items.add(new InfoItem(
            "Timezone",
            TimeZone.getDefault().getID() + " (UTC" + (TimeZone.getDefault().getRawOffset() >= 0 ? "+" : "") +
            (TimeZone.getDefault().getRawOffset() / 3600000) + ")",
            "Important location inference basis! Combined with other info can infer approximate user location",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        return items;
    }

    private List<InfoItem> getNetworkDeepInfo() {
        List<InfoItem> items = new ArrayList<>();

        String ipAddress = getIPAddress();
        items.add(new InfoItem(
            "IP Address",
            ipAddress,
            "Network location identifier! Can be geolocated to city level via IP database; used for regional restrictions and risk control",
            InfoItem.CATEGORY_NETWORK,
            InfoItem.LEVEL_DANGER
        ));

        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
            .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String ssid = wifiInfo.getSSID();
                if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    ssid = ssid.substring(1, ssid.length() - 1);
                }

                items.add(new InfoItem(
                    "WiFi Name (SSID)",
                    ssid,
                    "Important privacy data! Can infer user location type (home/office/mall/hotel); used for location fingerprinting",
                    InfoItem.CATEGORY_NETWORK,
                    InfoItem.LEVEL_DANGER
                ));

                items.add(new InfoItem(
                    "WiFi BSSID",
                    wifiInfo.getBSSID(),
                    "Router MAC address. Used for precise location (with WiFi location database); rogue apps can track user movement",
                    InfoItem.CATEGORY_NETWORK,
                    InfoItem.LEVEL_DANGER
                ));

                items.add(new InfoItem(
                    "WiFi Signal Strength",
                    wifiInfo.getRssi() + " dBm",
                    "Signal strength. Used for indoor positioning, distance estimation from router; infers user position in building",
                    InfoItem.CATEGORY_NETWORK,
                    InfoItem.LEVEL_INFO
                ));

                items.add(new InfoItem(
                    "WiFi Frequency",
                    wifiInfo.getFrequency() + " MHz",
                    "WiFi frequency (2.4G/5G). Used to determine network environment quality; infers router type and usage scenario",
                    InfoItem.CATEGORY_NETWORK,
                    InfoItem.LEVEL_INFO
                ));
            }
        }

        String networkType = getNetworkType();
        items.add(new InfoItem(
            "Network Type",
            networkType,
            "Network connection method. Used for content adaptation, traffic control; infers usage scenario (WiFi may mean at home/office)",
            InfoItem.CATEGORY_NETWORK,
            InfoItem.LEVEL_INFO
        ));

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            String networkOperatorName = tm.getNetworkOperatorName();
            String simOperator = tm.getSimOperator();
            String networkCountryIso = tm.getNetworkCountryIso();

            items.add(new InfoItem(
                "Network Operator",
                networkOperatorName != null ? networkOperatorName : "Unknown",
                "Mobile network operator. Used for carrier partnerships; infers user's country/region",
                InfoItem.CATEGORY_NETWORK,
                InfoItem.LEVEL_INFO
            ));

            items.add(new InfoItem(
                "Operator Country Code",
                networkCountryIso != null ? networkCountryIso.toUpperCase() : "Unknown",
                "Operator's country. Infers user's country/region",
                InfoItem.CATEGORY_NETWORK,
                InfoItem.LEVEL_WARNING
            ));
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            items.add(new InfoItem(
                "Bluetooth Status",
                "Enabled",
                "Bluetooth enabled status. Used for nearby device discovery; infers possible wearable device usage",
                InfoItem.CATEGORY_NETWORK,
                InfoItem.LEVEL_INFO
            ));
        }

        return items;
    }

    private List<InfoItem> getStorageInfo() {
        List<InfoItem> items = new ArrayList<>();

        StatFs internalStats = new StatFs(Environment.getDataDirectory().getPath());
        long internalTotal = internalStats.getBlockCountLong() * internalStats.getBlockSizeLong();
        long internalAvailable = internalStats.getAvailableBlocksLong() * internalStats.getBlockSizeLong();

        items.add(new InfoItem(
            "Internal Storage Total",
            formatSize(internalTotal),
            "Device storage capacity. Used to determine device tier; infers user spending ability (large storage = premium device)",
            InfoItem.CATEGORY_STORAGE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "Internal Storage Available",
            formatSize(internalAvailable) + " (" + (internalAvailable * 100 / internalTotal) + "%)",
            "Available storage space. Infers user habits: full storage may mean heavy photo/download usage; empty storage may indicate new device",
            InfoItem.CATEGORY_STORAGE,
            InfoItem.LEVEL_INFO
        ));

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalPath = Environment.getExternalStorageDirectory();
            if (externalPath != null) {
                StatFs externalStats = new StatFs(externalPath.getPath());
                long externalTotal = externalStats.getBlockCountLong() * externalStats.getBlockSizeLong();
                long externalAvailable = externalStats.getAvailableBlocksLong() * externalStats.getBlockSizeLong();

                items.add(new InfoItem(
                    "External Storage Total",
                    formatSize(externalTotal),
                    "SD card capacity. Determines if expandable storage exists; infers user storage needs",
                    InfoItem.CATEGORY_STORAGE,
                    InfoItem.LEVEL_INFO
                ));
            }
        }

        return items;
    }

    private List<InfoItem> getDisplayInfo() {
        List<InfoItem> items = new ArrayList<>();

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getRealMetrics(metrics);
        }

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        float density = metrics.density;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;
        float screenWidthInches = width / xdpi;
        float screenHeightInches = height / ydpi;
        double screenDiagonal = Math.sqrt(Math.pow(screenWidthInches, 2) + Math.pow(screenHeightInches, 2));

        items.add(new InfoItem(
            "Screen Resolution",
            width + " x " + height,
            "Display resolution. Used for UI adaptation, image resource selection; infers device tier",
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "Screen Size",
            String.format("%.1f inches", screenDiagonal),
            "Physical screen size. Used to determine device type (phone/tablet); infers usage scenario",
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "Screen Density",
            String.format("%.1f (%s)", density, getDensityName(density)),
            "Pixel density. Used for resource selection, performance optimization; high density usually means premium device",
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "Refresh Rate",
            windowManager != null ? windowManager.getDefaultDisplay().getRefreshRate() + " Hz" : "Unknown",
            "Screen refresh rate. High refresh rate = premium device; used for animation smoothness judgment",
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        int orientation = context.getResources().getConfiguration().orientation;
        String orientationStr = orientation == Configuration.ORIENTATION_PORTRAIT ? "Portrait" :
                               orientation == Configuration.ORIENTATION_LANDSCAPE ? "Landscape" : "Unknown";
        items.add(new InfoItem(
            "Current Orientation",
            orientationStr,
            "Current screen orientation. Used to determine user activity (landscape may indicate watching video/gaming)",
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        return items;
    }

    private List<InfoItem> getUserBehaviorInfo() {
        List<InfoItem> items = new ArrayList<>();

        float fontScale = context.getResources().getConfiguration().fontScale;
        items.add(new InfoItem(
            "Font Scale",
            String.format("%.1fx", fontScale),
            "User font preference. Greater than 1.0 may indicate visually impaired or older users; used for UI adaptation",
            InfoItem.CATEGORY_APP,
            InfoItem.LEVEL_INFO
        ));

        try {
            float animationScale = Settings.Global.getFloat(
                context.getContentResolver(),
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            );
            items.add(new InfoItem(
                "Animation Scale",
                String.format("%.1fx", animationScale),
                "Animation speed setting. 0 means animations disabled, possibly developer or performance-focused user",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        } catch (Exception e) {}

        try {
            int screenOffTimeout = Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT,
                30000
            );
            items.add(new InfoItem(
                "Screen Timeout",
                (screenOffTimeout / 1000) + " seconds",
                "Auto lock time. Infers user habits: long time = frequent phone use; short time = power saving mode",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        } catch (Exception e) {}

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int ringerMode = audioManager.getRingerMode();
            String modeStr = ringerMode == AudioManager.RINGER_MODE_NORMAL ? "Normal" :
                            ringerMode == AudioManager.RINGER_MODE_SILENT ? "Silent" :
                            ringerMode == AudioManager.RINGER_MODE_VIBRATE ? "Vibrate" : "Unknown";
            items.add(new InfoItem(
                "Ringer Mode",
                modeStr,
                "Sound mode. Infers current scenario: silent may indicate meeting/sleep; vibrate may indicate public place",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        }

        try {
            int brightnessMode = Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE
            );
            items.add(new InfoItem(
                "Brightness Mode",
                brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ? "Auto" : "Manual",
                "Brightness adjustment method. Auto brightness may indicate convenience-focused user; manual may indicate precise control preference",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        } catch (Exception e) {}

        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager != null) {
            int nightMode = uiModeManager.getNightMode();
            String nightModeStr = nightMode == UiModeManager.MODE_NIGHT_YES ? "On" :
                                 nightMode == UiModeManager.MODE_NIGHT_NO ? "Off" : "Auto";
            items.add(new InfoItem(
                "Dark Mode",
                nightModeStr,
                "Dark mode status. Enabled may indicate night use or dark theme preference",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        }

        return items;
    }

    private List<InfoItem> getAppEnvironmentInfo() {
        List<InfoItem> items = new ArrayList<>();

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        items.add(new InfoItem(
            "Installed Apps Count",
            String.valueOf(installedApps.size()),
            "App installation count. Infers user type: many apps = heavy user; few apps = light user or new device",
            InfoItem.CATEGORY_APP,
            InfoItem.LEVEL_INFO
        ));

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("http://"));
        ResolveInfo resolveInfo = pm.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo != null && resolveInfo.activityInfo != null) {
            String browserName = resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
            items.add(new InfoItem(
                "Default Browser",
                browserName,
                "Browser preference. Used to identify user group characteristics; some browsers have specific user profiles",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        }

        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo launcherInfo = pm.resolveActivity(launcherIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (launcherInfo != null && launcherInfo.activityInfo != null) {
            String launcherName = launcherInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
            items.add(new InfoItem(
                "Default Launcher",
                launcherName,
                "Home launcher. Third-party launcher indicates customization preference; infers technical ability",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        }

        String userAgent = System.getProperty("http.agent");
        items.add(new InfoItem(
            "System User Agent",
            userAgent != null ? userAgent : "Unknown",
            "Browser identification string. Contains device model, system version, browser version; used by servers to identify devices",
            InfoItem.CATEGORY_APP,
            InfoItem.LEVEL_WARNING
        ));

        return items;
    }

    private List<InfoItem> getTimeLocationInfo() {
        List<InfoItem> items = new ArrayList<>();

        long uptimeMillis = SystemClock.elapsedRealtime();
        items.add(new InfoItem(
            "System Runtime",
            formatUptime(uptimeMillis),
            "Device running duration. Short time = just powered on/restarted; long time = stable operation",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        boolean is24Hour = android.text.format.DateFormat.is24HourFormat(context);
        items.add(new InfoItem(
            "Time Format",
            is24Hour ? "24-hour" : "12-hour",
            "Time display preference. 24-hour mostly used in Europe/Asia; 12-hour mostly used in US",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        items.add(new InfoItem(
            "Current Time",
            sdf.format(new Date()),
            "Device current time. Used to determine user active hours; combined with timezone can infer approximate location",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        String dateFormat = Settings.System.getString(
            context.getContentResolver(),
            Settings.System.DATE_FORMAT
        );
        items.add(new InfoItem(
            "Date Format",
            dateFormat != null ? dateFormat : "System Default",
            "Date display format. Different regions have different preferences; helps determine user's region",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        return items;
    }

    private String getAndroidId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Not available";
    }

    private String getNetworkType() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                int type = activeNetwork.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    return "WiFi";
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    return "Mobile (" + activeNetwork.getSubtypeName() + ")";
                }
            }
        }
        return "Not connected";
    }

    private String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    private String formatUptime(long millis) {
        long seconds = millis / 1000;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        sb.append(minutes).append("m");
        return sb.toString();
    }

    private String getDensityName(float density) {
        if (density <= 0.75f) return "ldpi";
        if (density <= 1.0f) return "mdpi";
        if (density <= 1.5f) return "hdpi";
        if (density <= 2.0f) return "xhdpi";
        if (density <= 3.0f) return "xxhdpi";
        if (density <= 4.0f) return "xxxhdpi";
        return "Ultra HD";
    }

    private List<InfoItem> getSensorInfo() {
        List<InfoItem> items = new ArrayList<>();

        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

            items.add(new InfoItem(
                "Sensor Count",
                String.valueOf(sensors.size()),
                "Total device sensors. More sensors mean more data collection capability; some sensors can be used for user behavior tracking",
                InfoItem.CATEGORY_SENSOR,
                InfoItem.LEVEL_INFO
            ));

            boolean hasAccelerometer = false;
            boolean hasGyroscope = false;
            boolean hasMagnetic = false;
            boolean hasLight = false;
            boolean hasProximity = false;
            boolean hasStepCounter = false;

            StringBuilder sensorTypes = new StringBuilder();
            for (Sensor sensor : sensors) {
                sensorTypes.append(sensor.getName()).append("; ");
                switch (sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        hasAccelerometer = true;
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        hasGyroscope = true;
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        hasMagnetic = true;
                        break;
                    case Sensor.TYPE_LIGHT:
                        hasLight = true;
                        break;
                    case Sensor.TYPE_PROXIMITY:
                        hasProximity = true;
                        break;
                    case Sensor.TYPE_STEP_COUNTER:
                        hasStepCounter = true;
                        break;
                }
            }

            if (hasAccelerometer) {
                items.add(new InfoItem(
                    "Accelerometer",
                    "Supported",
                    "Detects device motion. Used for step counting, gesture recognition, user behavior analysis; commonly used in games and fitness apps",
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_INFO
                ));
            }

            if (hasGyroscope) {
                items.add(new InfoItem(
                    "Gyroscope",
                    "Supported",
                    "Detects device rotation. Used in AR/VR applications, precise motion tracking; can infer user activity type",
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_INFO
                ));
            }

            if (hasMagnetic) {
                items.add(new InfoItem(
                    "Magnetic Sensor",
                    "Supported",
                    "Detects magnetic field. Used for electronic compass, positioning assistance; can infer user orientation and position",
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_INFO
                ));
            }

            if (hasLight) {
                items.add(new InfoItem(
                    "Light Sensor",
                    "Supported",
                    "Detects ambient light intensity. Used for auto brightness adjustment; can infer user environment (indoor/outdoor)",
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_WARNING
                ));
            }

            if (hasProximity) {
                items.add(new InfoItem(
                    "Proximity Sensor",
                    "Supported",
                    "Detects object distance. Used to turn off screen during calls; can infer user habits and scenarios",
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_INFO
                ));
            }

            if (hasStepCounter) {
                items.add(new InfoItem(
                    "Step Counter",
                    "Supported",
                    "Counts steps. Used for health tracking; can infer user exercise amount and lifestyle",
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_WARNING
                ));
            }

            items.add(new InfoItem(
                "All Sensors List",
                sensorTypes.length() > 0 ? sensorTypes.substring(0, sensorTypes.length() - 2) : "None",
                "All sensors supported by device. Used for app feature adaptation; rogue apps can leverage sensor data for user behavior analysis and tracking",
                InfoItem.CATEGORY_SENSOR,
                InfoItem.LEVEL_INFO
            ));
        }

        return items;
    }

    private List<InfoItem> getSecurityInfo() {
        List<InfoItem> items = new ArrayList<>();

        boolean isRooted = checkRooted();
        items.add(new InfoItem(
            "Root Status",
            isRooted ? "Rooted" : "Not Rooted",
            isRooted ? "Rooting reduces system security, may allow malicious apps to gain higher privileges; some banking apps may refuse to run" : "System in normal security state",
            InfoItem.CATEGORY_SECURITY,
            isRooted ? InfoItem.LEVEL_DANGER : InfoItem.LEVEL_INFO
        ));

        boolean isDebuggable = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        items.add(new InfoItem(
            "Debug Mode",
            isDebuggable ? "Enabled" : "Disabled",
            isDebuggable ? "Debug mode may allow app data to be obtained by debug tools; should be disabled in production" : "Normal release state",
            InfoItem.CATEGORY_SECURITY,
            isDebuggable ? InfoItem.LEVEL_WARNING : InfoItem.LEVEL_INFO
        ));

        String selinuxStatus = getSELinuxStatus();
        items.add(new InfoItem(
            "SELinux Status",
            selinuxStatus,
            "SELinux is Security-Enhanced Linux. Enforcing mode provides strongest security protection; Permissive mode reduces security",
            InfoItem.CATEGORY_SECURITY,
            "Enforcing".equals(selinuxStatus) ? InfoItem.LEVEL_INFO : InfoItem.LEVEL_WARNING
        ));

        boolean adbEnabled = isAdbEnabled();
        items.add(new InfoItem(
            "ADB Debugging",
            adbEnabled ? "Enabled" : "Disabled",
            adbEnabled ? "ADB debugging allows computer to directly access device; may be maliciously exploited to gain device control" : "Normal security state",
            InfoItem.CATEGORY_SECURITY,
            adbEnabled ? InfoItem.LEVEL_WARNING : InfoItem.LEVEL_INFO
        ));

        boolean isSigned = isAppSigned();
        items.add(new InfoItem(
            "App Signature",
            isSigned ? "Signed" : "Not Signed",
            isSigned ? "App has passed digital signature verification, integrity guaranteed" : "Unsigned apps may be tampered with; security risk exists",
            InfoItem.CATEGORY_SECURITY,
            isSigned ? InfoItem.LEVEL_INFO : InfoItem.LEVEL_DANGER
        ));

        boolean isSystemIntegrityOk = checkSystemIntegrity();
        items.add(new InfoItem(
            "System Integrity",
            isSystemIntegrityOk ? "Normal" : "May be Tampered",
            isSystemIntegrityOk ? "System file integrity is good, not tampered" : "System files may be maliciously modified; suggest checking device",
            InfoItem.CATEGORY_SECURITY,
            isSystemIntegrityOk ? InfoItem.LEVEL_INFO : InfoItem.LEVEL_DANGER
        ));

        return items;
    }

    private boolean checkRooted() {
        String[] paths = {
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        };

        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"which", "su"});
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private String getSELinuxStatus() {
        try {
            Process process = Runtime.getRuntime().exec("getenforce");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            return line != null ? line.trim() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private boolean isAdbEnabled() {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAppSigned() {
        try {
            String sig = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures[0].toCharsString();
            return sig != null && !sig.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkSystemIntegrity() {
        try {
            Process process = Runtime.getRuntime().exec("ls -la /system/bin/sh");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return true;
        }
    }

    public static class InfoItem {
        public static final String CATEGORY_DEVICE = "Device Hardware";
        public static final String CATEGORY_SYSTEM = "System Info";
        public static final String CATEGORY_NETWORK = "Network Info";
        public static final String CATEGORY_STORAGE = "Storage Info";
        public static final String CATEGORY_DISPLAY = "Display Info";
        public static final String CATEGORY_APP = "App Environment";
        public static final String CATEGORY_SENSOR = "Sensors";
        public static final String CATEGORY_SECURITY = "Security Status";

        public static final String LEVEL_INFO = "info";
        public static final String LEVEL_WARNING = "warning";
        public static final String LEVEL_DANGER = "danger";

        public String name;
        public String value;
        public String usage;
        public String category;
        public String level;

        public InfoItem(String name, String value, String usage, String category, String level) {
            this.name = name;
            this.value = value;
            this.usage = usage;
            this.category = category;
            this.level = level;
        }
    }
}