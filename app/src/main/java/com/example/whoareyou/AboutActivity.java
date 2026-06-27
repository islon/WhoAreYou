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

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        LinearLayout container = findViewById(R.id.aboutContainer);
        
        // 添加标题区域
        addHeaderSection(container);
        
        // 添加应用介绍
        addAppIntro(container);
        
        // 添加功能特点
        addFeatures(container);
        
        // 添加隐私说明
        addPrivacyInfo(container);
        
        // 添加技术说明
        addTechInfo(container);
        
        // 添加返回按钮
        addBackButton(container);
    }

    private void addHeaderSection(LinearLayout container) {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundResource(R.drawable.header_gradient);
        header.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(32));
        
        TextView title = new TextView(this);
        title.setText("你是谁");
        title.setTextSize(32);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("隐私信息透明化展示");
        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.parseColor("#E0E0E0"));
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dpToPx(8), 0, 0);
        
        TextView version = new TextView(this);
        version.setText("版本 v1.1");
        version.setTextSize(12);
        version.setTextColor(Color.parseColor("#BDBDBD"));
        version.setGravity(Gravity.CENTER);
        version.setPadding(0, dpToPx(4), 0, 0);
        
        header.addView(title);
        header.addView(subtitle);
        header.addView(version);
        container.addView(header);
    }

    private void addAppIntro(LinearLayout container) {
        addSection(container, "📋 应用介绍", 
            "本应用旨在帮助用户了解：在不授予任何权限的情况下，手机应用能够获取哪些设备信息。\n\n" +
            "通过透明化展示这些信息，我们希望提高用户的隐私保护意识，让用户清楚了解自己的数据是如何被收集和使用的。");
    }

    private void addFeatures(LinearLayout container) {
        addSection(container, "✨ 功能特点",
            "• **信息透明**: 展示55+项设备信息，包括硬件、系统、网络、传感器、安全状态等\n\n" +
            "• **权限展示**: 展示24项权限信息，详细说明授权后的用途和风险\n\n" +
            "• **风险标识**: 三级风险等级（低/中/高），帮助快速识别敏感信息\n\n" +
            "• **用途说明**: 详细说明每项信息的潜在用途和风险\n\n" +
            "• **本地处理**: 所有数据仅在本地处理，不会上传任何信息\n\n" +
            "• **零权限**: 不请求任何敏感权限，保护用户隐私");
    }

    private void addPrivacyInfo(LinearLayout container) {
        addSection(container, "🔒 隐私保护",
            "• **零数据上传**: 本应用不会向任何服务器发送数据\n\n" +
            "• **本地分析**: 所有信息收集和展示都在设备本地完成\n\n" +
            "• **无权限请求**: 仅使用系统允许的普通权限（网络状态、WiFi状态）\n\n" +
            "• **开源透明**: 应用代码完全开源，可自行验证安全性");
    }

    private void addTechInfo(LinearLayout container) {
        addSection(container, "🛠️ 技术说明",
            "**为什么应用能获取这些信息？**\n\n" +
            "Android系统为应用提供了多种无需特殊权限即可访问的系统API。这些API原本是为了帮助应用更好地适配设备和提供个性化服务而设计的。\n\n" +
            "**信息用途示例：**\n\n" +
            "• 广告商：使用设备型号+Android ID进行精准广告投放\n\n" +
            "• 流氓应用：通过WiFi信息推断用户位置和行为模式，窃取隐私数据\n\n" +
            "• 安全研究：检测设备安全状态和系统漏洞\n\n" +
            "**保护建议：**\n\n" +
            "• 定期检查手机各应用的权限\n\n" +
            "• 谨慎授予敏感权限\n\n" +
            "• 使用隐私保护工具");
    }

    private void addSection(LinearLayout container, String title, String content) {
        LinearLayout section = new LinearLayout(this);
        section.setOrientation(LinearLayout.VERTICAL);
        section.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), 0);
        
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(16);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setTextColor(Color.parseColor("#374151"));
        titleView.setPadding(0, dpToPx(16), 0, dpToPx(8));
        
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(dpToPx(12));
        cardBg.setStroke(1, Color.parseColor("#E5E7EB"));
        
        TextView contentView = new TextView(this);
        contentView.setText(content);
        contentView.setTextSize(14);
        contentView.setTextColor(Color.parseColor("#4B5563"));
        contentView.setLineSpacing(dpToPx(4), 1.5f);
        contentView.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        contentView.setBackground(cardBg);
        
        section.addView(titleView);
        section.addView(contentView);
        container.addView(section);
    }

    private void addBackButton(LinearLayout container) {
        Button backButton = new Button(this);
        backButton.setText("返回");
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