package dev.sprite.claude

import okio.ByteString
import java.nio.charset.StandardCharsets

class BinaryProtocolHandler {

    interface OutputListener {
        fun onStdout(data: String)
        fun onStderr(data: String)
        fun onExit(exitCode: Int)
    }

    private var outputListener: OutputListener? = null

    fun setOutputListener(listener: OutputListener) {
        this.outputListener = listener
    }

    fun handleBinaryData(bytes: ByteString) {
        if (bytes.size < 1) return

        val streamId = bytes.getByte(0)
        val payload = bytes.substring(1)

        when (streamId) {
            StreamId.STDOUT -> {
                val text = payload.string(StandardCharsets.UTF_8)
                outputListener?.onStdout(text)
            }
            StreamId.STDERR -> {
                val text = payload.string(StandardCharsets.UTF_8)
                outputListener?.onStderr(text)
            }
            StreamId.EXIT -> {
                if (payload.size >= 1) {
                    val exitCode = payload.getByte(0).toInt()
                    outputListener?.onExit(exitCode)
                }
            }
        }
    }

    fun encodeStdin(text: String): ByteString {
        val payload = text.toByteArray(StandardCharsets.UTF_8)
        val message = ByteArray(payload.size + 1)
        message[0] = StreamId.STDIN
        System.arraycopy(payload, 0, message, 1, payload.size)
        return ByteString.of(*message)
    }

    fun encodeStdinEof(): ByteString {
        return ByteString.of(StreamId.STDIN_EOF)
    }
}
