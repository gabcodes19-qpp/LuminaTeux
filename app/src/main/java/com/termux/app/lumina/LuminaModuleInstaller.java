package com.termux.app.lumina;

import com.termux.shared.termux.TermuxConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Runs catalog install commands inside the Termux bootstrap environment on a
 * background thread, streaming stdout/stderr back to the caller line by line.
 */
public final class LuminaModuleInstaller {

    public interface Listener {
        void onLine(String line);
        void onComplete(boolean success, String summary);
    }

    private static final AtomicBoolean sBusy = new AtomicBoolean(false);

    private LuminaModuleInstaller() {}

    /** True while an install is running. */
    public static boolean isBusy() {
        return sBusy.get();
    }

    public static void install(final LuminaCatalog.Item item, final Listener listener) {
        runCommands(item.commands, listener);
    }

    /** Run an arbitrary list of commands in the Termux environment with live output. */
    public static void runCommands(final String[] commands, final Listener listener) {
        if (!sBusy.compareAndSet(false, true)) {
            listener.onComplete(false, "busy");
            return;
        }
        new Thread(() -> {
            boolean ok = true;
            try {
                for (String command : commands) {
                    listener.onLine("$ " + command);
                    int code = runCommand(command, listener);
                    if (code != 0) {
                        ok = false;
                        listener.onLine("[exit " + code + "]");
                        break;
                    }
                }
            } catch (Exception e) {
                ok = false;
                listener.onLine("Error: " + e.getMessage());
            } finally {
                sBusy.set(false);
                listener.onComplete(ok, ok ? "done" : "failed");
            }
        }, "LuminaInstall").start();
    }

    private static int runCommand(String command, Listener listener) throws Exception {
        String prefixBin = TermuxConstants.TERMUX_BIN_PREFIX_DIR_PATH;
        String prefix = TermuxConstants.TERMUX_PREFIX_DIR_PATH;
        String home = TermuxConstants.TERMUX_HOME_DIR_PATH;

        ProcessBuilder pb = new ProcessBuilder("/system/bin/sh", "-c", command);
        pb.redirectErrorStream(true);

        Map<String, String> env = pb.environment();
        String basePath = env.get("PATH");
        if (basePath == null) basePath = "";
        env.put("PATH", prefixBin + ":" + prefixBin + "/applets:" + basePath);
        env.put("PREFIX", prefix);
        env.put("HOME", home);
        env.put("TMPDIR", prefix + "/tmp");
        env.put("LD_LIBRARY_PATH", prefix + "/lib");
        env.put("LANG", "en_US.UTF-8");
        env.put("TERM", "xterm-256color");

        File workingDir = new File(home);
        if (!workingDir.isDirectory()) {
            workingDir = new File(prefix);
        }
        pb.directory(workingDir);

        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                listener.onLine(line);
            }
        }
        return process.waitFor();
    }
}
