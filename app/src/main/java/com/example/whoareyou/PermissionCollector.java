package com.example.whoareyou;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public class PermissionCollector {

    private Context context;

    public PermissionCollector(Context context) {
        this.context = context;
    }

    private String r(int resId) {
        return context.getString(resId);
    }

    public List<PermissionItem> getAllPermissions() {
        List<PermissionItem> permissions = new ArrayList<>();

        permissions.add(new PermissionItem(
            "ACCESS_FINE_LOCATION",
            r(R.string.perm_access_fine_location),
            r(R.string.perm_desc_access_fine_location),
            r(R.string.perm_risk_access_fine_location),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ));

        permissions.add(new PermissionItem(
            "ACCESS_COARSE_LOCATION",
            r(R.string.perm_access_coarse_location),
            r(R.string.perm_desc_access_coarse_location),
            r(R.string.perm_risk_access_coarse_location),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ));

        permissions.add(new PermissionItem(
            "ACCESS_BACKGROUND_LOCATION",
            r(R.string.perm_access_background_location),
            r(R.string.perm_desc_access_background_location),
            r(R.string.perm_risk_access_background_location),
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ));

        permissions.add(new PermissionItem(
            "READ_CONTACTS",
            r(R.string.perm_read_contacts),
            r(R.string.perm_desc_read_contacts),
            r(R.string.perm_risk_read_contacts),
            android.Manifest.permission.READ_CONTACTS
        ));

        permissions.add(new PermissionItem(
            "WRITE_CONTACTS",
            r(R.string.perm_write_contacts),
            r(R.string.perm_desc_write_contacts),
            r(R.string.perm_risk_write_contacts),
            android.Manifest.permission.WRITE_CONTACTS
        ));

        permissions.add(new PermissionItem(
            "READ_SMS",
            r(R.string.perm_read_sms),
            r(R.string.perm_desc_read_sms),
            r(R.string.perm_risk_read_sms),
            android.Manifest.permission.READ_SMS
        ));

        permissions.add(new PermissionItem(
            "SEND_SMS",
            r(R.string.perm_send_sms),
            r(R.string.perm_desc_send_sms),
            r(R.string.perm_risk_send_sms),
            android.Manifest.permission.SEND_SMS
        ));

        permissions.add(new PermissionItem(
            "RECEIVE_SMS",
            r(R.string.perm_receive_sms),
            r(R.string.perm_desc_receive_sms),
            r(R.string.perm_risk_receive_sms),
            android.Manifest.permission.RECEIVE_SMS
        ));

        permissions.add(new PermissionItem(
            "READ_CALL_LOG",
            r(R.string.perm_read_call_log),
            r(R.string.perm_desc_read_call_log),
            r(R.string.perm_risk_read_call_log),
            android.Manifest.permission.READ_CALL_LOG
        ));

        permissions.add(new PermissionItem(
            "WRITE_CALL_LOG",
            r(R.string.perm_write_call_log),
            r(R.string.perm_desc_write_call_log),
            r(R.string.perm_risk_write_call_log),
            android.Manifest.permission.WRITE_CALL_LOG
        ));

        permissions.add(new PermissionItem(
            "RECORD_AUDIO",
            r(R.string.perm_record_audio),
            r(R.string.perm_desc_record_audio),
            r(R.string.perm_risk_record_audio),
            android.Manifest.permission.RECORD_AUDIO
        ));

        permissions.add(new PermissionItem(
            "CAMERA",
            r(R.string.perm_camera),
            r(R.string.perm_desc_camera),
            r(R.string.perm_risk_camera),
            android.Manifest.permission.CAMERA
        ));

        permissions.add(new PermissionItem(
            "READ_EXTERNAL_STORAGE",
            r(R.string.perm_read_external_storage),
            r(R.string.perm_desc_read_external_storage),
            r(R.string.perm_risk_read_external_storage),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ));

        permissions.add(new PermissionItem(
            "WRITE_EXTERNAL_STORAGE",
            r(R.string.perm_write_external_storage),
            r(R.string.perm_desc_write_external_storage),
            r(R.string.perm_risk_write_external_storage),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ));

        permissions.add(new PermissionItem(
            "ACCESS_MEDIA_LOCATION",
            r(R.string.perm_access_media_location),
            r(R.string.perm_desc_access_media_location),
            r(R.string.perm_risk_access_media_location),
            android.Manifest.permission.ACCESS_MEDIA_LOCATION
        ));

        permissions.add(new PermissionItem(
            "READ_PHONE_STATE",
            r(R.string.perm_read_phone_state),
            r(R.string.perm_desc_read_phone_state),
            r(R.string.perm_risk_read_phone_state),
            android.Manifest.permission.READ_PHONE_STATE
        ));

        permissions.add(new PermissionItem(
            "CALL_PHONE",
            r(R.string.perm_call_phone),
            r(R.string.perm_desc_call_phone),
            r(R.string.perm_risk_call_phone),
            android.Manifest.permission.CALL_PHONE
        ));

        permissions.add(new PermissionItem(
            "ANSWER_PHONE_CALLS",
            r(R.string.perm_answer_phone_calls),
            r(R.string.perm_desc_answer_phone_calls),
            r(R.string.perm_risk_answer_phone_calls),
            android.Manifest.permission.ANSWER_PHONE_CALLS
        ));

        permissions.add(new PermissionItem(
            "USE_FINGERPRINT",
            r(R.string.perm_use_fingerprint),
            r(R.string.perm_desc_use_fingerprint),
            r(R.string.perm_risk_use_fingerprint),
            android.Manifest.permission.USE_FINGERPRINT
        ));

        permissions.add(new PermissionItem(
            "USE_BIOMETRIC",
            r(R.string.perm_use_biometric),
            r(R.string.perm_desc_use_biometric),
            r(R.string.perm_risk_use_biometric),
            android.Manifest.permission.USE_BIOMETRIC
        ));

        permissions.add(new PermissionItem(
            "ACCESS_NOTIFICATIONS",
            r(R.string.perm_access_notifications),
            r(R.string.perm_desc_access_notifications),
            r(R.string.perm_risk_access_notifications),
            "android.permission.ACCESS_NOTIFICATIONS"
        ));

        permissions.add(new PermissionItem(
            "BIND_ACCESSIBILITY_SERVICE",
            r(R.string.perm_accessibility_service),
            r(R.string.perm_desc_accessibility_service),
            r(R.string.perm_risk_accessibility_service),
            "android.permission.BIND_ACCESSIBILITY_SERVICE"
        ));

        permissions.add(new PermissionItem(
            "BIND_VPN_SERVICE",
            r(R.string.perm_vpn_service),
            r(R.string.perm_desc_vpn_service),
            r(R.string.perm_risk_vpn_service),
            "android.permission.BIND_VPN_SERVICE"
        ));

        permissions.add(new PermissionItem(
            "SYSTEM_ALERT_WINDOW",
            r(R.string.perm_overlay_window),
            r(R.string.perm_desc_overlay_window),
            r(R.string.perm_risk_overlay_window),
            "android.permission.SYSTEM_ALERT_WINDOW"
        ));

        permissions.add(new PermissionItem(
            "PACKAGE_USAGE_STATS",
            r(R.string.perm_app_usage_stats),
            r(R.string.perm_desc_app_usage_stats),
            r(R.string.perm_risk_app_usage_stats),
            "android.permission.PACKAGE_USAGE_STATS"
        ));

        permissions.add(new PermissionItem(
            "READ_CALENDAR",
            r(R.string.perm_read_calendar),
            r(R.string.perm_desc_read_calendar),
            r(R.string.perm_risk_read_calendar),
            android.Manifest.permission.READ_CALENDAR
        ));

        permissions.add(new PermissionItem(
            "WRITE_CALENDAR",
            r(R.string.perm_write_calendar),
            r(R.string.perm_desc_write_calendar),
            r(R.string.perm_risk_write_calendar),
            android.Manifest.permission.WRITE_CALENDAR
        ));

        permissions.add(new PermissionItem(
            "READ_HISTORY_BOOKMARKS",
            r(R.string.perm_read_browser_history),
            r(R.string.perm_desc_read_browser_history),
            r(R.string.perm_risk_read_browser_history),
            "android.permission.READ_HISTORY_BOOKMARKS"
        ));

        permissions.add(new PermissionItem(
            "INSTALL_PACKAGES",
            r(R.string.perm_install_apps),
            r(R.string.perm_desc_install_apps),
            r(R.string.perm_risk_install_apps),
            "android.permission.INSTALL_PACKAGES"
        ));

        permissions.add(new PermissionItem(
            "DELETE_PACKAGES",
            r(R.string.perm_uninstall_apps),
            r(R.string.perm_desc_uninstall_apps),
            r(R.string.perm_risk_uninstall_apps),
            "android.permission.DELETE_PACKAGES"
        ));

        permissions.add(new PermissionItem(
            "ACCESS_WIFI_STATE",
            "WiFi State",
            "Allows the app to access WiFi connection information",
            "Risk: Location inference through WiFi networks",
            android.Manifest.permission.ACCESS_WIFI_STATE
        ));

        permissions.add(new PermissionItem(
            "CHANGE_WIFI_STATE",
            "Change WiFi State",
            "Allows the app to enable/disable WiFi",
            "Risk: Network manipulation",
            android.Manifest.permission.CHANGE_WIFI_STATE
        ));

        permissions.add(new PermissionItem(
            "BLUETOOTH",
            "Bluetooth Access",
            "Allows the app to access Bluetooth functionality",
            "Risk: Device tracking via Bluetooth",
            android.Manifest.permission.BLUETOOTH
        ));

        permissions.add(new PermissionItem(
            "BLUETOOTH_ADMIN",
            "Bluetooth Admin",
            "Allows the app to manage Bluetooth settings",
            "Risk: Bluetooth manipulation",
            android.Manifest.permission.BLUETOOTH_ADMIN
        ));

        permissions.add(new PermissionItem(
            "WAKE_LOCK",
            "Wake Lock",
            "Allows the app to keep the device awake",
            "Risk: Battery drain, background activity",
            android.Manifest.permission.WAKE_LOCK
        ));

        permissions.add(new PermissionItem(
            "INTERNET",
            "Internet Access",
            "Allows the app to access the internet",
            "Risk: Data transmission, privacy leakage",
            android.Manifest.permission.INTERNET
        ));

        permissions.add(new PermissionItem(
            "ACCESS_NETWORK_STATE",
            "Network State",
            "Allows the app to access network connection information",
            "Risk: Connection tracking",
            android.Manifest.permission.ACCESS_NETWORK_STATE
        ));

        permissions.add(new PermissionItem(
            "FOREGROUND_SERVICE",
            "Foreground Service",
            "Allows the app to run foreground services",
            "Risk: Persistent background activity",
            android.Manifest.permission.FOREGROUND_SERVICE
        ));

        permissions.add(new PermissionItem(
            "SCHEDULE_EXACT_ALARM",
            "Exact Alarm",
            "Allows the app to set exact alarm times",
            "Risk: Device wake-up manipulation",
            android.Manifest.permission.SCHEDULE_EXACT_ALARM
        ));

        permissions.add(new PermissionItem(
            "POST_NOTIFICATIONS",
            "Post Notifications",
            "Allows the app to display notifications",
            "Risk: Spam notifications, phishing",
            android.Manifest.permission.POST_NOTIFICATIONS
        ));

        permissions.add(new PermissionItem(
            "NEARBY_WIFI_DEVICES",
            "Nearby WiFi Devices",
            "Allows the app to discover nearby WiFi devices",
            "Risk: Device tracking, location inference",
            android.Manifest.permission.NEARBY_WIFI_DEVICES
        ));

        permissions.add(new PermissionItem(
            "READ_MEDIA_IMAGES",
            "Read Media Images",
            "Allows the app to read images from device storage",
            "Risk: Photo theft, privacy breach",
            android.Manifest.permission.READ_MEDIA_IMAGES
        ));

        permissions.add(new PermissionItem(
            "READ_MEDIA_VIDEO",
            "Read Media Video",
            "Allows the app to read videos from device storage",
            "Risk: Video theft, privacy breach",
            android.Manifest.permission.READ_MEDIA_VIDEO
        ));

        permissions.add(new PermissionItem(
            "READ_MEDIA_AUDIO",
            "Read Media Audio",
            "Allows the app to read audio files from device storage",
            "Risk: Audio theft, privacy breach",
            android.Manifest.permission.READ_MEDIA_AUDIO
        ));

        permissions.add(new PermissionItem(
            "MANAGE_EXTERNAL_STORAGE",
            "Manage External Storage",
            "Allows the app to manage all files on external storage",
            "Risk: Full storage access, data manipulation",
            "android.permission.MANAGE_EXTERNAL_STORAGE"
        ));

        permissions.add(new PermissionItem(
            "ACCESS_SUPERUSER",
            "Root Access",
            "Allows the app to run commands with root privileges",
            "Risk: Full system control, device compromise",
            "android.permission.ACCESS_SUPERUSER"
        ));

        permissions.add(new PermissionItem(
            "READ_LOGS",
            "Read Logs",
            "Allows the app to read system logs",
            "Risk: Log analysis, information leakage",
            "android.permission.READ_LOGS"
        ));

        permissions.add(new PermissionItem(
            "WRITE_SECURE_SETTINGS",
            "Modify Secure Settings",
            "Allows the app to modify secure system settings",
            "Risk: System configuration manipulation",
            "android.permission.WRITE_SECURE_SETTINGS"
        ));

        permissions.add(new PermissionItem(
            "SET_WALLPAPER",
            "Set Wallpaper",
            "Allows the app to set the device wallpaper",
            "Risk: Visual spoofing",
            android.Manifest.permission.SET_WALLPAPER
        ));

        permissions.add(new PermissionItem(
            "SET_WALLPAPER_HINTS",
            "Set Wallpaper Hints",
            "Allows the app to set wallpaper hints",
            "Risk: Visual manipulation",
            android.Manifest.permission.SET_WALLPAPER_HINTS
        ));

        for (PermissionItem item : permissions) {
            item.status = checkPermission(item.permission);
        }

        return permissions;
    }

    private String checkPermission(String permission) {
        if (permission == null) {
            return context.getString(R.string.status_unavailable);
        }
        try {
            int result = context.checkSelfPermission(permission);
            return result == PackageManager.PERMISSION_GRANTED ?
                context.getString(R.string.status_granted) :
                context.getString(R.string.status_denied);
        } catch (Exception e) {
            return context.getString(R.string.status_unavailable);
        }
    }

    public static class PermissionItem {
        public String name;
        public String displayName;
        public String description;
        public String risk;
        public String permission;
        public String status;

        public PermissionItem(String name, String displayName, String description, String risk, String permission) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
            this.risk = risk;
            this.permission = permission;
            this.status = "Denied";
        }
    }
}
