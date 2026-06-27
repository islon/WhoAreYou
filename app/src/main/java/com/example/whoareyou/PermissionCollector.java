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

    public List<PermissionItem> getAllPermissions() {
        List<PermissionItem> permissions = new ArrayList<>();

        permissions.add(new PermissionItem(
            "ACCESS_FINE_LOCATION",
            "Precise Location",
            "Allows the app to access precise location information (GPS, WiFi, cell towers). If granted, the app can track your exact location at any time.",
            "Risk: Location tracking, targeted advertising, stalking. Attackers can use location data to track your movements, know where you live/work, and predict your habits.",
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ));

        permissions.add(new PermissionItem(
            "ACCESS_COARSE_LOCATION",
            "Approximate Location",
            "Allows the app to access approximate location information (WiFi, cell towers). Less precise than fine location but can still determine your approximate location.",
            "Risk: Regional targeting, approximate tracking. Can determine which city you are in, which may be combined with other data for more precise positioning.",
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ));

        permissions.add(new PermissionItem(
            "ACCESS_BACKGROUND_LOCATION",
            "Background Location",
            "Allows the app to access location information even when the app is running in the background. This is more dangerous than foreground location access.",
            "Risk: Continuous tracking, privacy invasion. The app can track your location 24/7 without your knowledge, recording all your movements.",
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ));

        permissions.add(new PermissionItem(
            "READ_CONTACTS",
            "Read Contacts",
            "Allows the app to read all contact information on your device, including phone numbers, email addresses, and names.",
            "Risk: Contact list theft, social engineering. Attackers can steal your entire contact list for spam, phishing, or selling to third parties.",
            android.Manifest.permission.READ_CONTACTS
        ));

        permissions.add(new PermissionItem(
            "WRITE_CONTACTS",
            "Modify Contacts",
            "Allows the app to add, delete, or modify contact information on your device.",
            "Risk: Contact tampering, malware injection. Rogue apps can modify your contacts to redirect calls/messages to malicious numbers.",
            android.Manifest.permission.WRITE_CONTACTS
        ));

        permissions.add(new PermissionItem(
            "READ_SMS",
            "Read SMS",
            "Allows the app to read all SMS messages on your device, including verification codes, personal conversations, and financial information.",
            "Risk: Verification code theft, privacy breach. Attackers can intercept SMS verification codes for account takeover attacks.",
            android.Manifest.permission.READ_SMS
        ));

        permissions.add(new PermissionItem(
            "SEND_SMS",
            "Send SMS",
            "Allows the app to send SMS messages from your device without your knowledge.",
            "Risk: Spam sending, fee fraud. Rogue apps can send premium SMS messages to rack up charges on your phone bill.",
            android.Manifest.permission.SEND_SMS
        ));

        permissions.add(new PermissionItem(
            "RECEIVE_SMS",
            "Receive SMS",
            "Allows the app to intercept incoming SMS messages.",
            "Risk: Message interception, verification code theft. Can intercept all incoming messages including sensitive verification codes.",
            android.Manifest.permission.RECEIVE_SMS
        ));

        permissions.add(new PermissionItem(
            "READ_CALL_LOG",
            "Read Call Log",
            "Allows the app to read all call history on your device, including incoming/outgoing calls and call duration.",
            "Risk: Privacy breach, social graph building. Attackers can analyze your call patterns to build social relationships and identify important contacts.",
            android.Manifest.permission.READ_CALL_LOG
        ));

        permissions.add(new PermissionItem(
            "WRITE_CALL_LOG",
            "Modify Call Log",
            "Allows the app to add, delete, or modify call history on your device.",
            "Risk: Call log tampering, evidence destruction. Rogue apps can delete call records to hide malicious activities.",
            android.Manifest.permission.WRITE_CALL_LOG
        ));

        permissions.add(new PermissionItem(
            "RECORD_AUDIO",
            "Microphone Access",
            "Allows the app to access the device microphone and record audio at any time.",
            "Risk: Eavesdropping, voice recording. Attackers can record your conversations, ambient sounds, and even your voiceprint.",
            android.Manifest.permission.RECORD_AUDIO
        ));

        permissions.add(new PermissionItem(
            "CAMERA",
            "Camera Access",
            "Allows the app to access the device camera and take photos/videos at any time.",
            "Risk: Secret photography, surveillance. Rogue apps can take photos or record videos without your knowledge, potentially capturing sensitive information.",
            android.Manifest.permission.CAMERA
        ));

        permissions.add(new PermissionItem(
            "READ_EXTERNAL_STORAGE",
            "Read Storage",
            "Allows the app to read all files on your device's external storage, including photos, documents, and downloads.",
            "Risk: File theft, privacy breach. Attackers can access all your personal files, photos, documents, and sensitive data.",
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ));

        permissions.add(new PermissionItem(
            "WRITE_EXTERNAL_STORAGE",
            "Write Storage",
            "Allows the app to write files to your device's external storage.",
            "Risk: Malware installation, data corruption. Rogue apps can install malware, modify system files, or corrupt your data.",
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ));

        permissions.add(new PermissionItem(
            "ACCESS_MEDIA_LOCATION",
            "Media Location",
            "Allows the app to access location information embedded in photos and videos.",
            "Risk: Location exposure from media. Attackers can extract location information from your photos to track your movements.",
            android.Manifest.permission.ACCESS_MEDIA_LOCATION
        ));

        permissions.add(new PermissionItem(
            "READ_PHONE_STATE",
            "Phone State",
            "Allows the app to read phone state information, including phone number, IMEI, SIM card information, and call status.",
            "Risk: Device fingerprinting, IMEI tracking. Attackers can use IMEI for device tracking and identification across different apps.",
            android.Manifest.permission.READ_PHONE_STATE
        ));

        permissions.add(new PermissionItem(
            "CALL_PHONE",
            "Make Calls",
            "Allows the app to make phone calls without user confirmation.",
            "Risk: Unauthorized calls, fee fraud. Rogue apps can make premium calls or international calls to rack up charges.",
            android.Manifest.permission.CALL_PHONE
        ));

        permissions.add(new PermissionItem(
            "ANSWER_PHONE_CALLS",
            "Answer Calls",
            "Allows the app to answer incoming phone calls automatically.",
            "Risk: Call hijacking, fraud. Rogue apps can intercept and answer calls to steal sensitive information.",
            android.Manifest.permission.ANSWER_PHONE_CALLS
        ));

        permissions.add(new PermissionItem(
            "USE_FINGERPRINT",
            "Fingerprint Access",
            "Allows the app to access the device's fingerprint sensor for biometric authentication.",
            "Risk: Biometric data theft, authentication bypass. Attackers can potentially steal fingerprint data or use it for unauthorized access.",
            android.Manifest.permission.USE_FINGERPRINT
        ));

        permissions.add(new PermissionItem(
            "USE_BIOMETRIC",
            "Biometric Access",
            "Allows the app to access biometric authentication features (fingerprint, face, iris).",
            "Risk: Biometric data theft, unauthorized authentication. Compromised biometric data cannot be changed like passwords.",
            android.Manifest.permission.USE_BIOMETRIC
        ));

        permissions.add(new PermissionItem(
            "ACCESS_NOTIFICATIONS",
            "Notification Access",
            "Allows the app to read all notifications on your device, including messages, emails, and app alerts.",
            "Risk: Notification spying, privacy breach. Attackers can read all your notifications to access sensitive information.",
            "android.permission.ACCESS_NOTIFICATIONS"
        ));

        permissions.add(new PermissionItem(
            "BIND_ACCESSIBILITY_SERVICE",
            "Accessibility Service",
            "Allows the app to act as an accessibility service, which can monitor and control user interactions with the device.",
            "Risk: Full device control, keylogging. Accessibility services can monitor all user actions, capture keystrokes, and control the device remotely.",
            "android.permission.BIND_ACCESSIBILITY_SERVICE"
        ));

        permissions.add(new PermissionItem(
            "BIND_VPN_SERVICE",
            "VPN Service",
            "Allows the app to create a VPN connection, which can route all network traffic through its servers.",
            "Risk: Traffic interception, man-in-the-middle attacks. A malicious VPN can intercept and monitor all your network traffic.",
            "android.permission.BIND_VPN_SERVICE"
        ));

        permissions.add(new PermissionItem(
            "SYSTEM_ALERT_WINDOW",
            "Overlay Window",
            "Allows the app to display overlays on top of other apps, including the lock screen.",
            "Risk: UI spoofing, phishing attacks. Rogue apps can create fake login screens to steal passwords.",
            "android.permission.SYSTEM_ALERT_WINDOW"
        ));

        permissions.add(new PermissionItem(
            "PACKAGE_USAGE_STATS",
            "App Usage Stats",
            "Allows the app to access detailed app usage statistics, including which apps you use and for how long.",
            "Risk: Usage tracking, habit profiling. Attackers can analyze your app usage patterns to understand your habits and interests.",
            "android.permission.PACKAGE_USAGE_STATS"
        ));

        permissions.add(new PermissionItem(
            "READ_CALENDAR",
            "Read Calendar",
            "Allows the app to read all calendar events on your device.",
            "Risk: Schedule exposure, privacy breach. Attackers can know your schedule, appointments, and travel plans.",
            android.Manifest.permission.READ_CALENDAR
        ));

        permissions.add(new PermissionItem(
            "WRITE_CALENDAR",
            "Modify Calendar",
            "Allows the app to add, delete, or modify calendar events on your device.",
            "Risk: Schedule tampering, social engineering. Rogue apps can add fake events to trick you into attending malicious meetings.",
            android.Manifest.permission.WRITE_CALENDAR
        ));

        permissions.add(new PermissionItem(
            "READ_HISTORY_BOOKMARKS",
            "Read Browser History",
            "Allows the app to read your browser history and bookmarks.",
            "Risk: Browsing history exposure, interest profiling. Attackers can know all websites you've visited and build detailed user profiles.",
            "android.permission.READ_HISTORY_BOOKMARKS"
        ));

        permissions.add(new PermissionItem(
            "INSTALL_PACKAGES",
            "Install Apps",
            "Allows the app to install other applications without user confirmation.",
            "Risk: Malware installation, device takeover. Rogue apps can install malicious software to take control of your device.",
            "android.permission.INSTALL_PACKAGES"
        ));

        permissions.add(new PermissionItem(
            "DELETE_PACKAGES",
            "Uninstall Apps",
            "Allows the app to uninstall other applications without user confirmation.",
            "Risk: App removal, system disruption. Rogue apps can uninstall security apps or important system applications.",
            "android.permission.DELETE_PACKAGES"
        ));

        permissions.add(new PermissionItem(
            "ACCESS_WIFI_STATE",
            "WiFi State",
            "Allows the app to access WiFi connection information, including SSID, BSSID, and connection status.",
            "Risk: Location inference, network tracking. WiFi information can be used to infer your location and track your movements.",
            android.Manifest.permission.ACCESS_WIFI_STATE
        ));

        permissions.add(new PermissionItem(
            "CHANGE_WIFI_STATE",
            "Change WiFi State",
            "Allows the app to enable/disable WiFi and connect to WiFi networks.",
            "Risk: Network manipulation, connection hijacking. Rogue apps can connect to malicious WiFi networks to intercept traffic.",
            android.Manifest.permission.CHANGE_WIFI_STATE
        ));

        permissions.add(new PermissionItem(
            "BLUETOOTH",
            "Bluetooth Access",
            "Allows the app to access Bluetooth functionality, including discovering and connecting to nearby devices.",
            "Risk: Device tracking, data theft. Attackers can discover nearby Bluetooth devices and potentially steal data.",
            android.Manifest.permission.BLUETOOTH
        ));

        permissions.add(new PermissionItem(
            "BLUETOOTH_ADMIN",
            "Bluetooth Admin",
            "Allows the app to manage Bluetooth settings, including pairing and unpairing devices.",
            "Risk: Bluetooth manipulation, unauthorized pairing. Rogue apps can pair with malicious devices or disconnect legitimate ones.",
            android.Manifest.permission.BLUETOOTH_ADMIN
        ));

        permissions.add(new PermissionItem(
            "WAKE_LOCK",
            "Wake Lock",
            "Allows the app to keep the device awake even when the screen is off.",
            "Risk: Battery drain, background activity. Rogue apps can keep the device awake to perform malicious activities in the background.",
            android.Manifest.permission.WAKE_LOCK
        ));

        permissions.add(new PermissionItem(
            "INTERNET",
            "Internet Access",
            "Allows the app to access the internet and send/receive data.",
            "Risk: Data transmission, privacy leakage. Without proper encryption, data can be intercepted during transmission.",
            android.Manifest.permission.INTERNET
        ));

        permissions.add(new PermissionItem(
            "ACCESS_NETWORK_STATE",
            "Network State",
            "Allows the app to access network connection information, including whether connected to WiFi or mobile data.",
            "Risk: Connection tracking, usage monitoring. Can determine your network type and connection status for tracking purposes.",
            android.Manifest.permission.ACCESS_NETWORK_STATE
        ));

        permissions.add(new PermissionItem(
            "FOREGROUND_SERVICE",
            "Foreground Service",
            "Allows the app to run foreground services, which continue running even when the app is not in the foreground.",
            "Risk: Persistent background activity, resource consumption. Rogue apps can run malicious services continuously.",
            android.Manifest.permission.FOREGROUND_SERVICE
        ));

        permissions.add(new PermissionItem(
            "SCHEDULE_EXACT_ALARM",
            "Exact Alarm",
            "Allows the app to set exact alarm times, which can wake up the device at specific moments.",
            "Risk: Device wake-up, privacy invasion. Can wake up device at specific times to perform activities without user knowledge.",
            android.Manifest.permission.SCHEDULE_EXACT_ALARM
        ));

        permissions.add(new PermissionItem(
            "POST_NOTIFICATIONS",
            "Post Notifications",
            "Allows the app to display notifications to the user.",
            "Risk: Spam notifications, phishing. Rogue apps can send fake notifications to trick users into clicking malicious links.",
            android.Manifest.permission.POST_NOTIFICATIONS
        ));

        permissions.add(new PermissionItem(
            "NEARBY_WIFI_DEVICES",
            "Nearby WiFi Devices",
            "Allows the app to discover nearby WiFi devices.",
            "Risk: Device tracking, location inference. Can discover nearby devices and infer user location through WiFi signals.",
            android.Manifest.permission.NEARBY_WIFI_DEVICES
        ));

        permissions.add(new PermissionItem(
            "READ_MEDIA_IMAGES",
            "Read Media Images",
            "Allows the app to read images from device storage.",
            "Risk: Photo theft, privacy breach. Attackers can access all your photos including sensitive ones.",
            android.Manifest.permission.READ_MEDIA_IMAGES
        ));

        permissions.add(new PermissionItem(
            "READ_MEDIA_VIDEO",
            "Read Media Video",
            "Allows the app to read videos from device storage.",
            "Risk: Video theft, privacy breach. Attackers can access all your videos including sensitive ones.",
            android.Manifest.permission.READ_MEDIA_VIDEO
        ));

        permissions.add(new PermissionItem(
            "READ_MEDIA_AUDIO",
            "Read Media Audio",
            "Allows the app to read audio files from device storage.",
            "Risk: Audio theft, privacy breach. Attackers can access all your audio recordings including voice memos.",
            android.Manifest.permission.READ_MEDIA_AUDIO
        ));

        permissions.add(new PermissionItem(
            "MANAGE_EXTERNAL_STORAGE",
            "Manage External Storage",
            "Allows the app to manage all files on external storage, including access to all directories.",
            "Risk: Full storage access, data manipulation. Rogue apps can access and modify all files on your device.",
            "android.permission.MANAGE_EXTERNAL_STORAGE"
        ));

        permissions.add(new PermissionItem(
            "ACCESS_SUPERUSER",
            "Root Access",
            "Allows the app to run commands with root privileges.",
            "Risk: Full system control, device compromise. Root access allows complete control over the device, including modifying system files and bypassing security.",
            "android.permission.ACCESS_SUPERUSER"
        ));

        permissions.add(new PermissionItem(
            "READ_LOGS",
            "Read Logs",
            "Allows the app to read system logs, which may contain sensitive information.",
            "Risk: Log analysis, information leakage. Attackers can read system logs to find sensitive information and debug data.",
            "android.permission.READ_LOGS"
        ));

        permissions.add(new PermissionItem(
            "WRITE_SECURE_SETTINGS",
            "Modify Secure Settings",
            "Allows the app to modify secure system settings.",
            "Risk: System configuration manipulation. Rogue apps can modify system settings to disable security features.",
            "android.permission.WRITE_SECURE_SETTINGS"
        ));

        permissions.add(new PermissionItem(
            "SET_WALLPAPER",
            "Set Wallpaper",
            "Allows the app to set the device wallpaper.",
            "Risk: Visual spoofing. Rogue apps can change wallpaper to display misleading information.",
            android.Manifest.permission.SET_WALLPAPER
        ));

        permissions.add(new PermissionItem(
            "SET_WALLPAPER_HINTS",
            "Set Wallpaper Hints",
            "Allows the app to set wallpaper hints.",
            "Risk: Visual manipulation. Can affect how wallpapers are displayed on the device.",
            android.Manifest.permission.SET_WALLPAPER_HINTS
        ));

        for (PermissionItem item : permissions) {
            item.status = checkPermission(item.permission);
        }

        return permissions;
    }

    private String checkPermission(String permission) {
        if (permission == null) {
            return "Vendor Specific";
        }
        try {
            int result = context.checkSelfPermission(permission);
            return result == PackageManager.PERMISSION_GRANTED ? "Granted" : "Denied";
        } catch (Exception e) {
            return "Vendor Specific";
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