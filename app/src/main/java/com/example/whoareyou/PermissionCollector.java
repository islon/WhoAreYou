package com.example.whoareyou;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PermissionCollector {

    private Context context;

    public PermissionCollector(Context context) {
        this.context = context;
    }

    // 获取所有权限相关信息
    public List<PermissionItem> collectAllPermissions() {
        List<PermissionItem> items = new ArrayList<>();

        items.addAll(checkLocationPermission());
        items.addAll(checkContactsPermission());
        items.addAll(checkSmsPermission());
        items.addAll(checkPhonePermission());
        items.addAll(checkCalendarPermission());
        items.addAll(checkCameraPermission());
        items.addAll(checkMicrophonePermission());
        items.addAll(checkStoragePermission());
        items.addAll(checkCallLogPermission());
        items.addAll(checkMediaPermission());
        items.addAll(checkNotificationPermission());
        items.addAll(checkSensorPermission());
        items.addAll(checkBluetoothPermission());
        items.addAll(checkBackgroundLocationPermission());
        items.addAll(checkSpecialPermissions());

        return items;
    }

    // 位置权限
    private List<PermissionItem> checkLocationPermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasGps = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        items.add(new PermissionItem(
            "📍 GPS精确位置",
            hasGps ? "已授权" : "未授权",
            "可获取精确到米级的实时位置信息，支持卫星定位",
            "【风险】可实时追踪用户位置，记录行动轨迹，推断家庭住址、工作地点、常去场所，实现精准营销或监控",
            "ACCESS_FINE_LOCATION",
            hasGps ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        boolean hasNetwork = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        items.add(new PermissionItem(
            "🌐 网络位置",
            hasNetwork ? "已授权" : "未授权",
            "通过基站信号和WiFi热点推断大致位置，精度约100-1000米",
            "【风险】可推断用户所在城市、商圈，分析出行模式，用于广告投放和用户画像",
            "ACCESS_COARSE_LOCATION",
            hasNetwork ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        return items;
    }

    // 通讯录权限
    private List<PermissionItem> checkContactsPermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasContacts = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;

        int contactCount = 0;
        if (hasContacts) {
            try {
                android.database.Cursor cursor = context.getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null
                );
                if (cursor != null) {
                    contactCount = cursor.getCount();
                    cursor.close();
                }
            } catch (Exception e) {
                contactCount = 0;
            }
        }

        items.add(new PermissionItem(
            "👥 通讯录",
            hasContacts ? "已授权 (" + contactCount + "个联系人)" : "未授权",
            "可读取所有联系人信息，包括姓名、手机号、邮箱、公司、地址等完整资料",
            "【风险】获取用户完整社交网络，分析人际关系，用于精准营销、社交图谱构建、甚至诈骗攻击",
            "READ_CONTACTS",
            hasContacts ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        return items;
    }

    // 短信权限
    private List<PermissionItem> checkSmsPermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasSms = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED;

        int smsCount = 0;
        if (hasSms) {
            try {
                android.database.Cursor cursor = context.getContentResolver().query(
                    Telephony.Sms.CONTENT_URI,
                    new String[]{Telephony.Sms._ID},
                    null, null, null
                );
                if (cursor != null) {
                    smsCount = cursor.getCount();
                    cursor.close();
                }
            } catch (Exception e) {
                smsCount = 0;
            }
        }

        items.add(new PermissionItem(
            "💬 短信",
            hasSms ? "已授权 (" + smsCount + "条)" : "未授权",
            "可读取所有短信内容，包括验证码、私人对话、银行通知、验证码等敏感信息",
            "【风险】可获取短信验证码进行账户劫持，读取私人对话泄露隐私，分析消费习惯和财务状况",
            "READ_SMS",
            hasSms ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        return items;
    }

    // 电话权限
    private List<PermissionItem> checkPhonePermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasPhone = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;

        String imei = "需要权限";
        if (hasPhone) {
            try {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        imei = tm.getDeviceId();
                    } else {
                        imei = tm.getImei();
                    }
                }
            } catch (Exception e) {
                imei = "无法获取";
            }
        }

        items.add(new PermissionItem(
            "📱 设备识别码(IMEI)",
            hasPhone ? imei : "未授权",
            "设备国际移动设备识别码，全球唯一，可识别手机硬件",
            "【风险】用于跨应用追踪用户，构建设备指纹，实现精准广告投放和用户画像",
            "READ_PHONE_STATE",
            hasPhone ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        return items;
    }

    // 日历权限
    private List<PermissionItem> checkCalendarPermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasCalendar = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED;

        int eventCount = 0;
        if (hasCalendar) {
            try {
                android.database.Cursor cursor = context.getContentResolver().query(
                    android.provider.CalendarContract.Events.CONTENT_URI,
                    new String[]{android.provider.CalendarContract.Events._ID},
                    null, null, null
                );
                if (cursor != null) {
                    eventCount = cursor.getCount();
                    cursor.close();
                }
            } catch (Exception e) {
                eventCount = 0;
            }
        }

        items.add(new PermissionItem(
            "📅 日历事件",
            hasCalendar ? "已授权 (" + eventCount + "个事件)" : "未授权",
            "可读取所有日历事件，包括会议安排、生日、旅行计划、纪念日等",
            "【风险】推断用户工作安排、社交活动、旅行计划，用于精准营销和行为分析",
            "READ_CALENDAR",
            hasCalendar ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        return items;
    }

    // 摄像头权限
    private List<PermissionItem> checkCameraPermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasCamera = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        items.add(new PermissionItem(
            "📷 摄像头",
            hasCamera ? "已授权" : "未授权",
            "可调用前后置摄像头进行拍照、录像，获取实时画面",
            "【风险】可在后台偷拍用户，监控用户环境，窃取隐私画面，进行人脸识别",
            "CAMERA",
            hasCamera ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        return items;
    }

    // 麦克风权限
    private List<PermissionItem> checkMicrophonePermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasRecord = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;

        items.add(new PermissionItem(
            "🎤 麦克风",
            hasRecord ? "已授权" : "未授权",
            "可录制音频、监听环境声音，获取语音输入",
            "【风险】可在后台监听用户对话，窃取语音信息，分析说话内容进行语音识别",
            "RECORD_AUDIO",
            hasRecord ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        return items;
    }

    // 存储权限
    private List<PermissionItem> checkStoragePermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasStorage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasStorage = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            hasStorage = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }

        int fileCount = 0;
        if (hasStorage) {
            try {
                File storage = Environment.getExternalStorageDirectory();
                if (storage != null && storage.exists()) {
                    File[] files = storage.listFiles();
                    if (files != null) {
                        fileCount = files.length;
                    }
                }
            } catch (Exception e) {
                fileCount = 0;
            }
        }

        items.add(new PermissionItem(
            "💾 存储文件",
            hasStorage ? "已授权 (" + fileCount + "个文件)" : "未授权",
            "可读取手机存储中的所有文件，包括照片、视频、文档、下载文件等",
            "【风险】可获取私人照片、视频、文档资料、聊天记录备份、浏览器历史等敏感数据",
            "READ_EXTERNAL_STORAGE",
            hasStorage ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        return items;
    }

    // 通话记录权限
    private List<PermissionItem> checkCallLogPermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasCallLog = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED;

        int callCount = 0;
        if (hasCallLog) {
            try {
                android.database.Cursor cursor = context.getContentResolver().query(
                    android.provider.CallLog.CONTENT_URI,
                    new String[]{android.provider.CallLog.Calls._ID},
                    null, null, null
                );
                if (cursor != null) {
                    callCount = cursor.getCount();
                    cursor.close();
                }
            } catch (Exception e) {
                callCount = 0;
            }
        }

        items.add(new PermissionItem(
            "📞 通话记录",
            hasCallLog ? "已授权 (" + callCount + "条)" : "未授权",
            "可读取所有通话记录，包括来电号码、去电号码、通话时间、通话时长、联系人姓名",
            "【风险】分析用户社交关系网络，识别亲密联系人，推断工作习惯和作息规律",
            "READ_CALL_LOG",
            hasCallLog ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        return items;
    }

    // 媒体权限（Android 13+）
    private List<PermissionItem> checkMediaPermission() {
        List<PermissionItem> items = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean hasImages = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
            items.add(new PermissionItem(
                "🖼️ 媒体图片",
                hasImages ? "已授权" : "未授权",
                "可读取手机中的照片和图片文件",
                "【风险】获取私人照片、截图、相册内容，分析用户生活场景和兴趣爱好",
                "READ_MEDIA_IMAGES",
                hasImages ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
            ));

            boolean hasVideo = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO)
                    == PackageManager.PERMISSION_GRANTED;
            items.add(new PermissionItem(
                "🎬 媒体视频",
                hasVideo ? "已授权" : "未授权",
                "可读取手机中的视频文件",
                "【风险】获取私人视频、屏幕录制内容，侵犯用户隐私和生活场景",
                "READ_MEDIA_VIDEO",
                hasVideo ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
            ));

            boolean hasAudio = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO)
                    == PackageManager.PERMISSION_GRANTED;
            items.add(new PermissionItem(
                "🎵 媒体音频",
                hasAudio ? "已授权" : "未授权",
                "可读取手机中的音频和音乐文件",
                "【风险】获取用户音乐偏好、语音备忘录、录音文件等个人信息",
                "READ_MEDIA_AUDIO",
                hasAudio ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
            ));
        }

        return items;
    }

    // 通知权限
    private List<PermissionItem> checkNotificationPermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasNotification = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotification = ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            try {
                android.app.NotificationManager nm = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (nm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    hasNotification = nm.areNotificationsEnabled();
                }
            } catch (Exception e) {
                hasNotification = false;
            }
        }

        items.add(new PermissionItem(
            "🔔 通知权限",
            hasNotification ? "已授权" : "未授权",
            "可发送应用通知、推送消息到通知栏",
            "【风险】发送垃圾通知、广告推送，打扰用户，甚至伪造系统通知进行诈骗",
            "POST_NOTIFICATIONS",
            hasNotification ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        return items;
    }

    // 传感器权限
    private List<PermissionItem> checkSensorPermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasSensors = ActivityCompat.checkSelfPermission(context, Manifest.permission.BODY_SENSORS)
                == PackageManager.PERMISSION_GRANTED;

        items.add(new PermissionItem(
            "🔬 身体传感器",
            hasSensors ? "已授权" : "未授权",
            "可访问心率传感器、步数传感器、睡眠监测等健康数据",
            "【风险】获取用户健康数据、运动习惯、睡眠模式，推断用户身体状况和生活规律",
            "BODY_SENSORS",
            hasSensors ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        return items;
    }

    // 蓝牙权限
    private List<PermissionItem> checkBluetoothPermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasBluetooth = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasBluetooth = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            hasBluetooth = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
                    == PackageManager.PERMISSION_GRANTED;
        }

        items.add(new PermissionItem(
            "🔵 蓝牙连接",
            hasBluetooth ? "已授权" : "未授权",
            "可连接蓝牙设备、扫描附近蓝牙设备、读取蓝牙设备信息",
            "【风险】追踪用户附近蓝牙设备，分析用户设备使用习惯，甚至通过蓝牙传输恶意数据",
            "BLUETOOTH_CONNECT",
            hasBluetooth ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean hasScan = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
                    == PackageManager.PERMISSION_GRANTED;
            items.add(new PermissionItem(
                "🔍 蓝牙扫描",
                hasScan ? "已授权" : "未授权",
                "可扫描附近蓝牙设备，发现周边蓝牙设备信息",
                "【风险】扫描周边设备构建用户环境画像，追踪用户位置和活动范围",
                "BLUETOOTH_SCAN",
                hasScan ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
            ));
        }

        return items;
    }

    // 后台位置权限
    private List<PermissionItem> checkBackgroundLocationPermission() {
        List<PermissionItem> items = new ArrayList<>();

        boolean hasBackgroundLocation = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            hasBackgroundLocation = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }

        items.add(new PermissionItem(
            "🌙 后台位置",
            hasBackgroundLocation ? "已授权" : "未授权",
            "即使应用不在前台运行，也能持续获取用户位置信息",
            "【风险】24小时持续追踪用户位置，记录完整行动轨迹，严重侵犯隐私",
            "ACCESS_BACKGROUND_LOCATION",
            hasBackgroundLocation ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        return items;
    }

    // 特殊权限
    private List<PermissionItem> checkSpecialPermissions() {
        List<PermissionItem> items = new ArrayList<>();

        // 悬浮窗权限
        boolean hasOverlay = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasOverlay = android.provider.Settings.canDrawOverlays(context);
        }
        items.add(new PermissionItem(
            "💬 悬浮窗",
            hasOverlay ? "已授权" : "未授权",
            "可在其他应用上层显示悬浮窗口，覆盖屏幕内容",
            "【风险】强制弹窗广告、监控用户操作、拦截系统功能，影响手机正常使用",
            "SYSTEM_ALERT_WINDOW",
            hasOverlay ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        // 自动启动权限（厂商特定，无法统一检测）
        boolean hasAutoStart = false;
        items.add(new PermissionItem(
            "🔄 自动启动",
            "厂商特定",
            "手机开机后自动启动应用，无需用户手动打开",
            "【风险】占用系统资源、增加耗电、后台持续运行，可能收集用户隐私数据",
            "AUTO_START",
            PermissionItem.STATUS_UNAVAILABLE
        ));

        // 读取剪贴板
        boolean hasClipboard = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                hasClipboard = ActivityCompat.checkSelfPermission(context, "android.permission.READ_CLIPBOARD_IN_BACKGROUND")
                        == PackageManager.PERMISSION_GRANTED;
            } catch (Exception e) {
                hasClipboard = false;
            }
        }
        items.add(new PermissionItem(
            "📋 读取剪贴板",
            hasClipboard ? "已授权" : "未授权",
            "可读取用户复制到剪贴板的所有内容，包括密码、网址、文本等",
            "【风险】窃取用户密码、银行卡号、验证码等敏感信息，造成财产损失",
            "READ_CLIPBOARD_IN_BACKGROUND",
            hasClipboard ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
        ));

        // 附近设备（Android 12+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean hasNearby = ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
                    == PackageManager.PERMISSION_GRANTED;
            items.add(new PermissionItem(
                "📶 附近设备",
                hasNearby ? "已授权" : "未授权",
                "可发现附近WiFi和蓝牙设备，获取设备列表和信号强度",
                "【风险】分析用户周边环境，追踪位置变化，构建用户活动模式",
                "NEARBY_WIFI_DEVICES",
                hasNearby ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
            ));
        }

        // 精确闹钟（Android 12+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean hasExactAlarm = ActivityCompat.checkSelfPermission(context, Manifest.permission.SCHEDULE_EXACT_ALARM)
                    == PackageManager.PERMISSION_GRANTED;
            items.add(new PermissionItem(
                "⏰ 精确闹钟",
                hasExactAlarm ? "已授权" : "未授权",
                "可设置精确到秒的闹钟，即使手机处于低功耗模式也能唤醒",
                "【风险】频繁唤醒手机，增加耗电，可能用于后台定时收集数据",
                "SCHEDULE_EXACT_ALARM",
                hasExactAlarm ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
            ));
        }

        // 访问媒体位置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean hasMediaLocation = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_MEDIA_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
            items.add(new PermissionItem(
                "📍 媒体位置",
                hasMediaLocation ? "已授权" : "未授权",
                "可读取照片和视频中的地理标签信息，获取拍摄地点",
                "【风险】追踪用户拍摄地点，分析出行路线，泄露个人行踪",
                "ACCESS_MEDIA_LOCATION",
                hasMediaLocation ? PermissionItem.STATUS_GRANTED : PermissionItem.STATUS_DENIED
            ));
        }

        return items;
    }

    // 权限项类
    public static class PermissionItem {
        public static final String STATUS_GRANTED = "granted";
        public static final String STATUS_DENIED = "denied";
        public static final String STATUS_UNAVAILABLE = "unavailable";

        public String name;
        public String value;
        public String description;
        public String risk;
        public String permission;
        public String statusType;

        public PermissionItem(String name, String value, String description,
                           String risk, String permission, String statusType) {
            this.name = name;
            this.value = value;
            this.description = description;
            this.risk = risk;
            this.permission = permission;
            this.statusType = statusType;
        }
    }
}