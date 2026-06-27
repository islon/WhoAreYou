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
            context.getString(R.string.info_device_brand),
            Build.MANUFACTURER,
            context.getString(R.string.usage_device_brand),
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_device_model),
            Build.MODEL,
            context.getString(R.string.usage_device_model),
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_device_codename),
            Build.DEVICE,
            context.getString(R.string.usage_device_codename),
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_product_name),
            Build.PRODUCT,
            context.getString(R.string.usage_product_name),
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_hardware_platform),
            Build.HARDWARE,
            context.getString(R.string.usage_hardware_platform),
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_android_id),
            getAndroidId(),
            context.getString(R.string.usage_android_id),
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_DANGER
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_cpu_cores),
            String.valueOf(Runtime.getRuntime().availableProcessors()),
            context.getString(R.string.usage_cpu_cores),
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_cpu_arch),
            Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "Unknown",
            context.getString(R.string.usage_cpu_arch),
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);

            items.add(new InfoItem(
                context.getString(R.string.info_total_memory),
                formatSize(memoryInfo.totalMem),
                context.getString(R.string.usage_total_memory),
                InfoItem.CATEGORY_DEVICE,
                InfoItem.LEVEL_INFO
            ));

            items.add(new InfoItem(
                context.getString(R.string.info_available_memory),
                formatSize(memoryInfo.availMem),
                context.getString(R.string.usage_available_memory),
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
                context.getString(R.string.info_battery_level),
                String.format("%.0f%% (%s)", batteryPct, chargingStatus),
                context.getString(R.string.usage_battery_level),
                InfoItem.CATEGORY_DEVICE,
                InfoItem.LEVEL_INFO
            ));

            items.add(new InfoItem(
                context.getString(R.string.info_charging_method),
                plugType,
                context.getString(R.string.usage_charging_method),
                InfoItem.CATEGORY_DEVICE,
                InfoItem.LEVEL_INFO
            ));
        }

        return items;
    }

    private List<InfoItem> getSystemDeepInfo() {
        List<InfoItem> items = new ArrayList<>();

        items.add(new InfoItem(
            context.getString(R.string.info_android_version),
            Build.VERSION.RELEASE,
            context.getString(R.string.usage_android_version),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_sdk_version),
            String.valueOf(Build.VERSION.SDK_INT),
            context.getString(R.string.usage_sdk_version),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_security_patch),
            Build.VERSION.SECURITY_PATCH,
            context.getString(R.string.usage_security_patch),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_baseband_version),
            Build.getRadioVersion(),
            context.getString(R.string.usage_baseband_version),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_kernel_version),
            System.getProperty("os.version", "Unknown"),
            context.getString(R.string.usage_kernel_version),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_build_fingerprint),
            Build.FINGERPRINT,
            context.getString(R.string.usage_build_fingerprint),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_bootloader_version),
            Build.BOOTLOADER,
            context.getString(R.string.usage_bootloader_version),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_system_uptime),
            formatUptime(SystemClock.elapsedRealtime()),
            context.getString(R.string.usage_system_uptime),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_system_language),
            Locale.getDefault().getDisplayLanguage() + " (" + Locale.getDefault().getLanguage() + ")",
            context.getString(R.string.usage_system_language),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_system_region),
            Locale.getDefault().getCountry(),
            context.getString(R.string.usage_system_region),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_timezone),
            TimeZone.getDefault().getID() + " (UTC" + (TimeZone.getDefault().getRawOffset() >= 0 ? "+" : "") +
            (TimeZone.getDefault().getRawOffset() / 3600000) + ")",
            context.getString(R.string.usage_timezone),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        return items;
    }

    private List<InfoItem> getNetworkDeepInfo() {
        List<InfoItem> items = new ArrayList<>();

        String ipAddress = getIPAddress();
        items.add(new InfoItem(
            context.getString(R.string.info_ip_address),
            ipAddress,
            context.getString(R.string.usage_ip_address),
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
                    context.getString(R.string.info_wifi_ssid),
                    ssid,
                    context.getString(R.string.usage_wifi_ssid),
                    InfoItem.CATEGORY_NETWORK,
                    InfoItem.LEVEL_DANGER
                ));

                items.add(new InfoItem(
                    context.getString(R.string.info_wifi_bssid),
                    wifiInfo.getBSSID(),
                    context.getString(R.string.usage_wifi_bssid),
                    InfoItem.CATEGORY_NETWORK,
                    InfoItem.LEVEL_DANGER
                ));

                items.add(new InfoItem(
                    context.getString(R.string.info_wifi_signal),
                    wifiInfo.getRssi() + " dBm",
                    context.getString(R.string.usage_wifi_signal),
                    InfoItem.CATEGORY_NETWORK,
                    InfoItem.LEVEL_INFO
                ));

                items.add(new InfoItem(
                    context.getString(R.string.info_wifi_frequency),
                    wifiInfo.getFrequency() + " MHz",
                    context.getString(R.string.usage_wifi_frequency),
                    InfoItem.CATEGORY_NETWORK,
                    InfoItem.LEVEL_INFO
                ));
            }
        }

        String networkType = getNetworkType();
        items.add(new InfoItem(
            context.getString(R.string.info_network_type),
            networkType,
            context.getString(R.string.usage_network_type),
            InfoItem.CATEGORY_NETWORK,
            InfoItem.LEVEL_INFO
        ));

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            String networkOperatorName = tm.getNetworkOperatorName();
            String simOperator = tm.getSimOperator();
            String networkCountryIso = tm.getNetworkCountryIso();

            items.add(new InfoItem(
                context.getString(R.string.info_network_operator),
                networkOperatorName != null ? networkOperatorName : "Unknown",
                context.getString(R.string.usage_network_operator),
                InfoItem.CATEGORY_NETWORK,
                InfoItem.LEVEL_INFO
            ));

            items.add(new InfoItem(
                context.getString(R.string.info_network_operator_code),
                networkCountryIso != null ? networkCountryIso.toUpperCase() : "Unknown",
                context.getString(R.string.usage_network_operator_code),
                InfoItem.CATEGORY_NETWORK,
                InfoItem.LEVEL_WARNING
            ));
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            items.add(new InfoItem(
                context.getString(R.string.info_bluetooth_status),
                "Enabled",
                context.getString(R.string.usage_bluetooth_status),
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
            context.getString(R.string.info_internal_storage_total),
            formatSize(internalTotal),
            context.getString(R.string.usage_internal_storage_total),
            InfoItem.CATEGORY_STORAGE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_internal_storage_available),
            formatSize(internalAvailable) + " (" + (internalAvailable * 100 / internalTotal) + "%)",
            context.getString(R.string.usage_internal_storage_available),
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
                    context.getString(R.string.info_external_storage_total),
                    formatSize(externalTotal),
                    context.getString(R.string.usage_external_storage_total),
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
            context.getString(R.string.info_screen_resolution),
            width + " x " + height,
            context.getString(R.string.usage_screen_resolution),
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_screen_size),
            String.format("%.1f inches", screenDiagonal),
            context.getString(R.string.usage_screen_size),
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_screen_density),
            String.format("%.1f (%s)", density, getDensityName(density)),
            context.getString(R.string.usage_screen_density),
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            context.getString(R.string.info_refresh_rate),
            windowManager != null ? windowManager.getDefaultDisplay().getRefreshRate() + " Hz" : "Unknown",
            context.getString(R.string.usage_refresh_rate),
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        int orientation = context.getResources().getConfiguration().orientation;
        String orientationStr = orientation == Configuration.ORIENTATION_PORTRAIT ? "Portrait" :
                               orientation == Configuration.ORIENTATION_LANDSCAPE ? "Landscape" : "Unknown";
        items.add(new InfoItem(
            context.getString(R.string.info_current_orientation),
            orientationStr,
            context.getString(R.string.usage_current_orientation),
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        return items;
    }

    private List<InfoItem> getUserBehaviorInfo() {
        List<InfoItem> items = new ArrayList<>();

        float fontScale = context.getResources().getConfiguration().fontScale;
        items.add(new InfoItem(
            context.getString(R.string.info_font_scale),
            String.format("%.1fx", fontScale),
            context.getString(R.string.usage_font_scale),
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
                context.getString(R.string.info_animation_scale),
                String.format("%.1fx", animationScale),
                context.getString(R.string.usage_animation_scale),
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
                context.getString(R.string.info_screen_timeout),
                (screenOffTimeout / 1000) + " seconds",
                context.getString(R.string.usage_screen_timeout),
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
                context.getString(R.string.info_ringer_mode),
                modeStr,
                context.getString(R.string.usage_ringer_mode),
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
                context.getString(R.string.info_brightness_mode),
                brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ? "Auto" : "Manual",
                context.getString(R.string.usage_brightness_mode),
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
                context.getString(R.string.info_dark_mode),
                nightModeStr,
                context.getString(R.string.usage_dark_mode),
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
            context.getString(R.string.info_installed_apps_count),
            String.valueOf(installedApps.size()),
            context.getString(R.string.usage_installed_apps_count),
            InfoItem.CATEGORY_APP,
            InfoItem.LEVEL_INFO
        ));

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("http://"));
        ResolveInfo resolveInfo = pm.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo != null && resolveInfo.activityInfo != null) {
            String browserName = resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
            items.add(new InfoItem(
                context.getString(R.string.info_default_browser),
                browserName,
                context.getString(R.string.usage_default_browser),
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
                context.getString(R.string.info_default_launcher),
                launcherName,
                context.getString(R.string.usage_default_launcher),
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        }

        String userAgent = System.getProperty("http.agent");
        items.add(new InfoItem(
            context.getString(R.string.info_system_user_agent),
            userAgent != null ? userAgent : "Unknown",
            context.getString(R.string.usage_system_user_agent),
            InfoItem.CATEGORY_APP,
            InfoItem.LEVEL_WARNING
        ));

        return items;
    }

    private List<InfoItem> getTimeLocationInfo() {
        List<InfoItem> items = new ArrayList<>();

        long uptimeMillis = SystemClock.elapsedRealtime();
        items.add(new InfoItem(
            context.getString(R.string.info_system_runtime),
            formatUptime(uptimeMillis),
            context.getString(R.string.usage_system_runtime),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        boolean is24Hour = android.text.format.DateFormat.is24HourFormat(context);
        items.add(new InfoItem(
            context.getString(R.string.info_time_format),
            is24Hour ? "24-hour" : "12-hour",
            context.getString(R.string.usage_time_format),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        items.add(new InfoItem(
            context.getString(R.string.info_current_time),
            sdf.format(new Date()),
            context.getString(R.string.usage_current_time),
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        String dateFormat = Settings.System.getString(
            context.getContentResolver(),
            Settings.System.DATE_FORMAT
        );
        items.add(new InfoItem(
            context.getString(R.string.info_date_format),
            dateFormat != null ? dateFormat : "System Default",
            context.getString(R.string.usage_date_format),
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
                context.getString(R.string.info_sensor_count),
                String.valueOf(sensors.size()),
                context.getString(R.string.usage_sensor_count),
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
                    context.getString(R.string.info_accelerometer),
                    "Supported",
                    context.getString(R.string.usage_accelerometer),
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_INFO
                ));
            }

            if (hasGyroscope) {
                items.add(new InfoItem(
                    context.getString(R.string.info_gyroscope),
                    "Supported",
                    context.getString(R.string.usage_gyroscope),
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_INFO
                ));
            }

            if (hasMagnetic) {
                items.add(new InfoItem(
                    context.getString(R.string.info_magnetic_sensor),
                    "Supported",
                    context.getString(R.string.usage_magnetic_sensor),
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_INFO
                ));
            }

            if (hasLight) {
                items.add(new InfoItem(
                    context.getString(R.string.info_light_sensor),
                    "Supported",
                    context.getString(R.string.usage_light_sensor),
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_WARNING
                ));
            }

            if (hasProximity) {
                items.add(new InfoItem(
                    context.getString(R.string.info_proximity_sensor),
                    "Supported",
                    context.getString(R.string.usage_proximity_sensor),
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_INFO
                ));
            }

            if (hasStepCounter) {
                items.add(new InfoItem(
                    context.getString(R.string.info_step_counter),
                    "Supported",
                    context.getString(R.string.usage_step_counter),
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_WARNING
                ));
            }

            items.add(new InfoItem(
                context.getString(R.string.info_all_sensors_list),
                sensorTypes.length() > 0 ? sensorTypes.substring(0, sensorTypes.length() - 2) : "None",
                context.getString(R.string.usage_all_sensors_list),
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
            context.getString(R.string.info_root_status),
            isRooted ? "Rooted" : "Not Rooted",
            isRooted ? context.getString(R.string.usage_root_status) : "System in normal security state",
            InfoItem.CATEGORY_SECURITY,
            isRooted ? InfoItem.LEVEL_DANGER : InfoItem.LEVEL_INFO
        ));

        boolean isDebuggable = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        items.add(new InfoItem(
            context.getString(R.string.info_debug_mode),
            isDebuggable ? "Enabled" : "Disabled",
            isDebuggable ? context.getString(R.string.usage_debug_mode) : "Normal release state",
            InfoItem.CATEGORY_SECURITY,
            isDebuggable ? InfoItem.LEVEL_WARNING : InfoItem.LEVEL_INFO
        ));

        String selinuxStatus = getSELinuxStatus();
        items.add(new InfoItem(
            context.getString(R.string.info_selinux_status),
            selinuxStatus,
            context.getString(R.string.usage_selinux_status),
            InfoItem.CATEGORY_SECURITY,
            "Enforcing".equals(selinuxStatus) ? InfoItem.LEVEL_INFO : InfoItem.LEVEL_WARNING
        ));

        boolean adbEnabled = isAdbEnabled();
        items.add(new InfoItem(
            context.getString(R.string.info_adb_debugging),
            adbEnabled ? "Enabled" : "Disabled",
            adbEnabled ? context.getString(R.string.usage_adb_debugging) : "Normal security state",
            InfoItem.CATEGORY_SECURITY,
            adbEnabled ? InfoItem.LEVEL_WARNING : InfoItem.LEVEL_INFO
        ));

        boolean isSigned = isAppSigned();
        items.add(new InfoItem(
            context.getString(R.string.info_app_signature),
            isSigned ? "Signed" : "Not Signed",
            isSigned ? context.getString(R.string.usage_app_signature) : "Unsigned apps may be tampered with; security risk exists",
            InfoItem.CATEGORY_SECURITY,
            isSigned ? InfoItem.LEVEL_INFO : InfoItem.LEVEL_DANGER
        ));

        boolean isSystemIntegrityOk = checkSystemIntegrity();
        items.add(new InfoItem(
            context.getString(R.string.info_system_integrity),
            isSystemIntegrityOk ? "Normal" : "May be Tampered",
            isSystemIntegrityOk ? context.getString(R.string.usage_system_integrity) : "System files may be maliciously modified; suggest checking device",
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
        public static final String CATEGORY_DEVICE = "category_device";
        public static final String CATEGORY_SYSTEM = "category_system";
        public static final String CATEGORY_NETWORK = "category_network";
        public static final String CATEGORY_STORAGE = "category_storage";
        public static final String CATEGORY_DISPLAY = "category_display";
        public static final String CATEGORY_APP = "category_app";
        public static final String CATEGORY_SENSOR = "category_sensor";
        public static final String CATEGORY_SECURITY = "category_security";

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