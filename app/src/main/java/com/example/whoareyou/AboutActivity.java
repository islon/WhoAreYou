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

        addHeaderSection(container);
        addAppIntro(container);
        addFeatures(container);
        addPrivacyInfo(container);
        addTechInfo(container);
        addProtectionTips(container);
        addBackButton(container);
    }

    private void addHeaderSection(LinearLayout container) {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundResource(R.drawable.header_gradient);
        header.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(32));

        TextView title = new TextView(this);
        title.setText(R.string.main_title);
        title.setTextSize(32);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText(R.string.main_subtitle);
        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.parseColor("#E0E0E0"));
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dpToPx(8), 0, 0);

        TextView version = new TextView(this);
        version.setText(R.string.app_version);
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
        addSection(container, getString(R.string.about_intro),
            getString(R.string.about_intro_content));
    }

    private void addFeatures(LinearLayout container) {
        addSection(container, getString(R.string.about_features),
            getString(R.string.about_features_content));
    }

    private void addPrivacyInfo(LinearLayout container) {
        addSection(container, getString(R.string.about_privacy),
            getString(R.string.about_privacy_content));
    }

    private void addTechInfo(LinearLayout container) {
        addSection(container, getString(R.string.about_tech),
            getString(R.string.about_tech_content));
    }

    private void addProtectionTips(LinearLayout container) {
        addSection(container, getString(R.string.about_suggestions),
            getString(R.string.about_suggestions_content));
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
        backButton.setText(R.string.btn_back);
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
