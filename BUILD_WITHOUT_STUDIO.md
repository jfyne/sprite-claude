# Building Without Android Studio

You can build the APK without Android Studio using either command-line tools or GitHub Actions.

## Option 1: GitHub Actions (Easiest - No Local Setup)

The repository includes a GitHub Actions workflow that automatically builds the APK for you.

### Steps:

1. **Push your code to GitHub** (already done!)

2. **Trigger the build:**
   - Go to your GitHub repository
   - Click on "Actions" tab
   - Click "Build APK" workflow
   - Click "Run workflow" button
   - Wait for the build to complete (~5 minutes)

3. **Download the APK:**
   - Once the build completes, click on the workflow run
   - Scroll down to "Artifacts"
   - Download `app-debug.apk`

4. **Install on your phone:**
   - Transfer the APK to your phone (email, USB, cloud storage)
   - Open the APK on your phone
   - Enable "Install from unknown sources" if prompted
   - Install and run!

## Option 2: Command Line Build (Local)

### Prerequisites

1. **Install Java JDK 17:**
   ```bash
   # Ubuntu/Debian
   sudo apt install openjdk-17-jdk

   # macOS (using Homebrew)
   brew install openjdk@17

   # Verify installation
   java -version
   ```

2. **Install Android SDK Command Line Tools:**
   ```bash
   # Download SDK command-line tools
   # For Linux:
   wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip

   # For macOS:
   wget https://dl.google.com/android/repository/commandlinetools-mac-9477386_latest.zip

   # Extract
   unzip commandlinetools-*.zip -d ~/android-sdk

   # Set up environment variables
   export ANDROID_HOME=~/android-sdk
   export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
   export PATH=$PATH:$ANDROID_HOME/platform-tools

   # Add to your shell profile (~/.bashrc, ~/.zshrc, etc.)
   echo 'export ANDROID_HOME=~/android-sdk' >> ~/.bashrc
   echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin' >> ~/.bashrc
   echo 'export PATH=$PATH:$ANDROID_HOME/platform-tools' >> ~/.bashrc
   ```

3. **Accept Android SDK licenses:**
   ```bash
   yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
   ```

### Build the APK

```bash
# Navigate to project directory
cd sprite-claude

# Make gradlew executable (if needed)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# The APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

### Install on Phone

**Via USB (ADB):**
```bash
# Install adb if not already installed
# Ubuntu/Debian
sudo apt install adb

# macOS
brew install android-platform-tools

# Enable USB debugging on your phone first
# (Settings > Developer Options > USB Debugging)

# Connect phone via USB and run:
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Via File Transfer:**
1. Copy `app/build/outputs/apk/debug/app-debug.apk` to your phone
2. Open the file on your phone
3. Enable "Install from unknown sources" if prompted
4. Install and run!

## Option 3: Docker Build (No Local Android SDK)

If you have Docker installed:

```bash
# Use official Android Docker image
docker run --rm -v "$(pwd)":/project -w /project \
  mingc/android-build-box:latest \
  bash -c "./gradlew assembleDebug"

# APK will be in app/build/outputs/apk/debug/app-debug.apk
```

## Troubleshooting

### "ANDROID_HOME not set"
```bash
export ANDROID_HOME=~/android-sdk
```

### "SDK location not found"
Create `local.properties` file:
```bash
echo "sdk.dir=$HOME/android-sdk" > local.properties
```

### "Gradle version mismatch"
```bash
./gradlew wrapper --gradle-version=8.2
```

### "Java version incorrect"
```bash
# Check Java version
java -version

# Should show version 17.x
# If not, install JDK 17
```

## Build Output

After successful build, you'll find:
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Size**: ~5-10 MB
- **Signature**: Debug keystore (don't use for production)

## Next Steps

Once you have the APK:
1. Transfer to your Android phone
2. Enable installation from unknown sources
3. Install the APK
4. Open "Sprite Claude" app
5. Configure your sprite name and token
6. Connect and start using Claude Code!
