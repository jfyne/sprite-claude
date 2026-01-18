# Installing the APK on Your Phone

Once you have the `app-debug.apk` file, here's how to install it on your Android phone.

## Prerequisites

- Android phone running Android 7.0 (Nougat) or higher
- APK file transferred to your phone or available for download

## Installation Methods

### Method 1: Direct Install (Easiest)

1. **Transfer the APK to your phone:**
   - Email it to yourself and download on phone
   - Upload to Google Drive/Dropbox and download on phone
   - Use USB cable and copy to phone storage
   - Use adb (see Method 3)

2. **Open the APK file:**
   - Find the APK in your Downloads folder or Files app
   - Tap on `app-debug.apk`

3. **Enable installation from unknown sources:**
   - Android will ask for permission
   - Tap "Settings"
   - Enable "Install unknown apps" or "Allow from this source"
   - Go back and tap "Install"

4. **Complete installation:**
   - Tap "Install"
   - Wait for installation to complete
   - Tap "Open" or find "Sprite Claude" in your app drawer

### Method 2: Using a QR Code (if hosted online)

If someone hosts the APK online and generates a QR code:
1. Scan the QR code with your phone camera
2. Download the APK
3. Follow steps 2-4 from Method 1

### Method 3: Using ADB (Advanced)

If you have a computer with ADB installed:

1. **Enable Developer Options on phone:**
   - Go to Settings > About Phone
   - Tap "Build Number" 7 times
   - Developer Options will be enabled

2. **Enable USB Debugging:**
   - Settings > System > Developer Options
   - Enable "USB Debugging"

3. **Connect phone to computer:**
   - Use USB cable
   - On phone, allow USB debugging when prompted

4. **Install via ADB:**
   ```bash
   # Check if phone is detected
   adb devices

   # Install APK
   adb install app-debug.apk

   # Or if app already installed (reinstall)
   adb install -r app-debug.apk
   ```

## First Run Setup

After installation:

1. **Open Sprite Claude app**

2. **Configure settings:**
   - Tap "Settings" button
   - Enter your sprite name
   - Enter your sprites.dev token
   - Tap "Save"

3. **Connect:**
   - Tap "Connect"
   - Wait for connection (status will turn green)
   - Start chatting with Claude!

## Troubleshooting

### "App not installed" error

**Cause**: Conflicting signature or corrupted APK

**Solutions**:
- Uninstall any previous version first
- Re-download the APK (might be corrupted)
- Try installing with ADB instead

### "Install blocked" or "Can't install unknown apps"

**Cause**: Security settings prevent installation

**Solutions**:
- Settings > Security > Unknown Sources (enable)
- Or Settings > Apps > Special Access > Install unknown apps > [Your File Manager] > Allow

### "Parse error" or "There was a problem parsing the package"

**Cause**: APK is corrupted or incompatible

**Solutions**:
- Re-download the APK
- Verify your Android version is 7.0+
- Check if APK downloaded completely (should be ~5-10 MB)

### App crashes on launch

**Cause**: Missing dependencies or incompatible device

**Solutions**:
- Check Android version (must be 7.0+)
- Clear app cache: Settings > Apps > Sprite Claude > Storage > Clear Cache
- Reinstall the app
- Check device logs with: `adb logcat | grep SpriteClaudeApp`

## Security Considerations

### Debug APK vs Release APK

- **Debug APK** (what we're building):
  - Signed with debug keystore
  - Larger file size
  - More verbose logging
  - Fine for personal use
  - **Don't publish on Play Store**

- **Release APK** (for distribution):
  - Requires signing with release keystore
  - Optimized and minified
  - Production-ready

### Is it safe?

Yes, as long as you:
- ✅ Build it yourself from source
- ✅ Or download from trusted CI/CD (GitHub Actions)
- ❌ Don't download from random websites
- ❌ Don't share your debug APK publicly

### Permissions

The app only requests:
- **INTERNET** - To connect to sprites.dev WebSocket
- **ACCESS_NETWORK_STATE** - To check connectivity

No sensitive permissions required!

## Updating the App

To update to a newer version:

1. Build/download new APK
2. Install directly over the old version
3. Settings and data will be preserved

Or:
1. Uninstall old version
2. Install new version
3. Reconfigure settings

## Uninstalling

To remove the app:
1. Long-press app icon
2. Tap "Uninstall" or drag to "Uninstall"
3. Confirm removal

Or via Settings:
1. Settings > Apps > Sprite Claude
2. Tap "Uninstall"

## File Locations

After installation:
- **App**: `/data/app/dev.sprite.claude/`
- **Settings**: `/data/data/dev.sprite.claude/files/datastore/`
- **APK size**: ~5-10 MB
- **Storage used**: <50 MB

## Getting Help

If you encounter issues:
1. Check this troubleshooting guide
2. Review the full [README.md](README.md)
3. Check GitHub Issues
4. Enable USB debugging and check logs with `adb logcat`

Happy coding with Claude on Android! 📱✨
