package com.termux.app.lumina;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;
import com.termux.R;

/**
 * Backup and restore of the LuminaTeux configuration (dotfiles, Termux config
 * and the installed-package list) to a tarball in the home directory.
 */
public class LuminaBackupActivity extends AppCompatActivity {

    private static final String[] BACKUP_COMMANDS = {
        "cd ~ && tar -czf lumateux-backup-latest.tar.gz .termux .config .bashrc .profile .zshrc 2>/dev/null",
        "pkg list-installed > lumateux-packages.txt 2>/dev/null",
        "echo 'Backup saved to ~/lumateux-backup-latest.tar.gz' && ls -lh ~/lumateux-backup-latest.tar.gz"
    };

    private static final String[] RESTORE_COMMANDS = {
        "cd ~ && if [ -f lumateux-backup-latest.tar.gz ]; then tar -xzf lumateux-backup-latest.tar.gz -C ~ && echo 'Restore complete'; else echo 'No backup found in home directory'; fi"
    };

    private TextView mLogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LuminaPrefs.dynamicColors(this)) {
            DynamicColors.applyToActivitiesIfAvailable(getApplication());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lumina_backup);

        findViewById(R.id.lumina_backup_now).setOnClickListener(v -> run(R.string.lumina_backup_now, BACKUP_COMMANDS));
        findViewById(R.id.lumina_backup_restore).setOnClickListener(v -> run(R.string.lumina_backup_restore, RESTORE_COMMANDS));
    }

    private void run(int titleRes, String[] commands) {
        if (LuminaModuleInstaller.isBusy()) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_lumina_install, null);
        ProgressBar progress = view.findViewById(R.id.lumina_install_progress);
        mLogView = view.findViewById(R.id.lumina_install_log);
        new AlertDialog.Builder(this)
                .setTitle(titleRes)
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
}
