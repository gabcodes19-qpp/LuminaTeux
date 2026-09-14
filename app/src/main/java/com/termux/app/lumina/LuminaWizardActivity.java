package com.termux.app.lumina;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;
import com.termux.R;
import com.termux.app.TermuxActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * First-run setup wizard: "What are you here for?" Installs the matching
 * package bundle in one tap, then continues to the terminal.
 */
public class LuminaWizardActivity extends AppCompatActivity {

    private TextView mLogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LuminaPrefs.dynamicColors(this)) {
            DynamicColors.applyToActivitiesIfAvailable(getApplication());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lumina_wizard);

        LinearLayout container = findViewById(R.id.lumina_wizard_categories);

        findViewById(R.id.lumina_wizard_install).setOnClickListener(v -> {
            List<String> commands = new ArrayList<>();
            for (int i = 0; i < container.getChildCount(); i++) {
                View child = container.getChildAt(i);
                if (child instanceof CheckBox && ((CheckBox) child).isChecked()) {
                    String[] cmd = LuminaCatalog.getBundleCommands((String) child.getTag());
                    if (cmd != null) {
                        for (String c : cmd) commands.add(c);
                    }
                }
            }
            if (commands.isEmpty()) {
                finishToTerminal();
                return;
            }
            runInstall(commands.toArray(new String[0]));
        });

        findViewById(R.id.lumina_wizard_skip).setOnClickListener(v -> finishToTerminal());
    }

    private void runInstall(String[] commands) {
        if (LuminaModuleInstaller.isBusy()) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_lumina_install, null);
        ProgressBar progress = view.findViewById(R.id.lumina_install_progress);
        mLogView = view.findViewById(R.id.lumina_install_log);
        new AlertDialog.Builder(this)
                .setTitle(R.string.lumina_wizard_installing)
                .setView(view)
                .setPositiveButton(R.string.lumina_close, null)
                .show();
        LuminaModuleInstaller.runCommands(commands, new LuminaModuleInstaller.Listener() {
            @Override
            public void onLine(String line) {
                runOnUiThread(() -> {
                    if (mLogView != null) mLogView.append(line + "\n");
                });
            }

            @Override
            public void onComplete(boolean success, String summary) {
                runOnUiThread(() -> {
                    if (progress != null) progress.setVisibility(View.GONE);
                    if (mLogView != null) mLogView.append("\n" + getString(
                            success ? R.string.lumina_install_done : R.string.lumina_install_failed) + "\n");
                });
            }
        });
    }

    private void finishToTerminal() {
        startActivity(new Intent(this, TermuxActivity.class));
        finish();
    }
}
