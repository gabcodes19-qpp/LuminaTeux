package com.termux.app.lumina;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.termux.R;
import com.termux.app.TermuxActivity;
/**
 * First-run intro / splash. The launcher entry point of LuminaTeux.
 * Shows branding + ASCII banner once, then forwards to the terminal.
 */
public class LuminaIntroActivity extends AppCompatActivity {

    private static final String PREFS = "lumina_prefs";
    private static final String KEY_INTRO_SHOWN = "intro_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_INTRO_SHOWN, false)) {
            startActivity(new Intent(this, TermuxActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_lumina_intro);

        TextView banner = findViewById(R.id.lumina_intro_banner);
        banner.setText(LuminaBanner.BANNER);

        findViewById(R.id.lumina_intro_start).setOnClickListener(v -> {
            prefs.edit().putBoolean(KEY_INTRO_SHOWN, true).apply();
            startActivity(new Intent(this, LuminaWizardActivity.class));
            finish();
        });
    }
}
