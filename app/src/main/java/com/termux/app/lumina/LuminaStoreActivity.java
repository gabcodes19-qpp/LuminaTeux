package com.termux.app.lumina;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;
import com.termux.R;

/**
 * One-tap module, AI CLI and proot-Linux store.
 *
 * Launched from the drawer of TermuxActivity. Each catalog item installs
 * through {@link LuminaModuleInstaller}, which runs commands in the Termux
 * environment and streams the output into a log dialog.
 */
public class LuminaStoreActivity extends AppCompatActivity {

    private TextView mLogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LuminaPrefs.dynamicColors(this)) {
            DynamicColors.applyToActivitiesIfAvailable(getApplication());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lumina_store);

        ListView list = findViewById(R.id.lumina_store_list);
        LuminaStoreAdapter adapter = new LuminaStoreAdapter(this, LuminaCatalog.getItems(), this::confirmInstall);
        list.setAdapter(adapter);

        ImageButton settings = findViewById(R.id.lumina_store_settings);
        settings.setOnClickListener(v ->
                startActivity(new Intent(this, LuminaSettingsActivity.class)));
    }

    private void confirmInstall(LuminaCatalog.Item item) {
        if (LuminaModuleInstaller.isBusy()) {
            Toast.makeText(this, R.string.lumina_busy, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!item.available) {
            return;
        }
        StringBuilder commands = new StringBuilder();
        for (String command : item.commands) {
            commands.append(command).append('\n');
        }
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.lumina_install_confirm_title, item.name))
                .setMessage(getString(R.string.lumina_install_confirm_message, commands.toString()))
                .setPositiveButton(R.string.lumina_install_ok, (dialog, which) -> runInstall(item))
                .setNegativeButton(R.string.lumina_install_cancel, null)
                .show();
    }

    private void runInstall(LuminaCatalog.Item item) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_lumina_install, null);
        ProgressBar progress = view.findViewById(R.id.lumina_install_progress);
        mLogView = view.findViewById(R.id.lumina_install_log);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.lumina_action_installing, item.name))
                .setView(view)
                .setPositiveButton(R.string.lumina_close, null)
                .show();

        LuminaModuleInstaller.install(item, new LuminaModuleInstaller.Listener() {
            @Override
            public void onLine(String line) {
                runOnUiThread(() -> {
                    if (mLogView != null) {
                        mLogView.append(line + "\n");
                    }
                });
            }

            @Override
            public void onComplete(boolean success, String summary) {
                runOnUiThread(() -> {
                    if (progress != null) {
                        progress.setVisibility(View.GONE);
                    }
                    if (mLogView != null) {
                        mLogView.append("\n" + getString(
                                success ? R.string.lumina_install_done : R.string.lumina_install_failed) + "\n");
                    }
                });
            }
        });
    }
}
