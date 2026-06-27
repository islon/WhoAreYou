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

    // 获取所有设备信息
    public List<InfoItem> collectAllInfo() {
        List<InfoItem> infoList = new ArrayList<>();

        // 设备硬件信息
        infoList.addAll(getDeviceHardwareInfo());

        // 系统深度信息
        infoList.addAll(getSystemDeepInfo());

        // 网络深度信息
        infoList.addAll(getNetworkDeepInfo());

        // 存储信息
        infoList.addAll(getStorageInfo());

        // 显示信息
        infoList.addAll(getDisplayInfo());

        // 用户行为推断
        infoList.addAll(getUserBehaviorInfo());

        // 应用环境信息
        infoList.addAll(getAppEnvironmentInfo());

        // 时间和位置推断
        infoList.addAll(getTimeLocationInfo());

        // 传感器信息
        infoList.addAll(getSensorInfo());

        // 安全状态信息
        infoList.addAll(getSecurityInfo());

        return infoList;
    }

    // 设备硬件信息
    private List<InfoItem> getDeviceHardwareInfo() {
        List<InfoItem> items = new ArrayList<>();

        // 基本信息
        items.add(new InfoItem(
            "设备品牌",
            Build.MANUFACTURER,
            "设备制造商识别。广告商用于设备分级、市场分析；流氓应用用于针对性漏洞利用",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "设备型号",
            Build.MODEL,
            "精确设备型号。用于设备价值评估、消费能力画像；部分老旧机型存在已知漏洞",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "设备代号",
            Build.DEVICE,
            "内部设备代号。用于精确识别设备类型，黑客用于查找特定设备漏洞",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "产品名称",
            Build.PRODUCT,
            "商业产品名称。用于市场细分和用户群体分析",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "硬件平台",
            Build.HARDWARE,
            "底层硬件标识。用于性能优化、驱动兼容性分析",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "设备唯一标识(Android ID)",
            getAndroidId(),
            "核心隐私数据！用于跨应用追踪用户、广告归因、账号关联。重置系统后才会改变",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_DANGER
        ));

        // CPU信息
        items.add(new InfoItem(
            "CPU核心数",
            String.valueOf(Runtime.getRuntime().availableProcessors()),
            "计算能力评估。用于判断设备性能等级、游戏适配；推断用户消费水平",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "CPU架构",
            Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "未知",
            "处理器架构。用于选择正确的恶意代码版本；判断设备是否支持某些安全特性；流氓应用可能针对特定架构攻击",
            InfoItem.CATEGORY_DEVICE,
            InfoItem.LEVEL_INFO
        ));

        // 内存信息
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            
            items.add(new InfoItem(
                "总内存",
                formatSize(memoryInfo.totalMem),
                "设备内存容量。用于性能分级、应用推荐；推断设备价格区间和用户消费能力",
                InfoItem.CATEGORY_DEVICE,
                InfoItem.LEVEL_INFO
            ));

            items.add(new InfoItem(
                "可用内存",
                formatSize(memoryInfo.availMem),
                "当前可用内存。用于判断设备负载状态；推断用户使用习惯（是否多任务）",
                InfoItem.CATEGORY_DEVICE,
                InfoItem.LEVEL_INFO
            ));
        }

        // 电池信息
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent != null) {
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            
            float batteryPct = level * 100 / (float)scale;
            String chargingStatus = status == BatteryManager.BATTERY_STATUS_CHARGING ? "充电中" : 
                                   status == BatteryManager.BATTERY_STATUS_FULL ? "已充满" : "放电中";
            String plugType = plugged == BatteryManager.BATTERY_PLUGGED_AC ? "交流电" :
                             plugged == BatteryManager.BATTERY_PLUGGED_USB ? "USB" :
                             plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS ? "无线" : "未连接";

            items.add(new InfoItem(
                "电池电量",
                String.format("%.0f%% (%s)", batteryPct, chargingStatus),
                "电池状态。用于推断使用场景（充电中可能在家/办公室）；广告商用于选择推送时机",
                InfoItem.CATEGORY_DEVICE,
                InfoItem.LEVEL_INFO
            ));

            items.add(new InfoItem(
                "充电方式",
                plugType,
                "充电类型。推断用户位置场景：USB可能在电脑前，交流电可能在家或办公室",
                InfoItem.CATEGORY_DEVICE,
                InfoItem.LEVEL_INFO
            ));
        }

        return items;
    }

    // 系统深度信息
    private List<InfoItem> getSystemDeepInfo() {
        List<InfoItem> items = new ArrayList<>();

        items.add(new InfoItem(
            "Android版本",
            Build.VERSION.RELEASE,
            "系统版本。用于兼容性判断、功能限制；旧版本系统可能存在已知安全漏洞；流氓应用可能利用旧版本漏洞",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "SDK版本",
            String.valueOf(Build.VERSION.SDK_INT),
            "API级别。精确判断可用功能和安全特性；低SDK版本意味着缺少新安全机制",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "安全补丁级别",
            Build.VERSION.SECURITY_PATCH,
            "重要安全指标！判断设备是否修复了已知漏洞。流氓应用可能利用未修复的漏洞进行攻击",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        items.add(new InfoItem(
            "基带版本",
            Build.getRadioVersion(),
            "通信模块版本。特定版本可能存在基带漏洞，可被流氓应用远程利用",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        items.add(new InfoItem(
            "内核版本",
            System.getProperty("os.version", "未知"),
            "Linux内核版本。用于判断内核漏洞可用性；旧内核可能存在提权漏洞；流氓应用可能利用内核漏洞获取更高权限",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        items.add(new InfoItem(
            "构建指纹",
            Build.FINGERPRINT,
            "完整系统构建标识。用于精确识别系统来源、判断是否为官方ROM；可追溯系统被篡改的历史",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "Bootloader版本",
            Build.BOOTLOADER,
            "引导程序版本。判断设备是否可解锁、是否被Root",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "系统启动时间",
            formatUptime(SystemClock.elapsedRealtime()),
            "设备运行时长。推断用户使用习惯：短时间可能刚开机，长时间可能较少重启",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "系统语言",
            Locale.getDefault().getDisplayLanguage() + " (" + Locale.getDefault().getLanguage() + ")",
            "用户语言偏好。用于内容推荐、地区化服务；推断用户可能的国籍或居住地",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "系统地区",
            Locale.getDefault().getCountry(),
            "国家/地区代码。用于地区限制、内容审核判断；推断用户所在国家",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        items.add(new InfoItem(
            "时区",
            TimeZone.getDefault().getID() + " (UTC" + (TimeZone.getDefault().getRawOffset() >= 0 ? "+" : "") + 
            (TimeZone.getDefault().getRawOffset() / 3600000) + ")",
            "重要位置推断依据！结合其他信息可推断用户大致地理位置",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_WARNING
        ));

        return items;
    }

    // 网络深度信息
    private List<InfoItem> getNetworkDeepInfo() {
        List<InfoItem> items = new ArrayList<>();

        // IP地址
        String ipAddress = getIPAddress();
        items.add(new InfoItem(
            "本机IP地址",
            ipAddress,
            "网络位置标识！通过IP地理数据库可定位到城市级别；用于区域限制、风控判断",
            InfoItem.CATEGORY_NETWORK,
            InfoItem.LEVEL_DANGER
        ));

        // WiFi信息
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
                    "WiFi名称(SSID)",
                    ssid,
                    "重要隐私数据！可推断用户所在场所类型（家/公司/商场/酒店）；用于位置指纹定位",
                    InfoItem.CATEGORY_NETWORK,
                    InfoItem.LEVEL_DANGER
                ));

                items.add(new InfoItem(
                    "WiFi BSSID",
                    wifiInfo.getBSSID(),
                    "路由器MAC地址。用于精确定位（配合WiFi定位数据库）；流氓应用可追踪用户移动轨迹",
                    InfoItem.CATEGORY_NETWORK,
                    InfoItem.LEVEL_DANGER
                ));

                items.add(new InfoItem(
                    "WiFi信号强度",
                    wifiInfo.getRssi() + " dBm",
                    "信号强度。用于室内定位、判断与路由器距离；推断用户在建筑内的位置",
                    InfoItem.CATEGORY_NETWORK,
                    InfoItem.LEVEL_INFO
                ));

                items.add(new InfoItem(
                    "WiFi频率",
                    wifiInfo.getFrequency() + " MHz",
                    "WiFi频段(2.4G/5G)。用于判断网络环境质量；推断路由器类型和使用场景",
                    InfoItem.CATEGORY_NETWORK,
                    InfoItem.LEVEL_INFO
                ));
            }
        }

        // 网络类型
        String networkType = getNetworkType();
        items.add(new InfoItem(
            "网络类型",
            networkType,
            "网络连接方式。用于内容适配、流量控制；推断用户使用场景（WiFi可能在家/办公室）",
            InfoItem.CATEGORY_NETWORK,
            InfoItem.LEVEL_INFO
        ));

        // 运营商信息
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            String networkOperatorName = tm.getNetworkOperatorName();
            String simOperator = tm.getSimOperator();
            String networkCountryIso = tm.getNetworkCountryIso();
            
            items.add(new InfoItem(
                "网络运营商",
                networkOperatorName != null ? networkOperatorName : "未知",
                "移动网络运营商。用于运营商合作业务；推断用户所在国家/地区",
                InfoItem.CATEGORY_NETWORK,
                InfoItem.LEVEL_INFO
            ));

            items.add(new InfoItem(
                "运营商国家代码",
                networkCountryIso != null ? networkCountryIso.toUpperCase() : "未知",
                "运营商所在国家。推断用户所在国家/地区",
                InfoItem.CATEGORY_NETWORK,
                InfoItem.LEVEL_WARNING
            ));
        }

        // 蓝牙信息
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            items.add(new InfoItem(
                "蓝牙状态",
                "已开启",
                "蓝牙开启状态。可用于近距离设备发现；推断用户可能佩戴可穿戴设备",
                InfoItem.CATEGORY_NETWORK,
                InfoItem.LEVEL_INFO
            ));
        }

        return items;
    }

    // 存储信息
    private List<InfoItem> getStorageInfo() {
        List<InfoItem> items = new ArrayList<>();

        // 内部存储
        StatFs internalStats = new StatFs(Environment.getDataDirectory().getPath());
        long internalTotal = internalStats.getBlockCountLong() * internalStats.getBlockSizeLong();
        long internalAvailable = internalStats.getAvailableBlocksLong() * internalStats.getBlockSizeLong();

        items.add(new InfoItem(
            "内部存储总量",
            formatSize(internalTotal),
            "设备存储容量。用于判断设备档次；推断用户消费能力（大存储=高端设备）",
            InfoItem.CATEGORY_STORAGE,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "内部存储可用",
            formatSize(internalAvailable) + " (" + (internalAvailable * 100 / internalTotal) + "%)",
            "可用存储空间。推断用户使用习惯：存储满可能拍照/下载多；空存储可能是新设备",
            InfoItem.CATEGORY_STORAGE,
            InfoItem.LEVEL_INFO
        ));

        // 外部存储
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalPath = Environment.getExternalStorageDirectory();
            if (externalPath != null) {
                StatFs externalStats = new StatFs(externalPath.getPath());
                long externalTotal = externalStats.getBlockCountLong() * externalStats.getBlockSizeLong();
                long externalAvailable = externalStats.getAvailableBlocksLong() * externalStats.getBlockSizeLong();

                items.add(new InfoItem(
                    "外部存储总量",
                    formatSize(externalTotal),
                    "SD卡容量。判断是否有扩展存储；推断用户存储需求",
                    InfoItem.CATEGORY_STORAGE,
                    InfoItem.LEVEL_INFO
                ));
            }
        }

        return items;
    }

    // 显示信息
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
            "屏幕分辨率",
            width + " x " + height,
            "显示分辨率。用于UI适配、图片资源选择；推断设备档次",
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "屏幕尺寸",
            String.format("%.1f 英寸", screenDiagonal),
            "物理屏幕尺寸。用于判断设备类型（手机/平板）；推断使用场景",
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "屏幕密度",
            String.format("%.1f (%s)", density, getDensityName(density)),
            "像素密度。用于资源选择、性能优化；高密度通常意味着高端设备",
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        items.add(new InfoItem(
            "刷新率",
            windowManager != null ? windowManager.getDefaultDisplay().getRefreshRate() + " Hz" : "未知",
            "屏幕刷新率。高刷新率=高端设备；用于动画流畅度判断",
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        // 屏幕方向
        int orientation = context.getResources().getConfiguration().orientation;
        String orientationStr = orientation == Configuration.ORIENTATION_PORTRAIT ? "竖屏" : 
                               orientation == Configuration.ORIENTATION_LANDSCAPE ? "横屏" : "未知";
        items.add(new InfoItem(
            "当前屏幕方向",
            orientationStr,
            "当前屏幕方向。用于判断用户当前活动（横屏可能在看视频/玩游戏）",
            InfoItem.CATEGORY_DISPLAY,
            InfoItem.LEVEL_INFO
        ));

        return items;
    }

    // 用户行为推断
    private List<InfoItem> getUserBehaviorInfo() {
        List<InfoItem> items = new ArrayList<>();

        // 系统设置
        float fontScale = context.getResources().getConfiguration().fontScale;
        items.add(new InfoItem(
            "字体缩放",
            String.format("%.1fx", fontScale),
            "用户字体偏好。大于1.0可能视力不佳或年长用户；用于UI适配",
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
                "动画缩放",
                String.format("%.1fx", animationScale),
                "动画速度设置。0表示关闭动画，可能是开发者或追求性能的用户",
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
                "屏幕超时时间",
                (screenOffTimeout / 1000) + " 秒",
                "自动锁屏时间。推断用户使用习惯：长时间=常看手机；短时间=省电模式",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        } catch (Exception e) {}

        // 铃声模式
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int ringerMode = audioManager.getRingerMode();
            String modeStr = ringerMode == AudioManager.RINGER_MODE_NORMAL ? "正常" :
                            ringerMode == AudioManager.RINGER_MODE_SILENT ? "静音" :
                            ringerMode == AudioManager.RINGER_MODE_VIBRATE ? "振动" : "未知";
            items.add(new InfoItem(
                "铃声模式",
                modeStr,
                "声音模式。推断用户当前场景：静音可能在会议/睡觉；振动可能在公共场所",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        }

        // 亮度模式
        try {
            int brightnessMode = Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE
            );
            items.add(new InfoItem(
                "亮度模式",
                brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ? "自动" : "手动",
                "亮度调节方式。自动亮度可能表示用户注重便利性；手动可能注重精确控制",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        } catch (Exception e) {}

        // 深色模式
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager != null) {
            int nightMode = uiModeManager.getNightMode();
            String nightModeStr = nightMode == UiModeManager.MODE_NIGHT_YES ? "开启" :
                                 nightMode == UiModeManager.MODE_NIGHT_NO ? "关闭" : "自动";
            items.add(new InfoItem(
                "深色模式",
                nightModeStr,
                "深色模式状态。开启可能表示用户在夜间使用或偏好深色主题",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        }

        return items;
    }

    // 应用环境信息
    private List<InfoItem> getAppEnvironmentInfo() {
        List<InfoItem> items = new ArrayList<>();

        // 已安装应用数量
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        
        items.add(new InfoItem(
            "已安装应用数量",
            String.valueOf(installedApps.size()),
            "应用安装量。推断用户类型：应用多=重度用户；应用少=轻度用户或新设备",
            InfoItem.CATEGORY_APP,
            InfoItem.LEVEL_INFO
        ));

        // 默认浏览器
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("http://"));
        ResolveInfo resolveInfo = pm.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo != null && resolveInfo.activityInfo != null) {
            String browserName = resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
            items.add(new InfoItem(
                "默认浏览器",
                browserName,
                "浏览器偏好。用于判断用户群体特征；某些浏览器有特定用户画像",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        }

        // 默认启动器
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo launcherInfo = pm.resolveActivity(launcherIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (launcherInfo != null && launcherInfo.activityInfo != null) {
            String launcherName = launcherInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
            items.add(new InfoItem(
                "默认启动器",
                launcherName,
                "桌面启动器。第三方启动器表示用户喜欢个性化；推断用户技术能力",
                InfoItem.CATEGORY_APP,
                InfoItem.LEVEL_INFO
            ));
        }

        // User Agent
        String userAgent = System.getProperty("http.agent");
        items.add(new InfoItem(
            "系统User Agent",
            userAgent != null ? userAgent : "未知",
            "浏览器标识字符串。包含设备型号、系统版本、浏览器版本；用于服务器识别设备",
            InfoItem.CATEGORY_APP,
            InfoItem.LEVEL_WARNING
        ));

        return items;
    }

    // 时间和位置推断
    private List<InfoItem> getTimeLocationInfo() {
        List<InfoItem> items = new ArrayList<>();

        // 系统运行时间
        long uptimeMillis = SystemClock.elapsedRealtime();
        items.add(new InfoItem(
            "系统运行时间",
            formatUptime(uptimeMillis),
            "设备运行时长。短时间=刚开机或重启；长时间=设备稳定运行",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        // 时间格式
        boolean is24Hour = android.text.format.DateFormat.is24HourFormat(context);
        items.add(new InfoItem(
            "时间格式",
            is24Hour ? "24小时制" : "12小时制",
            "时间显示偏好。24小时制多为欧洲/亚洲用户；12小时制多为美国用户",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        // 当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        items.add(new InfoItem(
            "当前系统时间",
            sdf.format(new Date()),
            "设备当前时间。用于判断用户活跃时段；配合时区可推断大致位置",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        // 日期格式
        String dateFormat = Settings.System.getString(
            context.getContentResolver(),
            Settings.System.DATE_FORMAT
        );
        items.add(new InfoItem(
            "日期格式",
            dateFormat != null ? dateFormat : "系统默认",
            "日期显示格式。不同地区有不同偏好；辅助判断用户所在地区",
            InfoItem.CATEGORY_SYSTEM,
            InfoItem.LEVEL_INFO
        ));

        return items;
    }

    // 辅助方法
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
        return "无法获取";
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
                    return "移动网络 (" + activeNetwork.getSubtypeName() + ")";
                }
            }
        }
        return "未连接";
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
        if (days > 0) sb.append(days).append("天 ");
        if (hours > 0) sb.append(hours).append("小时 ");
        sb.append(minutes).append("分钟");
        return sb.toString();
    }

    private String getDensityName(float density) {
        if (density <= 0.75f) return "ldpi";
        if (density <= 1.0f) return "mdpi";
        if (density <= 1.5f) return "hdpi";
        if (density <= 2.0f) return "xhdpi";
        if (density <= 3.0f) return "xxhdpi";
        if (density <= 4.0f) return "xxxhdpi";
        return "超高清";
    }

    // 传感器信息
    private List<InfoItem> getSensorInfo() {
        List<InfoItem> items = new ArrayList<>();
        
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            
            items.add(new InfoItem(
                "传感器数量",
                String.valueOf(sensors.size()),
                "设备传感器总数。更多传感器意味着更多数据采集能力；某些传感器可用于用户行为追踪",
                InfoItem.CATEGORY_SENSOR,
                InfoItem.LEVEL_INFO
            ));
            
            // 检测关键传感器
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
                    "加速度传感器",
                    "支持",
                    "检测设备运动。可用于步数统计、手势识别、用户行为分析；游戏和健身应用常用",
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_INFO
                ));
            }
            
            if (hasGyroscope) {
                items.add(new InfoItem(
                    "陀螺仪传感器",
                    "支持",
                    "检测设备旋转。用于AR/VR应用、精确运动追踪；可推断用户活动类型",
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_INFO
                ));
            }
            
            if (hasMagnetic) {
                items.add(new InfoItem(
                    "磁力传感器",
                    "支持",
                    "检测磁场。用于电子罗盘、定位辅助；可推断用户朝向和位置",
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_INFO
                ));
            }
            
            if (hasLight) {
                items.add(new InfoItem(
                    "光线传感器",
                    "支持",
                    "检测环境光强度。用于自动亮度调节；可推断用户所处环境（室内/室外）",
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_WARNING
                ));
            }
            
            if (hasProximity) {
                items.add(new InfoItem(
                    "距离传感器",
                    "支持",
                    "检测物体距离。用于通话时关闭屏幕；可推断用户使用习惯和场景",
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_INFO
                ));
            }
            
            if (hasStepCounter) {
                items.add(new InfoItem(
                    "计步器",
                    "支持",
                    "统计步数。用于健康追踪；可推断用户运动量和生活习惯",
                    InfoItem.CATEGORY_SENSOR,
                    InfoItem.LEVEL_WARNING
                ));
            }
            
            items.add(new InfoItem(
                "全部传感器列表",
                sensorTypes.length() > 0 ? sensorTypes.substring(0, sensorTypes.length() - 2) : "无",
                "设备支持的所有传感器。用于应用功能适配；流氓应用可利用传感器数据进行用户行为分析和追踪",
                InfoItem.CATEGORY_SENSOR,
                InfoItem.LEVEL_INFO
            ));
        }
        
        return items;
    }

    // 安全状态信息
    private List<InfoItem> getSecurityInfo() {
        List<InfoItem> items = new ArrayList<>();

        // Root检测
        boolean isRooted = checkRooted();
        items.add(new InfoItem(
            "设备Root状态",
            isRooted ? "已Root" : "未Root",
            isRooted ? "Root后系统安全性降低，可能被恶意应用获取更高权限；部分银行应用可能拒绝运行" : "系统处于正常安全状态",
            InfoItem.CATEGORY_SECURITY,
            isRooted ? InfoItem.LEVEL_DANGER : InfoItem.LEVEL_INFO
        ));

        // 调试模式
        boolean isDebuggable = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        items.add(new InfoItem(
            "调试模式",
            isDebuggable ? "开启" : "关闭",
            isDebuggable ? "调试模式开启可能导致应用数据被调试工具获取；生产环境应关闭" : "正常发布状态",
            InfoItem.CATEGORY_SECURITY,
            isDebuggable ? InfoItem.LEVEL_WARNING : InfoItem.LEVEL_INFO
        ));

        // SELinux状态
        String selinuxStatus = getSELinuxStatus();
        items.add(new InfoItem(
            "SELinux状态",
            selinuxStatus,
            "SELinux是安全增强型Linux。Enforcing模式提供最强安全保护；Permissive模式降低安全性",
            InfoItem.CATEGORY_SECURITY,
            "Enforcing".equals(selinuxStatus) ? InfoItem.LEVEL_INFO : InfoItem.LEVEL_WARNING
        ));

        // ADB状态
        boolean adbEnabled = isAdbEnabled();
        items.add(new InfoItem(
            "ADB调试",
            adbEnabled ? "开启" : "关闭",
            adbEnabled ? "ADB调试开启允许电脑直接访问设备；可能被恶意利用获取设备控制权" : "正常安全状态",
            InfoItem.CATEGORY_SECURITY,
            adbEnabled ? InfoItem.LEVEL_WARNING : InfoItem.LEVEL_INFO
        ));

        // 应用签名状态
        boolean isSigned = isAppSigned();
        items.add(new InfoItem(
            "应用签名",
            isSigned ? "已签名" : "未签名",
            isSigned ? "应用已通过数字签名验证，完整性有保障" : "未签名应用可能被篡改；存在安全风险",
            InfoItem.CATEGORY_SECURITY,
            isSigned ? InfoItem.LEVEL_INFO : InfoItem.LEVEL_DANGER
        ));

        // 系统完整性
        boolean isSystemIntegrityOk = checkSystemIntegrity();
        items.add(new InfoItem(
            "系统完整性",
            isSystemIntegrityOk ? "正常" : "可能被篡改",
            isSystemIntegrityOk ? "系统文件完整性良好，未被篡改" : "系统文件可能被恶意修改；建议检查设备",
            InfoItem.CATEGORY_SECURITY,
            isSystemIntegrityOk ? InfoItem.LEVEL_INFO : InfoItem.LEVEL_DANGER
        ));

        return items;
    }

    // 检测Root
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

    // 获取SELinux状态
    private String getSELinuxStatus() {
        try {
            Process process = Runtime.getRuntime().exec("getenforce");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            return line != null ? line.trim() : "未知";
        } catch (Exception e) {
            return "未知";
        }
    }

    // 检测ADB是否开启
    private boolean isAdbEnabled() {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    // 检测应用是否签名
    private boolean isAppSigned() {
        try {
            String sig = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures[0].toCharsString();
            return sig != null && !sig.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // 检查系统完整性
    private boolean checkSystemIntegrity() {
        try {
            Process process = Runtime.getRuntime().exec("ls -la /system/bin/sh");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return true;
        }
    }

    // 信息项类
    public static class InfoItem {
        // 分类
        public static final String CATEGORY_DEVICE = "设备硬件";
        public static final String CATEGORY_SYSTEM = "系统信息";
        public static final String CATEGORY_NETWORK = "网络信息";
        public static final String CATEGORY_STORAGE = "存储信息";
        public static final String CATEGORY_DISPLAY = "显示信息";
        public static final String CATEGORY_APP = "应用环境";
        public static final String CATEGORY_SENSOR = "传感器";
        public static final String CATEGORY_SECURITY = "安全状态";

        // 级别
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
