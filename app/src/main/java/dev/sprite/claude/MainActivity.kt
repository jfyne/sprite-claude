package dev.sprite.claude

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var terminalOutput: TextView
    private lateinit var inputField: EditText
    private lateinit var sendButton: Button
    private lateinit var clearButton: Button
    private lateinit var connectButton: Button
    private lateinit var settingsButton: Button
    private lateinit var statusText: TextView
    private lateinit var terminalScrollView: ScrollView

    // Terminal keyboard buttons
    private lateinit var keyEsc: Button
    private lateinit var keyTab: Button
    private lateinit var keyCtrlC: Button
    private lateinit var keyCtrlD: Button
    private lateinit var keyCtrlZ: Button
    private lateinit var keyArrowUp: Button
    private lateinit var keyArrowDown: Button
    private lateinit var keyArrowLeft: Button
    private lateinit var keyArrowRight: Button
    private lateinit var keyPipe: Button
    private lateinit var keySlash: Button
    private lateinit var keyTilde: Button
    private lateinit var keyMinus: Button
    private lateinit var keyClear: Button

    private lateinit var preferencesManager: PreferencesManager
    private var webSocketClient: SpriteWebSocketClient? = null
    private var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferencesManager = PreferencesManager(this)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        terminalOutput = findViewById(R.id.terminalOutput)
        inputField = findViewById(R.id.inputField)
        sendButton = findViewById(R.id.sendButton)
        clearButton = findViewById(R.id.clearButton)
        connectButton = findViewById(R.id.connectButton)
        settingsButton = findViewById(R.id.settingsButton)
        statusText = findViewById(R.id.statusText)
        terminalScrollView = findViewById(R.id.terminalScrollView)

        // Terminal keyboard buttons
        keyEsc = findViewById(R.id.keyEsc)
        keyTab = findViewById(R.id.keyTab)
        keyCtrlC = findViewById(R.id.keyCtrlC)
        keyCtrlD = findViewById(R.id.keyCtrlD)
        keyCtrlZ = findViewById(R.id.keyCtrlZ)
        keyArrowUp = findViewById(R.id.keyArrowUp)
        keyArrowDown = findViewById(R.id.keyArrowDown)
        keyArrowLeft = findViewById(R.id.keyArrowLeft)
        keyArrowRight = findViewById(R.id.keyArrowRight)
        keyPipe = findViewById(R.id.keyPipe)
        keySlash = findViewById(R.id.keySlash)
        keyTilde = findViewById(R.id.keyTilde)
        keyMinus = findViewById(R.id.keyMinus)
        keyClear = findViewById(R.id.keyClear)

        updateConnectionState(false)
    }

    private fun setupListeners() {
        connectButton.setOnClickListener {
            if (isConnected) {
                disconnect()
            } else {
                connect()
            }
        }

        sendButton.setOnClickListener {
            sendCommand()
        }

        clearButton.setOnClickListener {
            terminalOutput.text = ""
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        inputField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendCommand()
                true
            } else {
                false
            }
        }

        setupTerminalKeyboard()
    }

    private fun setupTerminalKeyboard() {
        keyEsc.setOnClickListener { sendSpecialKey("\u001B") } // ESC
        keyTab.setOnClickListener { sendSpecialKey("\t") } // Tab
        keyCtrlC.setOnClickListener { sendSpecialKey("\u0003") } // Ctrl+C
        keyCtrlD.setOnClickListener { sendSpecialKey("\u0004") } // Ctrl+D
        keyCtrlZ.setOnClickListener { sendSpecialKey("\u001A") } // Ctrl+Z
        keyArrowUp.setOnClickListener { sendSpecialKey("\u001B[A") } // Arrow Up
        keyArrowDown.setOnClickListener { sendSpecialKey("\u001B[B") } // Arrow Down
        keyArrowRight.setOnClickListener { sendSpecialKey("\u001B[C") } // Arrow Right
        keyArrowLeft.setOnClickListener { sendSpecialKey("\u001B[D") } // Arrow Left
        keyPipe.setOnClickListener { insertText("|") }
        keySlash.setOnClickListener { insertText("/") }
        keyTilde.setOnClickListener { insertText("~") }
        keyMinus.setOnClickListener { insertText("-") }
        keyClear.setOnClickListener { inputField.text.clear() }
    }

    private fun sendSpecialKey(key: String) {
        if (isConnected) {
            webSocketClient?.sendInput(key)
        }
    }

    private fun insertText(text: String) {
        val currentPosition = inputField.selectionStart
        val currentText = inputField.text.toString()
        val newText = currentText.substring(0, currentPosition) + text +
                      currentText.substring(currentPosition)
        inputField.setText(newText)
        inputField.setSelection(currentPosition + text.length)
    }

    private fun connect() {
        lifecycleScope.launch {
            val spriteName = preferencesManager.spriteNameFlow.first()
            val token = preferencesManager.tokenFlow.first()

            if (spriteName.isNullOrBlank() || token.isNullOrBlank()) {
                Toast.makeText(
                    this@MainActivity,
                    R.string.error_missing_config,
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                return@launch
            }

            updateStatus(getString(R.string.status_connecting), Color.parseColor("#FFFFC107"))

            webSocketClient = SpriteWebSocketClient(spriteName, token).apply {
                setConnectionListener(object : SpriteWebSocketClient.ConnectionListener {
                    override fun onConnected(sessionId: String?) {
                        runOnUiThread {
                            updateConnectionState(true)
                            appendToTerminal("Connected to sprite: $spriteName\n", false)
                            appendToTerminal("Starting tmux session with Claude Code...\n", false)
                            sessionId?.let {
                                appendToTerminal("Session ID: $it\n\n", false)
                            }
                        }
                    }

                    override fun onDisconnected() {
                        runOnUiThread {
                            updateConnectionState(false)
                            appendToTerminal("\nDisconnected from sprite.\n", false)
                        }
                    }

                    override fun onError(error: String) {
                        runOnUiThread {
                            updateStatus("Error: $error", Color.parseColor("#FFF44336"))
                            appendToTerminal("ERROR: $error\n", true)
                        }
                    }

                    override fun onMessage(message: String, isError: Boolean) {
                        runOnUiThread {
                            appendToTerminal(message, isError)
                        }
                    }
                })

                setProtocolListener(object : BinaryProtocolHandler.OutputListener {
                    override fun onStdout(data: String) {
                        runOnUiThread {
                            appendToTerminal(data, false)
                        }
                    }

                    override fun onStderr(data: String) {
                        runOnUiThread {
                            appendToTerminal(data, true)
                        }
                    }

                    override fun onExit(exitCode: Int) {
                        runOnUiThread {
                            appendToTerminal("\n[Process exited with code: $exitCode]\n", false)
                        }
                    }
                })

                // Connect to tmux session with Claude running
                // -A: attach to session if exists, create if doesn't
                // -s claude: name the session "claude"
                connect("tmux new-session -A -s claude claude", useTty = true)
            }
        }
    }

    private fun disconnect() {
        webSocketClient?.disconnect()
        webSocketClient = null
        updateConnectionState(false)
    }

    private fun sendCommand() {
        val command = inputField.text.toString()
        if (command.isNotBlank() && isConnected) {
            webSocketClient?.sendCommand(command)
            // Don't echo the command here - the terminal will echo it in PTY mode
            inputField.text.clear()
        }
    }

    private fun appendToTerminal(text: String, isError: Boolean) {
        val spannable = SpannableStringBuilder(text)
        if (isError) {
            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#FFFF6B6B")),
                0,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        terminalOutput.append(spannable)

        // Auto-scroll to bottom
        terminalScrollView.post {
            terminalScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun updateConnectionState(connected: Boolean) {
        isConnected = connected
        runOnUiThread {
            if (connected) {
                connectButton.text = getString(R.string.disconnect)
                updateStatus(getString(R.string.status_connected), Color.parseColor("#FF4CAF50"))
                sendButton.isEnabled = true
                inputField.isEnabled = true
            } else {
                connectButton.text = getString(R.string.connect)
                updateStatus(getString(R.string.status_disconnected), Color.parseColor("#FFF44336"))
                sendButton.isEnabled = false
                inputField.isEnabled = false
            }
        }
    }

    private fun updateStatus(text: String, color: Int) {
        statusText.text = text
        statusText.setTextColor(color)
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()
    }
}
