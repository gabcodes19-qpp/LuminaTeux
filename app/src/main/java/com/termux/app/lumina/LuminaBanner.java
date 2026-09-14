package com.termux.app.lumina;

import com.termux.shared.logger.Logger;
import com.termux.shared.termux.TermuxConstants;

import java.io.File;
import java.io.FileWriter;

/**
 * LuminaTeux terminal welcome banner.
 *
 * Writes the ASCII banner into {@code $PREFIX/etc/motd}. Termux starts its
 * shell as a login shell, so bash prints motd automatically on every new
 * session.
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

    private LuminaBanner() {}

    /** Write the banner into the Termux prefix motd (no-op until bootstrap exists). */
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
            File motd = new File(etc, "motd");
            FileWriter writer = new FileWriter(motd);
            writer.write(BANNER);
            writer.close();
            Logger.logInfo(LOG_TAG, "Installed LuminaTeux motd banner at " + motd.getAbsolutePath());
        } catch (Exception e) {
            Logger.logWarn(LOG_TAG, "Failed to install motd banner: " + e.getMessage());
        }
    }
}
