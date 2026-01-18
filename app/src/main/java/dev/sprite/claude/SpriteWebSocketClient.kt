package dev.sprite.claude

import android.util.Log
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

class SpriteWebSocketClient(
    private val spriteName: String,
    private val token: String
) {

    private val TAG = "SpriteWebSocketClient"
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val gson = Gson()
    private val protocolHandler = BinaryProtocolHandler()

    interface ConnectionListener {
        fun onConnected(sessionId: String?)
        fun onDisconnected()
        fun onError(error: String)
        fun onMessage(message: String, isError: Boolean = false)
    }

    private var connectionListener: ConnectionListener? = null

    fun setConnectionListener(listener: ConnectionListener) {
        this.connectionListener = listener
    }

    fun setProtocolListener(listener: BinaryProtocolHandler.OutputListener) {
        protocolHandler.setOutputListener(listener)
    }

    fun connect(command: String = "claude") {
        val url = "wss://api.sprites.dev/v1/sprites/$spriteName/exec"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket opened")

                // Send the command to execute
                val commandMessage = mapOf(
                    "command" to command,
                    "tty" to false
                )
                val json = gson.toJson(commandMessage)
                webSocket.send(json)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Received text message: $text")

                try {
                    // Try to parse as JSON control message
                    val jsonMap = gson.fromJson(text, Map::class.java)
                    val type = jsonMap["type"] as? String

                    when (type) {
                        "session_info" -> {
                            val sessionId = jsonMap["session_id"] as? String
                            connectionListener?.onConnected(sessionId)
                        }
                        "exit" -> {
                            val exitCode = (jsonMap["exit_code"] as? Double)?.toInt() ?: 0
                            connectionListener?.onMessage("\nProcess exited with code: $exitCode\n", false)
                        }
                        "port_opened", "port_closed" -> {
                            connectionListener?.onMessage("\n[Port event: $text]\n", false)
                        }
                    }
                } catch (e: Exception) {
                    // If not JSON, treat as regular output
                    connectionListener?.onMessage(text, false)
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(TAG, "Received binary message: ${bytes.size} bytes")
                protocolHandler.handleBinaryData(bytes)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code $reason")
                webSocket.close(1000, null)
                connectionListener?.onDisconnected()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket error", t)
                connectionListener?.onError(t.message ?: "Unknown error")
                connectionListener?.onDisconnected()
            }
        })
    }

    fun sendInput(text: String) {
        webSocket?.let { ws ->
            val data = protocolHandler.encodeStdin(text)
            ws.send(data)
        }
    }

    fun sendCommand(command: String) {
        // Send command with newline as if user pressed Enter
        sendInput(command + "\n")
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }

    fun resize(cols: Int, rows: Int) {
        webSocket?.let { ws ->
            val resizeMessage = SpriteMessage.Resize(cols = cols, rows = rows)
            val json = gson.toJson(resizeMessage)
            ws.send(json)
        }
    }
}
