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
        "   Type 'lumina help' for tips & features\n";

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

    /** Install motd + terminal theme + help command. Non-destructive unless {@code force} is set. */
    public static void installAll(boolean force) {
        installMotd();
        installTerminalTheme(force);
        installHelpCommand();
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

    /** Write the 'lumina' shell command documenting the app's features. */
    public static void installHelpCommand() {
        try {
            File prefix = new File(TermuxConstants.TERMUX_PREFIX_DIR_PATH);
            if (!prefix.isDirectory()) {
                return;
            }
            File bin = new File(TermuxConstants.TERMUX_BIN_PREFIX_DIR_PATH);
            if (!bin.isDirectory() && !bin.mkdirs()) {
                return;
            }
            String shPath = TermuxConstants.TERMUX_BIN_PREFIX_DIR_PATH + "/sh";
            StringBuilder script = new StringBuilder();
            script.append("#!").append(shPath).append("\n");
            script.append("# LuminaTeux helper command\n");
            script.append("case \"${1:-help}\" in\n");
            script.append("  backup)\n");
            script.append("    cd \"$HOME\" && tar -czf lumateux-backup-latest.tar.gz .termux .config .bashrc .profile .zshrc 2>/dev/null\n");
            script.append("    echo \"Backup saved to ~/lumateux-backup-latest.tar.gz\"\n");
            script.append("    ;;\n");
            script.append("  help|*)\n");
            script.append("    cat << 'EOF'\n");
            script.append(HELP_TEXT);
            script.append("EOF\n");
            script.append("    ;;\n");
            script.append("esac\n");

            File file = new File(bin, "lumina");
            write(file, script.toString());
            file.setExecutable(true, false);
            Logger.logInfo(LOG_TAG, "Installed 'lumina' help command at " + file.getAbsolutePath());
        } catch (Exception e) {
            Logger.logWarn(LOG_TAG, "Failed to install 'lumina' command: " + e.getMessage());
        }
    }

    private static final String HELP_TEXT =
        "LUMINATEUX HELP\n" +
        "===============\n" +
        "\n" +
        "IN-APP (swipe from the left edge to open the drawer):\n" +
        "  [Search] Command palette - type any action or command\n" +
        "  [Store]  One-tap packages, AI CLIs and proot Linux\n" +
        "  [Tune]   Settings - dynamic colors + terminal theme\n" +
        "\n" +
        "AI ASSISTANTS (install from the Store):\n" +
        "  gemini     Google Gemini CLI (free AI Studio key)\n" +
        "  opencode   Open-source AI coding agent\n" +
        "\n" +
        "PROOT LINUX (install from the Store, then):\n" +
        "  proot-distro login ubuntu    enter Ubuntu\n" +
        "  proot-distro login kali      enter Kali\n" +
        "  proot-distro list            list installed distros\n" +
        "\n" +
        "USEFUL COMMANDS:\n" +
        "  pkg update && pkg upgrade     update everything\n" +
        "  pkg install <name>            install a package\n" +
        "  pkg list-installed            what is installed\n" +
        "  neofetch                      system info\n" +
        "  termux-change-repo            switch package mirror\n" +
        "  termux-setup-storage          access phone storage\n" +
        "\n" +
        "BACKUP:\n" +
        "  lumina backup    save config to ~/lumateux-backup-latest.tar.gz\n";

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
