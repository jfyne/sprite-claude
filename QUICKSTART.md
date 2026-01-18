# Quick Start Guide

Get up and running with Sprite Claude Android in 5 minutes!

## Step 1: Get Your Sprites.dev Account

1. Go to [https://sprites.dev](https://sprites.dev)
2. Sign up for an account
3. Note your API token from the dashboard

## Step 2: Create a Sprite

Using the sprites CLI or web interface:

```bash
# Install sprites CLI (optional)
npm install -g @fly/sprites

# Create a new sprite
sprites create my-claude-sprite

# Your sprite is now ready!
```

Or use the sprites.dev web dashboard to create a sprite.

## Step 3: Build and Install the App

### Option A: Using Android Studio (Recommended)

1. Open Android Studio
2. File → Open → Select the `sprite-claude` folder
3. Wait for Gradle sync to complete
4. Click the "Run" button (green play icon)
5. Select your device or emulator

### Option B: Using Command Line

```bash
# Navigate to project directory
cd sprite-claude

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Or manually install
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Step 4: Configure the App

1. Open the app on your device
2. Tap **Settings**
3. Enter your configuration:
   - **Sprite Name**: `my-claude-sprite` (or whatever you named it)
   - **Sprites Token**: Paste your API token
4. Tap **Save**

## Step 5: Connect and Use

1. Return to the main screen
2. Tap **Connect**
3. Wait for "Connected" status (green)
4. Start typing commands!

### Example Commands

```bash
# Check Claude version
claude --version

# Ask Claude a question
claude "What is the capital of France?"

# List files in the sprite
ls -la

# Check current directory
pwd

# Run Python
python3 -c "print('Hello from sprite!')"

# Use Claude Code interactively
claude
```

## Troubleshooting

### Can't connect?

- ✅ Check your sprite name is correct
- ✅ Verify your token is valid
- ✅ Ensure you have internet connectivity
- ✅ Confirm your sprite exists on sprites.dev

### No output?

- ✅ Try a simple command like `echo "test"`
- ✅ Check the status indicator is green
- ✅ Look for error messages in red

### Token not working?

- ✅ Copy the token directly from sprites.dev dashboard
- ✅ Make sure there are no extra spaces
- ✅ Tokens are case-sensitive!

## Next Steps

- Explore Claude Code capabilities
- Run development tasks in your sprite
- Use the terminal for file operations
- Execute long-running processes

## Need Help?

- 📖 Check the full [README.md](README.md)
- 🌐 Visit [sprites.dev documentation](https://sprites.dev/api)
- 💬 Open an issue on GitHub

Enjoy coding with Claude on Android! 🚀
