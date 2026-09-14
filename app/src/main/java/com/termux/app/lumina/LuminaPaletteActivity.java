package com.termux.app.lumina;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;
import com.termux.R;
import com.termux.app.TermuxActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * VS Code–style command palette. Type to filter quick actions and shell
 * commands; run commands with live output, or jump to any LuminaTeux screen.
 */
public class LuminaPaletteActivity extends AppCompatActivity {

    private static class Action {
        final String title;
        final String subtitle;
        final String[] commands; // null = navigation
        final Intent intent;     // null = run command

        Action(String title, String subtitle, Intent intent) {
            this(title, subtitle, null, intent);
        }

        Action(String title, String subtitle, String[] commands) {
            this(title, subtitle, commands, null);
        }

        private Action(String title, String subtitle, String[] commands, Intent intent) {
            this.title = title;
            this.subtitle = subtitle;
            this.commands = commands;
            this.intent = intent;
        }
    }

    private final List<Action> allActions = new ArrayList<>();
    private final List<Action> filtered = new ArrayList<>();
    private ActionAdapter adapter;
    private TextView mLogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LuminaPrefs.dynamicColors(this)) {
            DynamicColors.applyToActivitiesIfAvailable(getApplication());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lumina_palette);

        allActions.add(new Action(getString(R.string.lumina_palette_new_session),
                getString(R.string.lumina_palette_new_session_sub), new Intent(this, TermuxActivity.class)));
        allActions.add(new Action(getString(R.string.lumina_palette_store),
                getString(R.string.lumina_palette_store_sub), new Intent(this, LuminaStoreActivity.class)));
        allActions.add(new Action(getString(R.string.lumina_palette_settings),
                getString(R.string.lumina_palette_settings_sub), new Intent(this, LuminaSettingsActivity.class)));
        allActions.add(new Action(getString(R.string.lumina_palette_backup),
                getString(R.string.lumina_palette_backup_sub), new Intent(this, LuminaBackupActivity.class)));
        allActions.add(new Action(getString(R.string.lumina_palette_help),
                getString(R.string.lumina_palette_help_sub), new Intent(this, LuminaHelpActivity.class)));
        allActions.add(new Action(getString(R.string.lumina_palette_update), "pkg update -y",
                new String[]{"pkg update -y"}));
        allActions.add(new Action(getString(R.string.lumina_palette_upgrade), "pkg upgrade -y",
                new String[]{"pkg upgrade -y"}));
        allActions.add(new Action(getString(R.string.lumina_palette_neofetch), "neofetch",
                new String[]{"neofetch"}));
        allActions.add(new Action(getString(R.string.lumina_palette_ubuntu), "proot-distro login ubuntu",
                new String[]{"proot-distro login ubuntu"}));

        filtered.addAll(allActions);

        ListView list = findViewById(R.id.lumina_palette_list);
        adapter = new ActionAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, position, id) -> dispatch(filtered.get(position)));

        EditText search = findViewById(R.id.lumina_palette_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String query) {
        filtered.clear();
        String q = query.trim().toLowerCase();
        for (Action a : allActions) {
            if (q.isEmpty() || a.title.toLowerCase().contains(q) || a.subtitle.toLowerCase().contains(q)) {
                filtered.add(a);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void dispatch(Action action) {
        if (action.intent != null) {
            startActivity(action.intent);
            return;
        }
        if (action.commands == null || LuminaModuleInstaller.isBusy()) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_lumina_install, null);
        ProgressBar progress = view.findViewById(R.id.lumina_install_progress);
        mLogView = view.findViewById(R.id.lumina_install_log);
        new AlertDialog.Builder(this)
                .setTitle(action.title)
                .setView(view)
                .setPositiveButton(R.string.lumina_close, null)
                .show();
        LuminaModuleInstaller.runCommands(action.commands, new LuminaModuleInstaller.Listener() {
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

    private class ActionAdapter extends BaseAdapter {
        @Override public int getCount() { return filtered.size(); }
        @Override public Object getItem(int position) { return filtered.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView != null ? convertView
                    : LayoutInflater.from(LuminaPaletteActivity.this).inflate(R.layout.item_lumina_palette, parent, false);
            Action a = filtered.get(position);
            ((TextView) row.findViewById(R.id.lumina_palette_title)).setText(a.title);
            ((TextView) row.findViewById(R.id.lumina_palette_subtitle)).setText(a.subtitle);
            return row;
        }
    }
}
