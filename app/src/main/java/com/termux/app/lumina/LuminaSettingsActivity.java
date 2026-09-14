package com.termux.app.lumina;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.color.DynamicColors;
import com.termux.R;
import com.termux.shared.termux.TermuxConstants;

/**
 * LuminaTeux settings: Material You dynamic colors, terminal theme, and
 * one-tap re-application of the Lumina terminal styling.
 */
public class LuminaSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LuminaPrefs.dynamicColors(this)) {
            DynamicColors.applyToActivitiesIfAvailable(getApplication());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lumina_settings);

        SwitchCompat dynamicSwitch = findViewById(R.id.lumina_switch_dynamic);
        dynamicSwitch.setChecked(LuminaPrefs.dynamicColors(this));
        dynamicSwitch.setOnCheckedChangeListener((button, checked) -> {
            LuminaPrefs.setDynamicColors(this, checked);
            Toast.makeText(this, R.string.lumina_settings_dynamic_applied, Toast.LENGTH_SHORT).show();
        });

        SwitchCompat themeSwitch = findViewById(R.id.lumina_switch_theme);
        themeSwitch.setChecked(LuminaPrefs.terminalTheme(this));
        themeSwitch.setOnCheckedChangeListener((button, checked) -> {
            LuminaPrefs.setTerminalTheme(this, checked);
            if (checked) {
                LuminaBanner.installTerminalTheme(true);
            } else {
                LuminaBanner.removeTerminalTheme();
            }
            sendReloadStyle();
            Toast.makeText(this, checked
                    ? R.string.lumina_settings_theme_on
                    : R.string.lumina_settings_theme_off, Toast.LENGTH_SHORT).show();
        });

        Button applyButton = findViewById(R.id.lumina_btn_apply);
        applyButton.setOnClickListener(v -> {
            LuminaBanner.installTerminalTheme(true);
            sendReloadStyle();
            Toast.makeText(this, R.string.lumina_settings_applied, Toast.LENGTH_SHORT).show();
        });
    }

    /** Ask the running terminal to re-read colors.properties / termux.properties. */
    private void sendReloadStyle() {
        try {
            Intent reload = new Intent(TermuxConstants.TERMUX_APP.TERMUX_ACTIVITY.ACTION_RELOAD_STYLE);
            reload.setPackage(getPackageName());
            sendBroadcast(reload);
        } catch (Exception e) {
            // Non-fatal: theme applies fully on next app launch.
        }
    }
}
