package com.example.whoareyou;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionActivity extends AppCompatActivity {

    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        container = findViewById(R.id.permissionContainer);

        addHeader();

        PermissionCollector collector = new PermissionCollector(this);
        List<PermissionCollector.PermissionItem> permissions = collector.getAllPermissions();

        int totalCount = permissions.size();
        int grantedCount = 0;
        for (PermissionCollector.PermissionItem item : permissions) {
            if (getString(R.string.perm_granted).equals(item.status)) {
                grantedCount++;
            }
        }

        addStats(totalCount, grantedCount);
        addWarning();
        addPermissionList(permissions);
        addBackButton();
    }

    private void addHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundResource(R.drawable.header_gradient);
        header.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(32));

        TextView title = new TextView(this);
        title.setText(R.string.perm_activity_title);
        title.setTextSize(28);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText(R.string.perm_activity_subtitle);
        subtitle.setTextSize(14);
        subtitle.setTextColor(Color.parseColor("#E0E0E0"));
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dpToPx(8), 0, 0);

        header.addView(title);
        header.addView(subtitle);
        container.addView(header);
    }

    private void addStats(int total, int granted) {
        LinearLayout statsLayout = new LinearLayout(this);
        statsLayout.setOrientation(LinearLayout.HORIZONTAL);
        statsLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        statsLayout.setBackgroundColor(Color.WHITE);

        LinearLayout totalLayout = createStatItem(String.valueOf(total), getString(R.string.perm_activity_stat_permissions), Color.parseColor("#6366F1"));
        LinearLayout.LayoutParams totalParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        totalLayout.setLayoutParams(totalParams);

        View divider1 = new View(this);
        divider1.setBackgroundColor(Color.parseColor("#E5E7EB"));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(1, dpToPx(40));
        divider1.setLayoutParams(dividerParams);

        LinearLayout grantedLayout = createStatItem(String.valueOf(granted), getString(R.string.perm_activity_stat_granted), granted > 0 ? Color.parseColor("#EF4444") : Color.parseColor("#10B981"));
        LinearLayout.LayoutParams grantedParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        grantedLayout.setLayoutParams(grantedParams);

        View divider2 = new View(this);
        divider2.setBackgroundColor(Color.parseColor("#E5E7EB"));
        divider2.setLayoutParams(dividerParams);

        LinearLayout deniedLayout = createStatItem(String.valueOf(total - granted), getString(R.string.perm_activity_stat_denied), Color.parseColor("#F59E0B"));
        LinearLayout.LayoutParams deniedParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        deniedLayout.setLayoutParams(deniedParams);

        statsLayout.addView(totalLayout);
        statsLayout.addView(divider1);
        statsLayout.addView(grantedLayout);
        statsLayout.addView(divider2);
        statsLayout.addView(deniedLayout);

        container.addView(statsLayout);
    }

    private LinearLayout createStatItem(String value, String label, int color) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextSize(32);
        valueView.setTypeface(null, android.graphics.Typeface.BOLD);
        valueView.setTextColor(color);
        valueView.setGravity(Gravity.CENTER);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextSize(12);
        labelView.setTextColor(Color.parseColor("#757575"));
        labelView.setGravity(Gravity.CENTER);
        labelView.setPadding(0, dpToPx(4), 0, 0);

        layout.addView(valueView);
        layout.addView(labelView);

        return layout;
    }

    private void addWarning() {
        LinearLayout warningLayout = new LinearLayout(this);
        warningLayout.setOrientation(LinearLayout.VERTICAL);
        warningLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), 0);

        GradientDrawable warningBg = new GradientDrawable();
        warningBg.setColor(Color.parseColor("#FEF3C7"));
        warningBg.setCornerRadius(dpToPx(12));
        warningBg.setStroke(2, Color.parseColor("#F59E0B"));

        LinearLayout warningCard = new LinearLayout(this);
        warningCard.setOrientation(LinearLayout.VERTICAL);
        warningCard.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));
        warningCard.setBackground(warningBg);

        TextView warningTitle = new TextView(this);
        warningTitle.setText(R.string.perm_activity_warning_title);
        warningTitle.setTextSize(14);
        warningTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        warningTitle.setTextColor(Color.parseColor("#92400E"));

        TextView warningText = new TextView(this);
        warningText.setText(R.string.tip_permission_warning);
        warningText.setTextSize(13);
        warningText.setTextColor(Color.parseColor("#78350F"));
        warningText.setLineSpacing(dpToPx(4), 1.3f);
        warningText.setPadding(0, dpToPx(8), 0, 0);

        warningCard.addView(warningTitle);
        warningCard.addView(warningText);
        warningLayout.addView(warningCard);

        container.addView(warningLayout);
    }

    private void addPermissionList(List<PermissionCollector.PermissionItem> permissions) {
        Map<String, String> groups = new HashMap<>();
        groups.put(getString(R.string.perm_group_location), "location");
        groups.put(getString(R.string.perm_group_personal), "personal");
        groups.put(getString(R.string.perm_group_device), "device");

        addGroup(getString(R.string.perm_group_location), getPermissionByGroup(permissions, new String[]{
            "ACCESS_FINE_LOCATION", "ACCESS_COARSE_LOCATION"
        }));

        addGroup(getString(R.string.perm_group_personal), getPermissionByGroup(permissions, new String[]{
            "READ_CONTACTS", "READ_SMS", "READ_CALENDAR", "READ_CALL_LOG"
        }));

        addGroup(getString(R.string.perm_group_device), getPermissionByGroup(permissions, new String[]{
            "CAMERA", "RECORD_AUDIO", "READ_PHONE_STATE", "READ_EXTERNAL_STORAGE"
        }));
    }

    private List<PermissionCollector.PermissionItem> getPermissionByGroup(
            List<PermissionCollector.PermissionItem> all, String[] perms) {
        java.util.ArrayList<PermissionCollector.PermissionItem> result = new java.util.ArrayList<>();
        for (PermissionCollector.PermissionItem item : all) {
            for (String perm : perms) {
                if (item.permission.equals(perm)) {
                    result.add(item);
                    break;
                }
            }
        }
        return result;
    }

    private void addGroup(String groupName, List<PermissionCollector.PermissionItem> items) {
        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        titleLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(8));

        TextView title = new TextView(this);
        title.setText(groupName);
        title.setTextSize(16);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(Color.parseColor("#374151"));

        titleLayout.addView(title);
        container.addView(titleLayout);

        for (PermissionCollector.PermissionItem item : items) {
            addPermissionCard(item);
        }
    }

    private void addPermissionCard(PermissionCollector.PermissionItem item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(dpToPx(12));
        cardBg.setStroke(1, Color.parseColor("#E5E7EB"));
        card.setBackground(cardBg);
        card.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(dpToPx(16), 0, dpToPx(16), dpToPx(8));
        card.setLayoutParams(cardParams);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView nameView = new TextView(this);
        nameView.setText(item.displayName);
        nameView.setTextSize(15);
        nameView.setTextColor(Color.parseColor("#1F2937"));
        nameView.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        nameView.setLayoutParams(nameParams);

        TextView statusTag = new TextView(this);
        boolean isGranted = getString(R.string.perm_granted).equals(item.status);
        statusTag.setText(isGranted ? getString(R.string.perm_granted) : getString(R.string.perm_denied));
        statusTag.setTextSize(11);
        statusTag.setPadding(dpToPx(8), dpToPx(3), dpToPx(8), dpToPx(3));
        statusTag.setGravity(Gravity.CENTER);

        GradientDrawable tagBg = new GradientDrawable();
        tagBg.setCornerRadius(dpToPx(12));

        if (isGranted) {
            tagBg.setColor(Color.parseColor("#FEE2E2"));
            statusTag.setTextColor(Color.parseColor("#DC2626"));
        } else {
            tagBg.setColor(Color.parseColor("#D1FAE5"));
            statusTag.setTextColor(Color.parseColor("#059669"));
        }
        statusTag.setBackground(tagBg);

        header.addView(nameView);
        header.addView(statusTag);
        card.addView(header);

        TextView permView = new TextView(this);
        permView.setText(item.permission);
        permView.setTextSize(11);
        permView.setTextColor(Color.parseColor("#9CA3AF"));
        permView.setPadding(0, dpToPx(2), 0, dpToPx(8));
        card.addView(permView);

        TextView descView = new TextView(this);
        descView.setText(item.description);
        descView.setTextSize(13);
        descView.setTextColor(Color.parseColor("#6B7280"));
        card.addView(descView);

        if (item.risk != null && !item.risk.isEmpty()) {
            LinearLayout riskLayout = new LinearLayout(this);
            riskLayout.setOrientation(LinearLayout.HORIZONTAL);
            riskLayout.setPadding(0, dpToPx(8), 0, 0);

            GradientDrawable riskBg = new GradientDrawable();
            riskBg.setColor(Color.parseColor("#FEE2E2"));
            riskBg.setCornerRadius(dpToPx(4));

            TextView riskIcon = new TextView(this);
            riskIcon.setText("⚠️");
            riskIcon.setTextSize(12);

            TextView riskView = new TextView(this);
            riskView.setText(item.risk);
            riskView.setTextSize(12);
            riskView.setTextColor(Color.parseColor("#B91C1C"));
            riskView.setPadding(dpToPx(4), 0, 0, 0);

            riskLayout.addView(riskIcon);
            riskLayout.addView(riskView);
            card.addView(riskLayout);
        }

        container.addView(card);
    }

    private void addBackButton() {
        Button backButton = new Button(this);
        backButton.setText(R.string.perm_activity_back);
        backButton.setTextColor(Color.WHITE);
        backButton.setTextSize(16);

        GradientDrawable buttonBg = new GradientDrawable();
        buttonBg.setColor(Color.parseColor("#6366F1"));
        buttonBg.setCornerRadius(dpToPx(12));
        backButton.setBackground(buttonBg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(48)
        );
        params.setMargins(dpToPx(16), dpToPx(24), dpToPx(16), dpToPx(24));
        backButton.setLayoutParams(params);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        container.addView(backButton);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
