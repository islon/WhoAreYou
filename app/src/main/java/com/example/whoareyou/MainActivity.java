package com.example.whoareyou;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private LinearLayout infoContainer;
    private LinearLayout tabContainer;
    private TextView totalCountView;
    private TextView warningCountView;
    private TextView categoryCountView;
    private TextView tabFree;
    private TextView tabPermission;
    private boolean isFreeTabSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoContainer = findViewById(R.id.infoContainer);
        tabContainer = findViewById(R.id.tabContainer);
        totalCountView = findViewById(R.id.totalCount);
        warningCountView = findViewById(R.id.warningCount);
        categoryCountView = findViewById(R.id.categoryCount);

        setupTopButtons();
        addTabSwitch();
        loadFreeInfo();
    }

    private void setupTopButtons() {
        LinearLayout exportButton = findViewById(R.id.exportButton);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExportDialog();
            }
        });

        LinearLayout aboutButton = findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new android.content.Intent(MainActivity.this, AboutActivity.class));
            }
        });
    }

    private void addTabSwitch() {
        tabFree = findViewById(R.id.tabFree);
        tabPermission = findViewById(R.id.tabPermission);
        final LinearLayout tabFreeContainer = findViewById(R.id.tabFreeContainer);
        final LinearLayout tabPermissionContainer = findViewById(R.id.tabPermissionContainer);

        tabFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFreeTab(tabFreeContainer, tabPermissionContainer);
            }
        });

        tabPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToPermissionTab(tabFreeContainer, tabPermissionContainer);
            }
        });
    }

    private void switchToFreeTab(LinearLayout tabFreeContainer, LinearLayout tabPermissionContainer) {
        isFreeTabSelected = true;
        tabFree.setTextColor(Color.parseColor("#6366F1"));
        tabPermission.setTextColor(Color.parseColor("#9CA3AF"));
        tabFreeContainer.setBackgroundResource(R.drawable.tab_selected_bg);
        tabPermissionContainer.setBackgroundResource(R.drawable.tab_unselected_bg);

        infoContainer.removeAllViews();
        loadFreeInfo();
    }

    private void switchToPermissionTab(LinearLayout tabFreeContainer, LinearLayout tabPermissionContainer) {
        isFreeTabSelected = false;
        tabFree.setTextColor(Color.parseColor("#9CA3AF"));
        tabPermission.setTextColor(Color.parseColor("#6366F1"));
        tabFreeContainer.setBackgroundResource(R.drawable.tab_unselected_bg);
        tabPermissionContainer.setBackgroundResource(R.drawable.tab_selected_bg);

        infoContainer.removeAllViews();
        loadPermissionInfo();
    }

    private void loadFreeInfo() {
        DeviceInfoCollector collector = new DeviceInfoCollector(this);
        List<DeviceInfoCollector.InfoItem> infoList = collector.collectAllInfo();

        int totalCount = infoList.size();
        int warningCount = 0;
        Set<String> categories = new HashSet<>();

        for (DeviceInfoCollector.InfoItem item : infoList) {
            if (DeviceInfoCollector.InfoItem.LEVEL_WARNING.equals(item.level) ||
                DeviceInfoCollector.InfoItem.LEVEL_DANGER.equals(item.level)) {
                warningCount++;
            }
            categories.add(item.category);
        }

        totalCountView.setText(String.valueOf(totalCount));
        warningCountView.setText(String.valueOf(warningCount));
        categoryCountView.setText(String.valueOf(categories.size()));

        addFreeInfoTip();

        Map<String, List<DeviceInfoCollector.InfoItem>> groupedItems = new HashMap<>();
        for (DeviceInfoCollector.InfoItem item : infoList) {
            if (!groupedItems.containsKey(item.category)) {
                groupedItems.put(item.category, new java.util.ArrayList<>());
            }
            groupedItems.get(item.category).add(item);
        }

        String[] categoryOrder = {
            DeviceInfoCollector.InfoItem.CATEGORY_DEVICE,
            DeviceInfoCollector.InfoItem.CATEGORY_SYSTEM,
            DeviceInfoCollector.InfoItem.CATEGORY_NETWORK,
            DeviceInfoCollector.InfoItem.CATEGORY_STORAGE,
            DeviceInfoCollector.InfoItem.CATEGORY_DISPLAY,
            DeviceInfoCollector.InfoItem.CATEGORY_APP,
            DeviceInfoCollector.InfoItem.CATEGORY_SENSOR,
            DeviceInfoCollector.InfoItem.CATEGORY_SECURITY
        };

        for (String category : categoryOrder) {
            if (groupedItems.containsKey(category)) {
                addCategorySection(category, groupedItems.get(category));
            }
        }
    }

    private void addFreeInfoTip() {
        LinearLayout tipLayout = new LinearLayout(this);
        tipLayout.setOrientation(LinearLayout.VERTICAL);
        tipLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(8));

        GradientDrawable tipBg = new GradientDrawable();
        tipBg.setColor(Color.parseColor("#DBEAFE"));
        tipBg.setCornerRadius(dpToPx(12));
        tipBg.setStroke(2, Color.parseColor("#3B82F6"));

        LinearLayout tipCard = new LinearLayout(this);
        tipCard.setOrientation(LinearLayout.HORIZONTAL);
        tipCard.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
        tipCard.setBackground(tipBg);
        tipCard.setGravity(Gravity.CENTER_VERTICAL);

        TextView tipIcon = new TextView(this);
        tipIcon.setText("ℹ️");
        tipIcon.setTextSize(18);
        LinearLayout.LayoutParams tipIconParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tipIconParams.setMargins(0, 0, dpToPx(8), 0);
        tipIcon.setLayoutParams(tipIconParams);
        tipCard.addView(tipIcon);

        TextView tipText = new TextView(this);
        tipText.setText(R.string.tip_free_info);
        tipText.setTextSize(14);
        tipText.setTextColor(Color.parseColor("#1E40AF"));
        tipText.setTypeface(null, android.graphics.Typeface.BOLD);
        tipCard.addView(tipText);

        tipLayout.addView(tipCard);
        infoContainer.addView(tipLayout);
    }

    private void loadPermissionInfo() {
        PermissionCollector collector = new PermissionCollector(this);
        List<PermissionCollector.PermissionItem> permissions = collector.getAllPermissions();

        int totalCount = permissions.size();
        int grantedCount = 0;
        for (PermissionCollector.PermissionItem item : permissions) {
            if (getString(R.string.status_granted).equals(item.status)) {
                grantedCount++;
            }
        }

        totalCountView.setText(String.valueOf(totalCount));
        warningCountView.setText(String.valueOf(grantedCount));
        categoryCountView.setText(R.string.stat_permissions);

        addPermissionWarning(grantedCount);

        for (PermissionCollector.PermissionItem item : permissions) {
            addPermissionCard(item);
        }
    }

    private void addPermissionWarning(int grantedCount) {
        LinearLayout warningLayout = new LinearLayout(this);
        warningLayout.setOrientation(LinearLayout.VERTICAL);
        warningLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(8));

        GradientDrawable warningBg = new GradientDrawable();
        warningBg.setColor(Color.parseColor("#FEF3C7"));
        warningBg.setCornerRadius(dpToPx(12));
        warningBg.setStroke(2, Color.parseColor("#F59E0B"));

        LinearLayout warningCard = new LinearLayout(this);
        warningCard.setOrientation(LinearLayout.VERTICAL);
        warningCard.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));
        warningCard.setBackground(warningBg);

        TextView warningTitle = new TextView(this);
        warningTitle.setText(String.format(getString(R.string.permission_warning_title), grantedCount));
        warningTitle.setTextSize(14);
        warningTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        warningTitle.setTextColor(Color.parseColor("#92400E"));

        TextView warningText = new TextView(this);
        warningText.setText(R.string.tip_permission_warning);
        warningText.setTextSize(13);
        warningText.setTextColor(Color.parseColor("#78350F"));
        warningText.setLineSpacing(dpToPx(4), 1.3f);
        warningText.setPadding(0, dpToPx(4), 0, 0);

        warningCard.addView(warningTitle);
        warningCard.addView(warningText);
        warningLayout.addView(warningCard);

        infoContainer.addView(warningLayout);
    }

    private void addPermissionCard(PermissionCollector.PermissionItem item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(dpToPx(16));
        card.setBackground(cardBg);
        card.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
        card.setElevation(dpToPx(4));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(12));
        card.setLayoutParams(cardParams);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout iconContainer = new LinearLayout(this);
        iconContainer.setOrientation(LinearLayout.HORIZONTAL);
        iconContainer.setGravity(Gravity.CENTER);

        GradientDrawable iconBg = new GradientDrawable();
        boolean isGranted = getString(R.string.status_granted).equals(item.status);
        if (isGranted) {
            iconBg.setColor(Color.parseColor("#FEE2E2"));
        } else {
            iconBg.setColor(Color.parseColor("#D1FAE5"));
        }
        iconBg.setShape(GradientDrawable.OVAL);
        iconContainer.setBackground(iconBg);

        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(44), dpToPx(44));
        iconParams.setMargins(0, 0, dpToPx(16), 0);
        iconContainer.setLayoutParams(iconParams);

        String iconText = item.name.substring(0, Math.min(item.name.length(), 2));
        TextView iconView = new TextView(this);
        iconView.setText(iconText);
        iconView.setTextSize(20);
        iconContainer.addView(iconView);

        header.addView(iconContainer);

        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        ));

        TextView nameView = new TextView(this);
        nameView.setText(item.displayName);
        nameView.setTextSize(17);
        nameView.setTextColor(Color.parseColor("#1F2937"));
        nameView.setTypeface(null, android.graphics.Typeface.BOLD);
        textContainer.addView(nameView);

        TextView permView = new TextView(this);
        permView.setText(item.permission);
        permView.setTextSize(11);
        permView.setTextColor(Color.parseColor("#9CA3AF"));
        permView.setPadding(0, dpToPx(2), 0, 0);
        textContainer.addView(permView);

        header.addView(textContainer);

        TextView statusTag = new TextView(this);
        statusTag.setText(isGranted ? R.string.status_granted : R.string.status_denied);
        statusTag.setTextSize(10);
        statusTag.setTextColor(Color.WHITE);
        statusTag.setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4));
        statusTag.setGravity(Gravity.CENTER);

        GradientDrawable tagBg = new GradientDrawable();
        tagBg.setCornerRadius(dpToPx(12));
        if (isGranted) {
            tagBg.setColor(Color.parseColor("#EF4444"));
        } else {
            tagBg.setColor(Color.parseColor("#10B981"));
        }
        statusTag.setBackground(tagBg);

        header.addView(statusTag);
        card.addView(header);

        View divider = new View(this);
        GradientDrawable dividerBg = new GradientDrawable();
        dividerBg.setColor(Color.parseColor("#F3F4F6"));
        dividerBg.setCornerRadius(dpToPx(1));
        divider.setBackground(dividerBg);

        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(2)
        );
        dividerParams.setMargins(0, dpToPx(16), 0, dpToPx(12));
        divider.setLayoutParams(dividerParams);
        card.addView(divider);

        TextView valueView = new TextView(this);
        valueView.setText(item.status);
        valueView.setTextSize(15);
        valueView.setTextColor(Color.parseColor("#374151"));
        valueView.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(valueView);

        LinearLayout descContainer = new LinearLayout(this);
        descContainer.setOrientation(LinearLayout.HORIZONTAL);
        descContainer.setGravity(Gravity.TOP);
        descContainer.setPadding(0, dpToPx(8), 0, 0);

        TextView descIcon = new TextView(this);
        descIcon.setText("📋");
        descIcon.setTextSize(14);
        LinearLayout.LayoutParams descIconParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        descIconParams.setMargins(0, 0, dpToPx(8), 0);
        descIcon.setLayoutParams(descIconParams);
        descContainer.addView(descIcon);

        TextView descView = new TextView(this);
        descView.setText(item.description);
        descView.setTextSize(13);
        descView.setTextColor(Color.parseColor("#6B7280"));
        descView.setLineSpacing(dpToPx(3), 1.2f);
        descView.setLayoutParams(new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        ));
        descContainer.addView(descView);

        card.addView(descContainer);

        if (item.risk != null && !item.risk.isEmpty()) {
            LinearLayout riskLayout = new LinearLayout(this);
            riskLayout.setOrientation(LinearLayout.VERTICAL);
            riskLayout.setPadding(0, dpToPx(12), 0, 0);

            GradientDrawable riskBg = new GradientDrawable();
            riskBg.setColor(Color.parseColor("#FEF2F2"));
            riskBg.setCornerRadius(dpToPx(12));
            riskBg.setStroke(2, Color.parseColor("#FECACA"));

            LinearLayout riskCard = new LinearLayout(this);
            riskCard.setOrientation(LinearLayout.HORIZONTAL);
            riskCard.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
            riskCard.setBackground(riskBg);
            riskCard.setGravity(Gravity.TOP);

            TextView riskIcon = new TextView(this);
            riskIcon.setText("⚠️");
            riskIcon.setTextSize(16);
            LinearLayout.LayoutParams riskIconParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            riskIconParams.setMargins(0, 0, dpToPx(8), 0);
            riskIcon.setLayoutParams(riskIconParams);
            riskCard.addView(riskIcon);

            TextView riskView = new TextView(this);
            riskView.setText(item.risk);
            riskView.setTextSize(13);
            riskView.setTextColor(Color.parseColor("#B91C1C"));
            riskView.setLineSpacing(dpToPx(3), 1.2f);
            riskView.setTypeface(null, android.graphics.Typeface.BOLD);
            riskView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
            ));
            riskCard.addView(riskView);

            riskLayout.addView(riskCard);
            card.addView(riskLayout);
        }

        infoContainer.addView(card);
    }

    private LinearLayout createActionButton(String icon, String text, int color, View.OnClickListener listener) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        container.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(dpToPx(12));
        container.setBackground(bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        container.setLayoutParams(params);

        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(24);
        iconView.setGravity(Gravity.CENTER);

        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(13);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setPadding(0, dpToPx(4), 0, 0);

        container.addView(iconView);
        container.addView(textView);

        container.setOnClickListener(listener);

        return container;
    }

    private void showExportDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.export_title);
        builder.setMessage(R.string.export_message);
        builder.setPositiveButton(R.string.export_confirm, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                performExport();
            }
        });
        builder.setNegativeButton(R.string.export_cancel, null);
        builder.show();
    }

    private void performExport() {
        try {
            StringBuilder content = new StringBuilder();
            content.append("========================================\n");
            content.append("      Who Are You - Device Info Report\n");
            content.append("========================================\n");
            content.append("Generated: ").append(java.time.LocalDateTime.now()).append("\n");

            if (isFreeTabSelected) {
                content.append("Data Type: No Permission Required\n");
                content.append("========================================\n\n");

                DeviceInfoCollector collector = new DeviceInfoCollector(this);
                List<DeviceInfoCollector.InfoItem> infoList = collector.collectAllInfo();

                Map<String, List<DeviceInfoCollector.InfoItem>> groupedItems = new HashMap<>();
                for (DeviceInfoCollector.InfoItem item : infoList) {
                    if (!groupedItems.containsKey(item.category)) {
                        groupedItems.put(item.category, new java.util.ArrayList<>());
                    }
                    groupedItems.get(item.category).add(item);
                }

                String[] categoryOrder = {
                    DeviceInfoCollector.InfoItem.CATEGORY_DEVICE,
                    DeviceInfoCollector.InfoItem.CATEGORY_SYSTEM,
                    DeviceInfoCollector.InfoItem.CATEGORY_NETWORK,
                    DeviceInfoCollector.InfoItem.CATEGORY_STORAGE,
                    DeviceInfoCollector.InfoItem.CATEGORY_DISPLAY,
                    DeviceInfoCollector.InfoItem.CATEGORY_APP,
                    DeviceInfoCollector.InfoItem.CATEGORY_SENSOR,
                    DeviceInfoCollector.InfoItem.CATEGORY_SECURITY
                };

                for (String category : categoryOrder) {
                    if (groupedItems.containsKey(category)) {
                        content.append("[").append(category).append("]\n");
                        content.append("----------------------------------------\n");
                        for (DeviceInfoCollector.InfoItem item : groupedItems.get(category)) {
                            String level = "";
                            switch (item.level) {
                                case DeviceInfoCollector.InfoItem.LEVEL_DANGER:
                                    level = "[High Risk]";
                                    break;
                                case DeviceInfoCollector.InfoItem.LEVEL_WARNING:
                                    level = "[Medium Risk]";
                                    break;
                            }
                            content.append("• ").append(item.name).append(level).append("\n");
                            content.append("  Value: ").append(item.value).append("\n");
                            content.append("  Usage: ").append(item.usage).append("\n\n");
                        }
                    }
                }
            } else {
                content.append("Data Type: Permission Required\n");
                content.append("========================================\n\n");

                PermissionCollector collector = new PermissionCollector(this);
                List<PermissionCollector.PermissionItem> permissions = collector.getAllPermissions();

                int grantedCount = 0;
                for (PermissionCollector.PermissionItem item : permissions) {
                    if (getString(R.string.status_granted).equals(item.status)) {
                        grantedCount++;
                    }
                }

                content.append("⚠️ Permission Overview\n");
                content.append("----------------------------------------\n");
                content.append("Total Permissions: ").append(permissions.size()).append("\n");
                content.append("Granted: ").append(grantedCount).append("\n");
                content.append("Denied: ").append(permissions.size() - grantedCount).append("\n\n");

                content.append("[Permission Details]\n");
                content.append("----------------------------------------\n");
                for (PermissionCollector.PermissionItem item : permissions) {
                    content.append("• ").append(item.displayName).append("\n");
                    content.append("  Status: ").append(item.status).append("\n");
                    content.append("  Permission: ").append(item.permission).append("\n");
                    content.append("  Description: ").append(item.description).append("\n");
                    if (item.risk != null && !item.risk.isEmpty()) {
                        content.append("  Risk: ").append(item.risk).append("\n");
                    }
                    content.append("\n");
                }
            }

            content.append("========================================\n");
            content.append("End of Report\n");
            content.append("========================================\n");

            File exportDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "WhoAreYou");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            String fileName = "Device_Report_" +
                java.time.LocalDateTime.now().toString().replace(":", "-") + ".txt";
            File file = new File(exportDir, fileName);

            try (PrintWriter writer = new PrintWriter(new FileOutputStream(file))) {
                writer.print(content.toString());
            }

            Toast.makeText(this, getString(R.string.export_success) + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.export_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void addCategorySection(String category, List<DeviceInfoCollector.InfoItem> items) {
        TextView categoryTitle = new TextView(this);
        categoryTitle.setText(getCategoryIcon(category) + " " + category);
        categoryTitle.setTextSize(16);
        categoryTitle.setTextColor(Color.parseColor("#374151"));
        categoryTitle.setTypeface(null, android.graphics.Typeface.BOLD);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, dpToPx(16), 0, dpToPx(8));
        categoryTitle.setLayoutParams(titleParams);
        infoContainer.addView(categoryTitle);

        for (DeviceInfoCollector.InfoItem item : items) {
            addInfoCard(item);
        }
    }

    private void addInfoCard(DeviceInfoCollector.InfoItem item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable cardBackground = new GradientDrawable();
        cardBackground.setColor(Color.WHITE);
        cardBackground.setCornerRadius(dpToPx(16));
        card.setBackground(cardBackground);
        card.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
        card.setElevation(dpToPx(4));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(12));
        card.setLayoutParams(cardParams);

        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout iconContainer = new LinearLayout(this);
        iconContainer.setOrientation(LinearLayout.HORIZONTAL);
        iconContainer.setGravity(Gravity.CENTER);
        int iconColor = getLevelColor(item.level);

        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setColor(getLightColor(iconColor));
        iconBg.setShape(GradientDrawable.OVAL);
        iconContainer.setBackground(iconBg);

        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(44), dpToPx(44));
        iconParams.setMargins(0, 0, dpToPx(16), 0);
        iconContainer.setLayoutParams(iconParams);

        TextView iconText = new TextView(this);
        iconText.setText(getCategoryIcon(item.category));
        iconText.setTextSize(20);
        iconContainer.addView(iconText);

        headerRow.addView(iconContainer);

        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        ));

        TextView nameText = new TextView(this);
        nameText.setText(item.name);
        nameText.setTextSize(13);
        nameText.setTextColor(Color.parseColor("#9CA3AF"));
        nameText.setLetterSpacing(0.05f);
        textContainer.addView(nameText);

        TextView valueText = new TextView(this);
        valueText.setText(item.value);
        valueText.setTextSize(17);
        valueText.setTextColor(Color.parseColor("#1F2937"));
        valueText.setTypeface(null, android.graphics.Typeface.BOLD);
        valueText.setPadding(0, dpToPx(4), 0, 0);
        textContainer.addView(valueText);

        headerRow.addView(textContainer);

        TextView levelTag = new TextView(this);
        levelTag.setTextSize(10);
        levelTag.setTextColor(Color.WHITE);
        levelTag.setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4));
        levelTag.setGravity(Gravity.CENTER);

        GradientDrawable tagBg = new GradientDrawable();
        tagBg.setColor(iconColor);
        tagBg.setCornerRadius(dpToPx(12));
        levelTag.setBackground(tagBg);

        LinearLayout.LayoutParams tagParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tagParams.setMargins(dpToPx(8), 0, 0, 0);
        levelTag.setLayoutParams(tagParams);

        if (DeviceInfoCollector.InfoItem.LEVEL_DANGER.equals(item.level)) {
            levelTag.setText(R.string.risk_high);
        } else if (DeviceInfoCollector.InfoItem.LEVEL_WARNING.equals(item.level)) {
            levelTag.setText(R.string.risk_medium);
        } else {
            levelTag.setText(R.string.risk_low);
        }
        headerRow.addView(levelTag);

        card.addView(headerRow);

        View divider = new View(this);
        GradientDrawable dividerBg = new GradientDrawable();
        dividerBg.setColor(Color.parseColor("#F3F4F6"));
        dividerBg.setCornerRadius(dpToPx(1));
        divider.setBackground(dividerBg);

        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(2)
        );
        dividerParams.setMargins(0, dpToPx(16), 0, dpToPx(12));
        divider.setLayoutParams(dividerParams);
        card.addView(divider);

        LinearLayout usageContainer = new LinearLayout(this);
        usageContainer.setOrientation(LinearLayout.HORIZONTAL);
        usageContainer.setGravity(Gravity.TOP);

        TextView usageIcon = new TextView(this);
        usageIcon.setText("💡");
        usageIcon.setTextSize(14);
        LinearLayout.LayoutParams usageIconParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        usageIconParams.setMargins(0, 0, dpToPx(8), 0);
        usageIcon.setLayoutParams(usageIconParams);
        usageContainer.addView(usageIcon);

        TextView usageText = new TextView(this);
        usageText.setText(item.usage);
        usageText.setTextSize(13);
        usageText.setTextColor(Color.parseColor("#6B7280"));
        usageText.setLineSpacing(dpToPx(3), 1.2f);
        usageText.setLayoutParams(new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        ));
        usageContainer.addView(usageText);

        card.addView(usageContainer);
        infoContainer.addView(card);
    }

    private int getLightColor(int color) {
        return Color.argb(30, Color.red(color), Color.green(color), Color.blue(color));
    }

    private int getLevelColor(String level) {
        switch (level) {
            case DeviceInfoCollector.InfoItem.LEVEL_DANGER:
                return Color.parseColor("#EF4444");
            case DeviceInfoCollector.InfoItem.LEVEL_WARNING:
                return Color.parseColor("#F59E0B");
            default:
                return Color.parseColor("#3B82F6");
        }
    }

    private String getCategoryIcon(String category) {
        switch (category) {
            case DeviceInfoCollector.InfoItem.CATEGORY_DEVICE:
                return "📱";
            case DeviceInfoCollector.InfoItem.CATEGORY_SYSTEM:
                return "⚙️";
            case DeviceInfoCollector.InfoItem.CATEGORY_NETWORK:
                return "🌐";
            case DeviceInfoCollector.InfoItem.CATEGORY_STORAGE:
                return "💾";
            case DeviceInfoCollector.InfoItem.CATEGORY_DISPLAY:
                return "📺";
            case DeviceInfoCollector.InfoItem.CATEGORY_APP:
                return "📦";
            case DeviceInfoCollector.InfoItem.CATEGORY_SENSOR:
                return "🔬";
            case DeviceInfoCollector.InfoItem.CATEGORY_SECURITY:
                return "🛡️";
            default:
                return "📋";
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}