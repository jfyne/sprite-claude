# Sprite Claude Android

An Android application that streams Claude Code CLI via WebSocket using [sprites.dev](https://sprites.dev).

## Overview

This Android app provides a terminal interface to interact with Claude Code running in a Sprites.dev sandbox environment. It connects via WebSocket to stream real-time input/output, allowing you to run Claude Code CLI commands directly from your Android device.

## Features

- **Real-time WebSocket streaming** to sprites.dev sandbox environments
- **Tmux integration** - automatically connects to persistent tmux session with Claude Code
- **Terminal-like interface** for Claude Code interactions
- **Custom terminal keyboard** with special keys (ESC, Tab, Ctrl+C, arrows, etc.)
- **PTY mode support** for proper terminal emulation
- **Binary protocol support** for stdin/stdout/stderr multiplexing
- **Session persistence** - reconnect to existing tmux sessions
- **Configurable settings** for sprite name and authentication token
- **Error highlighting** in terminal output
- **Auto-scrolling terminal** for continuous output
- **Material Design UI** with dark terminal theme

## Architecture

### Core Components

1. **SpriteWebSocketClient** (`SpriteWebSocketClient.kt`)
   - Manages WebSocket connections to sprites.dev API
   - Handles authentication via Bearer token
   - Supports both JSON control messages and binary data streams
   - Implements connection lifecycle management

2. **BinaryProtocolHandler** (`BinaryProtocolHandler.kt`)
   - Encodes/decodes binary protocol for stdin/stdout/stderr
   - Multiplexes streams using stream ID prefixes (0x00-0x04)
   - Converts between byte streams and UTF-8 text

3. **MainActivity** (`MainActivity.kt`)
   - Main terminal UI with input/output display
   - Manages WebSocket connection state
   - Handles user input and command sending
   - Displays real-time output with error highlighting

4. **SettingsActivity** (`SettingsActivity.kt`)
   - Configuration screen for sprite name and token
   - Secure token storage using DataStore

5. **PreferencesManager** (`PreferencesManager.kt`)
   - Persistent storage for user settings
   - Uses Jetpack DataStore for preferences

## Setup

### Prerequisites

- Android Studio (Electric Eel or newer)
- Android SDK 24+ (Android 7.0 Nougat or higher)
- A sprites.dev account and API token
- A configured sprite on sprites.dev

### Getting Your Sprites Token

1. Visit [sprites.dev](https://sprites.dev)
2. Sign up or log in to your account
3. Create a new sprite or use an existing one
4. Get your API token from the dashboard

### Building the App

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/sprite-claude.git
   cd sprite-claude
   ```

2. Open the project in Android Studio

3. Sync Gradle dependencies

4. Build and run on your device or emulator:
   ```bash
   ./gradlew assembleDebug
   ```

## Usage

### Configuration

1. Launch the app
2. Tap the **Settings** button
3. Enter your sprite configuration:
   - **Sprite Name**: Your sprite identifier (e.g., `my-sprite-name`)
   - **Sprites Token**: Your API authentication token
4. Tap **Save**

### Connecting to Claude Code

1. From the main screen, tap **Connect**
2. The app will establish a WebSocket connection to your sprite
3. A tmux session named "claude" is automatically created/attached with Claude Code running
4. Once connected, you'll see the status change to "Connected" (green)
5. You can now interact directly with Claude Code in the tmux session

**Session Persistence**: If you disconnect and reconnect, you'll automatically reattach to the existing tmux session, preserving your conversation history with Claude.

### Using the Terminal Keyboard

The app includes a custom keyboard bar above the standard keyboard with terminal-specific keys:

- **ESC** - Escape key
- **TAB** - Tab key for autocomplete
- **^C** - Ctrl+C (interrupt/cancel)
- **^D** - Ctrl+D (EOF/logout)
- **^Z** - Ctrl+Z (suspend)
- **↑ ↓ ← →** - Arrow keys for navigation and history
- **| / ~ -** - Common terminal characters
- **CLR** - Clear input field

### Running Commands

1. Type commands in the input field at the bottom
2. Tap **Send** or press Enter to execute
3. Output appears in the terminal display above
4. Errors are highlighted in red

Since Claude Code is already running in the tmux session, you can directly ask questions:
```bash
What files are in the current directory?
Help me write a Python script to parse JSON
Show me how to use git
```

You can also use tmux commands:
- `Ctrl+B D` - Detach from tmux (stay connected)
- Type `tmux ls` - List tmux sessions
- Type `exit` - Exit Claude and the tmux session

### Disconnecting

- Tap **Disconnect** to close the WebSocket connection
- The app automatically disconnects when closed

## WebSocket Protocol

### Connection

The app connects to:
```
wss://api.sprites.dev/v1/sprites/{sprite-name}/exec
```

With authentication header:
```
Authorization: Bearer {your-token}
```

### Message Types

#### JSON Control Messages (Text)

**Session Info** (server → client):
```json
{
  "type": "session_info",
  "session_id": "abc123",
  "command": "bash",
  "created": "2026-01-18T12:00:00Z",
  "cols": 80,
  "rows": 24,
  "tty": false
}
```

**Exit** (server → client):
```json
{
  "type": "exit",
  "exit_code": 0
}
```

**Resize** (client → server):
```json
{
  "type": "resize",
  "cols": 120,
  "rows": 40
}
```

#### Binary Data Messages

Format: `[Stream ID (1 byte)] + [Payload (N bytes)]`

Stream IDs:
- `0x00`: stdin (client → server)
- `0x01`: stdout (server → client)
- `0x02`: stderr (server → client)
- `0x03`: exit code (server → client)
- `0x04`: stdin EOF (client → server)

## Technical Details

### Dependencies

- **OkHttp 4.12.0**: WebSocket client implementation
- **Gson 2.10.1**: JSON parsing for control messages
- **Kotlin Coroutines 1.7.3**: Asynchronous operations
- **AndroidX DataStore 1.0.0**: Preferences storage
- **Material Components 1.11.0**: UI components

### Minimum Requirements

- **Android 7.0 (API 24)** or higher
- **Internet permission** (granted automatically)
- Active network connection

### Security

- API tokens are stored securely using AndroidX DataStore
- All connections use WSS (WebSocket Secure) over TLS
- Tokens are excluded from backup/restore operations

## Troubleshooting

### Connection Issues

**Problem**: "Error: Unable to connect"
- Verify your sprite name is correct
- Check that your API token is valid
- Ensure your device has internet connectivity
- Confirm your sprite is running on sprites.dev

**Problem**: "Error: 401 Unauthorized"
- Your API token is invalid or expired
- Generate a new token from sprites.dev dashboard

### Terminal Output Issues

**Problem**: No output appearing
- Check that the command completed successfully
- Some commands may take time to produce output
- Try a simple command like `echo "test"` to verify connectivity

**Problem**: Garbled or incorrect output
- This may indicate a binary protocol issue
- Ensure you're using a compatible sprite configuration
- Check the app logs for detailed error messages

## Development

### Project Structure

```
sprite-claude/
├── app/
│   ├── src/main/
│   │   ├── java/dev/sprite/claude/
│   │   │   ├── MainActivity.kt
│   │   │   ├── SettingsActivity.kt
│   │   │   ├── SpriteWebSocketClient.kt
│   │   │   ├── BinaryProtocolHandler.kt
│   │   │   ├── PreferencesManager.kt
│   │   │   └── SpriteMessage.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   └── drawable/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

### Building from Source

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing configuration)
./gradlew assembleRelease

# Run tests
./gradlew test

# Install on connected device
./gradlew installDebug
```

### Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

MIT License - see LICENSE file for details

## Resources

- [Sprites.dev Documentation](https://sprites.dev/api)
- [WebSocket API Spec](https://sprites.dev/api/sprites/exec)
- [Claude Code Documentation](https://docs.anthropic.com)
- [Android Development Guide](https://developer.android.com)

## Acknowledgments

- Built with [sprites.dev](https://sprites.dev) by Fly.io
- Powered by [Claude](https://anthropic.com) by Anthropic
- Uses [OkHttp](https://square.github.io/okhttp/) for WebSocket connectivity

## Support

For issues or questions:
- Check the [sprites.dev documentation](https://sprites.dev/api)
- Review the troubleshooting section above
- Open an issue on GitHub
