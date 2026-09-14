package com.termux.app.lumina;

import android.content.Context;
import android.content.SharedPreferences;

/** LuminaTeux user preferences (stored in app-private SharedPreferences). */
public final class LuminaPrefs {

    private static final String PREFS = "lumina_prefs";
    private static final String KEY_DYNAMIC_COLORS = "dynamic_colors";
    private static final String KEY_TERMINAL_THEME = "terminal_theme";

    private LuminaPrefs() {}

    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** Material You dynamic colors (default off, dark-first). */
    public static boolean dynamicColors(Context context) {
        return get(context).getBoolean(KEY_DYNAMIC_COLORS, false);
    }

    public static void setDynamicColors(Context context, boolean on) {
        get(context).edit().putBoolean(KEY_DYNAMIC_COLORS, on).apply();
    }

    /** LuminaTeux terminal color scheme (default on). */
    public static boolean terminalTheme(Context context) {
        return get(context).getBoolean(KEY_TERMINAL_THEME, true);
    }

    public static void setTerminalTheme(Context context, boolean on) {
        get(context).edit().putBoolean(KEY_TERMINAL_THEME, on).apply();
    }
}
