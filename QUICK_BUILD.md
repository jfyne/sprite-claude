# Quick Build Guide - TL;DR

Get the APK on your phone in 3 steps without Android Studio!

## Easiest Method: GitHub Actions (Recommended)

### Step 1: Push to GitHub
```bash
# Already done! Code is pushed to your repo
```

### Step 2: Trigger Build
1. Go to your GitHub repository
2. Click "Actions" tab
3. Click "Build APK" workflow (left sidebar)
4. Click "Run workflow" button (top right)
5. Click green "Run workflow" button
6. Wait ~5 minutes for build to complete ✅

### Step 3: Download & Install
1. Click on the completed workflow run
2. Scroll down to "Artifacts" section
3. Download `app-debug.apk` (it's a ZIP file)
4. Extract the ZIP to get `app-debug.apk`
5. Send APK to your phone (email/cloud/USB)
6. Open APK on phone → Install
7. Done! 🎉

## Alternative: Command Line (If you have Java)

```bash
# One command to build
./gradlew assembleDebug

# APK is here:
ls -lh app/build/outputs/apk/debug/app-debug.apk

# Send to phone and install!
```

## Alternative: Docker (No Java needed)

```bash
# One command with Docker
docker run --rm -v "$(pwd)":/project -w /project \
  mingc/android-build-box:latest \
  bash -c "./gradlew assembleDebug"

# APK is at: app/build/outputs/apk/debug/app-debug.apk
```

## Installing on Phone

1. **Transfer APK** to your phone (any method)
2. **Open** the APK file
3. **Allow** installation from unknown sources (if prompted)
4. **Install** and you're done!

## First Use

1. Open "Sprite Claude" app
2. Tap "Settings"
3. Enter:
   - Sprite name (from sprites.dev)
   - Token (from sprites.dev)
4. Tap "Save"
5. Tap "Connect"
6. Start chatting with Claude! 💬

## Troubleshooting

**Build fails?**
- Check [BUILD_WITHOUT_STUDIO.md](BUILD_WITHOUT_STUDIO.md)

**Install fails?**
- Check [INSTALL_APK.md](INSTALL_APK.md)

**Can't connect?**
- Verify sprite name and token
- Check internet connection

## That's It!

You now have Claude Code running on your Android phone via sprites.dev! 🚀

---

For detailed documentation:
- **Building**: [BUILD_WITHOUT_STUDIO.md](BUILD_WITHOUT_STUDIO.md)
- **Installing**: [INSTALL_APK.md](INSTALL_APK.md)
- **Usage**: [README.md](README.md)
- **Quick Start**: [QUICKSTART.md](QUICKSTART.md)
