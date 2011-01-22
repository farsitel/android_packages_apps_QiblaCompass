package com.farsitel.android.qiblacompass.activities;

/*
 * This Activity is responsible to show about screen to user
 * 
 * This activity was originally written by Bahareh, and changed by Majid Kalkatehchi 
 * 
 * Required files :
 * res/values/strings.xml
 * res/values/dimens.xml
 *
 */

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.farsitel.android.qiblacompass.R;

public class AboutActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout titleLinearLayout = new LinearLayout(this);
        titleLinearLayout.setGravity(Gravity.CENTER);
        TextView titleTextview = new TextView(this);
        titleTextview.setText(getResources().getString(
                R.string.about_title_text));
        titleTextview.setTextSize(getResources().getDimension(
                R.dimen.about_text_size));
        titleTextview.setTypeface(null, Typeface.BOLD);
        titleLinearLayout.addView(titleTextview);

        LinearLayout versionLinearLayout = new LinearLayout(this);
        versionLinearLayout.setGravity(Gravity.CENTER);
        TextView version = new TextView(this);
        version.setTextSize(getResources()
                .getDimension(R.dimen.about_text_size));
        version.setText(R.string.version);
        versionLinearLayout.addView(version);

        LinearLayout releaseDateLinearLayout = new LinearLayout(this);
        releaseDateLinearLayout.setGravity(Gravity.CENTER);
        TextView releaseDateTextView = new TextView(this);
        releaseDateTextView.setText(R.string.release_date);
        releaseDateTextView.setTextSize(getResources().getDimension(
                R.dimen.about_text_size));
        releaseDateLinearLayout.addView(releaseDateTextView);

        TextView aboutTextView = new TextView(this);
        aboutTextView.setLineSpacing(1, (float) 1.3);
        aboutTextView.setGravity(Gravity.LEFT);
        aboutTextView.setText(R.string.about_text);
        aboutTextView.setPadding(
                getResources().getDimensionPixelOffset(
                        R.dimen.about_text_padding),
                getResources().getDimensionPixelOffset(
                        R.dimen.about_text_padding),
                getResources().getDimensionPixelOffset(
                        R.dimen.about_text_padding), getResources()
                        .getDimensionPixelOffset(R.dimen.about_text_padding));
        aboutTextView.setTextSize(getResources().getDimension(
                R.dimen.about_text_size));

        ll.addView(titleLinearLayout);
        ll.addView(aboutTextView);
        ll.addView(versionLinearLayout);
        ll.addView(releaseDateLinearLayout);

        sv.addView(ll);
        setContentView(sv);
    }

}
