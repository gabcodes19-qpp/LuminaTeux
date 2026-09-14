# LuminaTeux — Roadmap

**Vision:** Termux, but iOS-simple. A terminal that feels like a polished app, not a
Linux terminal — with a one-tap module/AI store instead of `pkg install` typing.

---

## ⚖️ Ground rules (legal, non-negotiable)

- Termux is licensed **GPLv3**. You may fork, modify, rename, and rebrand it.
- **Private development is fine.** But the moment you *distribute* the app
  (Play Store, APK to other people), you must release your full source under GPLv3
  and keep all original copyright notices.
- Keep the upstream `LICENSE.md` and add your own changes under GPLv3 too.

---

## Phase 0 — Foundation (v0.1) ✅ in progress

- [x] Fork clones & builds in GitHub Actions (`.github/workflows/build.yml` — Gradle 9.2.1, AGP 8.13.2, JDK 17)
- [x] Rebrand: app name → **LuminaTeux**, new launcher icon, `versionName 1.0.0`
- [ ] Keep `upstream` remote for syncing new Termux releases

## Phase 1 — Module + AI Store (v0.2) ⭐ the big one

- **Module Store UI** — card-based catalog (like an app store), one-tap install / update / uninstall with size shown first ✅ *v1 shipped: `LuminaStoreActivity` + drawer "Store" button + `LuminaModuleInstaller` engine*
- **Terminal styling** ✅ *v1.2: indigo/cyan `colors.properties` + `termux.properties` (extra-key bar, bar cursor), navy drawer/extra-key chrome, branded intro*
- **Settings toggles** ✅ *v1.2: `LuminaSettingsActivity` — Material You dynamic colors + terminal theme toggle + apply-now*
- **Proot Linux** ✅ *v1.2: proot-distro + Ubuntu / Kali / Arch / Debian one-tap in the Store*
- **Dev tools plugins** ✅ *v1.2: ripgrep, tmux, neovim, htop, jq, zsh, neofetch, cmatrix*
- **Guided setup wizard** — "What are you here for?" (Coding / Web dev / Networking / Security / Automation) → auto-installs the matching bundle
- **One-tap AI CLIs:**
  | CLI | Runtime | Install path in Termux | Free providers |
  |---|---|---|---|
  | **opencode** | Go (single binary) | prebuilt binary / `go install` | multi-provider; free tiers (OpenRouter free models, local Ollama, etc.) |
  | **Gemini CLI** | Node.js ≥ 20 | `npm i -g @google/gemini-cli` | Google Gemini API free tier / AI Studio key |
  | **Zen** (Zencoder agent) | Rust | prebuilt binary | provider-agnostic + Zencoder free options (verify exact project at install) |
  - Most of these run **natively in Termux** (no Ubuntu needed). Ubuntu/proot is a fallback for glibc-only tools.
- **One-tap Linux environments** — proot-distro Ubuntu / Kali / Arch from a menu
- Offline-friendly: cache prebuilt binaries so installs don't always hit the network

## Phase 2 — Terminal UX (v0.3)

- Tabs + split-screen multi-session
- Command palette (VS Code–style `Ctrl+Shift+P`) with command/history/package search
- Syntax highlighting + smart autocomplete
- Customizable extra-key bar (ESC, Ctrl, Tab, arrows, `/`, `|` — like iSH)
- Session save/restore across restarts

## Phase 3 — Design refresh (v0.4)

- **Material You** dynamic color (wallpaper-based theming)
- iOS-style **frosted-glass / blur** panels + translucent bottom bar
- Dark-first with true **AMOLED black** mode + clean light mode
- Custom mono fonts with ligatures (JetBrains Mono / Fira Code) + adjustable size
- Rounded cards, soft shadows, micro-animations
- **Bottom navigation** (Home / Terminal / Store / Settings) instead of buried menus
- Polished onboarding flow

## Phase 4 — Extras (v1.0)

- App lock (PIN / fingerprint)
- SSH key manager
- Encrypted cloud backup/restore of configs + installed packages
- Home-screen widgets + quick-launch shortcuts for favorite commands/scripts

---

## Build facts (from upstream)

- Gradle **9.2.1** (wrapper), Android Gradle Plugin **8.13.2**, JDK **17** (Temurin)
- Modules: `:app`, `:termux-shared`, `:terminal-emulator`, `:terminal-view` — all vendored in-tree (no submodules)
- Package variants: `apt-android-7` (modern) and `apt-android-5` (legacy)
- Build command: `./gradlew assembleDebug` (NDK auto-installed by Gradle on the runner)
