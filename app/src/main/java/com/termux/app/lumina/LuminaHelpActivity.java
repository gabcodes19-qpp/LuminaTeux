package com.termux.app.lumina;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;
import com.termux.R;

/**
 * In-app help: lists the LuminaTeux features, the 'lumina' shell command and
 * the most useful terminal commands.
 */
public class LuminaHelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LuminaPrefs.dynamicColors(this)) {
            DynamicColors.applyToActivitiesIfAvailable(getApplication());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lumina_help);

        TextView body = findViewById(R.id.lumina_help_body);
        body.setText(getString(R.string.lumina_help_body));
    }
}
