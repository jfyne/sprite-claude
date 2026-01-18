package dev.sprite.claude

import com.google.gson.annotations.SerializedName

sealed class SpriteMessage {
    data class Resize(
        @SerializedName("type") val type: String = "resize",
        @SerializedName("cols") val cols: Int,
        @SerializedName("rows") val rows: Int
    ) : SpriteMessage()

    data class SessionInfo(
        @SerializedName("type") val type: String,
        @SerializedName("session_id") val sessionId: String,
        @SerializedName("command") val command: String,
        @SerializedName("created") val created: String,
        @SerializedName("cols") val cols: Int,
        @SerializedName("rows") val rows: Int,
        @SerializedName("tty") val tty: Boolean
    ) : SpriteMessage()

    data class Exit(
        @SerializedName("type") val type: String,
        @SerializedName("exit_code") val exitCode: Int
    ) : SpriteMessage()

    data class PortNotification(
        @SerializedName("type") val type: String,
        @SerializedName("port") val port: Int,
        @SerializedName("proxy_url") val proxyUrl: String?,
        @SerializedName("pid") val pid: Int
    ) : SpriteMessage()
}

object StreamId {
    const val STDIN: Byte = 0x00
    const val STDOUT: Byte = 0x01
    const val STDERR: Byte = 0x02
    const val EXIT: Byte = 0x03
    const val STDIN_EOF: Byte = 0x04
}
