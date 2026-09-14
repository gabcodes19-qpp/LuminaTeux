package com.termux.app.lumina;

import com.termux.shared.logger.Logger;
import com.termux.shared.termux.TermuxConstants;

import java.io.File;
import java.io.FileWriter;

/**
 * LuminaTeux styling & branding installer.
 *
 * Writes into the Termux environment on first run:
 *  - {@code $PREFIX/etc/motd}          : ASCII welcome banner
 *  - {@code ~/.termux/colors.properties} : indigo/cyan terminal color scheme
 *  - {@code ~/.termux/termux.properties} : extra-key bar + cursor style
 */
public final class LuminaBanner {

    private static final String LOG_TAG = "LuminaBanner";

    public static final String BANNER =
        " _              _          _____\n" +
        "| |  _  _ _ __ (_)_ _  __ |_   _|__ _  ___ __\n" +
        "| |_| || | '  \\| | ' \\/ _` || |/ -_) || \\ \\ /\n" +
        "|____\\_,_|_|_|_|_|_||_\\__,_||_|\\___|\\_,_/_\\_\\\n" +
        "\n" +
        "   LuminaTeux - your terminal, elevated\n" +
        "   one-tap modules | AI agents | proot Linux\n" +
        "   Type 'help' to get started\n";

    /** LuminaTeux dark terminal scheme (indigo/cyan on deep navy). */
    private static final String COLORS_PROPERTIES =
        "background=#0B1020\n" +
        "foreground=#DCE7F5\n" +
        "cursor=#22D3EE\n" +
        "color0=#0B1020\n" +
        "color1=#F87171\n" +
        "color2=#4ADE80\n" +
        "color3=#FACC15\n" +
        "color4=#60A5FA\n" +
        "color5=#C084FC\n" +
        "color6=#22D3EE\n" +
        "color7=#E2E8F0\n" +
        "color8=#64748B\n" +
        "color9=#FCA5A5\n" +
        "color10=#86EFAC\n" +
        "color11=#FDE047\n" +
        "color12=#93C5FD\n" +
        "color13=#D8B4FE\n" +
        "color14=#67E8F9\n" +
        "color15=#FFFFFF\n";

    /** Default terminal behaviour: friendly extra-key bar, bar cursor, URL tapping. */
    private static final String TERMUX_PROPERTIES =
        "# LuminaTeux defaults\n" +
        "extra-keys = [['ESC','/','-','HOME','UP','END','PGUP'],['TAB','CTRL','ALT','LEFT','DOWN','RIGHT','PGDN']]\n" +
        "terminal-cursor-style = bar\n" +
        "terminal-onclick-url-open = true\n" +
        "bell-character = ignore\n";

    private LuminaBanner() {}

    /** Install motd + terminal theme. Non-destructive unless {@code force} is set. */
    public static void installAll(boolean force) {
        installMotd();
        installTerminalTheme(force);
    }

    /** Write the ASCII banner into the Termux prefix motd (no-op until bootstrap exists). */
    public static void installMotd() {
        try {
            File prefix = new File(TermuxConstants.TERMUX_PREFIX_DIR_PATH);
            if (!prefix.isDirectory()) {
                return; // bootstrap not installed yet
            }
            File etc = new File(prefix, "etc");
            if (!etc.isDirectory() && !etc.mkdirs()) {
                return;
            }
            write(new File(etc, "motd"), BANNER);
            Logger.logInfo(LOG_TAG, "Installed LuminaTeux motd banner");
        } catch (Exception e) {
            Logger.logWarn(LOG_TAG, "Failed to install motd banner: " + e.getMessage());
        }
    }

    /** Write colors.properties and termux.properties if missing (or when forced). */
    public static void installTerminalTheme(boolean force) {
        try {
            File prefix = new File(TermuxConstants.TERMUX_PREFIX_DIR_PATH);
            if (!prefix.isDirectory()) {
                return;
            }

            File colorsFile = TermuxConstants.TERMUX_COLOR_PROPERTIES_FILE;
            if (!colorsFile.exists() || force) {
                write(colorsFile, COLORS_PROPERTIES);
                Logger.logInfo(LOG_TAG, "Installed LuminaTeux colors.properties");
            }

            File propsFile = TermuxConstants.TERMUX_PROPERTIES_PRIMARY_FILE;
            if (!propsFile.exists() || force) {
                write(propsFile, TERMUX_PROPERTIES);
                Logger.logInfo(LOG_TAG, "Installed LuminaTeux termux.properties");
            }
        } catch (Exception e) {
            Logger.logWarn(LOG_TAG, "Failed to install terminal theme: " + e.getMessage());
        }
    }

    /** Revert to the stock black terminal (delete colors.properties). */
    public static void removeTerminalTheme() {
        try {
            File colorsFile = TermuxConstants.TERMUX_COLOR_PROPERTIES_FILE;
            if (colorsFile.exists() && !colorsFile.delete()) {
                Logger.logWarn(LOG_TAG, "Failed to delete colors.properties");
            }
        } catch (Exception e) {
            Logger.logWarn(LOG_TAG, "Failed to remove terminal theme: " + e.getMessage());
        }
    }

    private static void write(File file, String content) throws Exception {
        File parent = file.getParentFile();
        if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
            throw new Exception("Cannot create " + parent.getAbsolutePath());
        }
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }
}
