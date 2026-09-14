package com.termux.app.lumina;

import java.util.ArrayList;
import java.util.List;

/**
 * Curated catalog of one-tap installs for LuminaTeux.
 *
 * Each item holds a list of shell commands that are executed in sequence
 * inside the Termux bootstrap environment by {@link LuminaModuleInstaller}.
 */
public final class LuminaCatalog {

    public static final String CATEGORY_ESSENTIALS = "Essentials";
    public static final String CATEGORY_AI = "AI Assistants";
    public static final String CATEGORY_LINUX = "Proot Linux";
    public static final String CATEGORY_TOOLS = "Dev & Tools";

    public static final class Item {
        public final String id;
        public final String name;
        public final String tagline;
        public final String category;
        public final String sizeHint;
        public final boolean available;
        public final String[] commands;

        public Item(String id, String name, String tagline, String category,
                    String sizeHint, boolean available, String... commands) {
            this.id = id;
            this.name = name;
            this.tagline = tagline;
            this.category = category;
            this.sizeHint = sizeHint;
            this.available = available;
            this.commands = commands;
        }
    }

    private LuminaCatalog() {}

    public static List<Item> getItems() {
        List<Item> items = new ArrayList<>();

        // --- Essentials (one-line pkg installs) ---
        items.add(new Item("python", "Python 3", "Interpreter, pip and the standard library",
                CATEGORY_ESSENTIALS, "~30 MB", true,
                "pkg install -y python"));
        items.add(new Item("nodejs", "Node.js LTS", "Node 20 runtime with npm",
                CATEGORY_ESSENTIALS, "~25 MB", true,
                "pkg install -y nodejs-lts"));
        items.add(new Item("git", "Git", "Version control for your projects",
                CATEGORY_ESSENTIALS, "~20 MB", true,
                "pkg install -y git"));
        items.add(new Item("ffmpeg", "FFmpeg", "Audio and video toolkit",
                CATEGORY_ESSENTIALS, "~40 MB", true,
                "pkg install -y ffmpeg"));
        items.add(new Item("openssh", "OpenSSH", "SSH server and client",
                CATEGORY_ESSENTIALS, "~15 MB", true,
                "pkg install -y openssh"));
        items.add(new Item("rust", "Rust", "Rust toolchain with cargo",
                CATEGORY_ESSENTIALS, "~250 MB", true,
                "pkg install -y rust"));
        items.add(new Item("golang", "Go", "Go toolchain for building binaries",
                CATEGORY_ESSENTIALS, "~150 MB", true,
                "pkg install -y golang"));
        items.add(new Item("mariadb", "MariaDB", "SQL database server",
                CATEGORY_ESSENTIALS, "~80 MB", true,
                "pkg install -y mariadb"));

        // --- AI Assistants ---
        items.add(new Item("gemini-cli", "Gemini CLI", "Google's agentic coding CLI (free AI Studio key)",
                CATEGORY_AI, "~30 MB", true,
                "pkg install -y nodejs-lts && npm install -g @google/gemini-cli"));
        items.add(new Item("opencode", "opencode", "Open-source AI coding agent (Android build)",
                CATEGORY_AI, "~60 MB", true,
                "pkg install -y curl ripgrep",
                "curl -fL -o opencode.deb https://github.com/guysoft/opencode-termux/releases/latest/download/opencode-aarch64.deb && dpkg -i opencode.deb"));
        items.add(new Item("zen", "Zen CLI", "Zencoder's native AI runtime — installer coming soon",
                CATEGORY_AI, "—", false));

        // --- Proot Linux ---
        items.add(new Item("proot", "Proot Engine", "Run full Linux distros on Android (base)",
                CATEGORY_LINUX, "~10 MB", true,
                "pkg install -y proot-distro"));
        items.add(new Item("ubuntu", "Ubuntu", "Ubuntu 22.04 desktop/server in proot",
                CATEGORY_LINUX, "~400 MB", true,
                "pkg install -y proot-distro && proot-distro install ubuntu"));
        items.add(new Item("kali", "Kali Linux", "Penetration-testing distro in proot",
                CATEGORY_LINUX, "~600 MB", true,
                "pkg install -y proot-distro && proot-distro install kali"));
        items.add(new Item("arch", "Arch Linux", "Rolling-release Arch in proot",
                CATEGORY_LINUX, "~300 MB", true,
                "pkg install -y proot-distro && proot-distro install archlinux"));
        items.add(new Item("debian", "Debian", "Debian stable in proot",
                CATEGORY_LINUX, "~400 MB", true,
                "pkg install -y proot-distro && proot-distro install debian"));

        // --- Dev & Tools (plugins) ---
        items.add(new Item("ripgrep", "ripgrep", "Blazing-fast file search",
                CATEGORY_TOOLS, "~3 MB", true,
                "pkg install -y ripgrep"));
        items.add(new Item("tmux", "tmux", "Terminal multiplexer (tabs & splits in shell)",
                CATEGORY_TOOLS, "~2 MB", true,
                "pkg install -y tmux"));
        items.add(new Item("neovim", "Neovim", "Modern modal text editor",
                CATEGORY_TOOLS, "~20 MB", true,
                "pkg install -y neovim"));
        items.add(new Item("htop", "htop", "Interactive process monitor",
                CATEGORY_TOOLS, "~1 MB", true,
                "pkg install -y htop"));
        items.add(new Item("jq", "jq", "JSON processor for the shell",
                CATEGORY_TOOLS, "~1 MB", true,
                "pkg install -y jq"));
        items.add(new Item("zsh", "Zsh", "Z shell + set as default login shell",
                CATEGORY_TOOLS, "~5 MB", true,
                "pkg install -y zsh && chsh -s zsh"));
        items.add(new Item("neofetch", "neofetch", "Pretty system info in your terminal",
                CATEGORY_TOOLS, "~1 MB", true,
                "pkg install -y neofetch"));
        items.add(new Item("cmatrix", "cmatrix", "The Matrix rain effect",
                CATEGORY_TOOLS, "~1 MB", true,
                "pkg install -y cmatrix"));

        return items;
    }
}
