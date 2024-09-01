package com.example.chatapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.chatapp.databinding.ActivityMainBinding
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var socket: Socket
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SocketIO", "Activity created")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // 사용자 이름 받아오기
        username = intent.getStringExtra("username")
        binding.username.setText("접속 닉네임 : ${username}")
        // 소켓 옵션 설정
        val options = IO.Options().apply {
            transports = arrayOf("websocket")
        }

        try {
            socket = IO.socket("http://10.0.2.2:4000", options) // 에뮬레이터에서 localhost 대신 10.0.2.2 사용
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }

        // 소켓 이벤트 설정
        setupSocketListeners()

        // 소켓 연결
        socket.connect()

        // 메시지 전송 버튼 클릭 리스너
        binding.buttonSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                socket.emit("chat message", message)
                binding.editTextMessage.text.clear()
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // 사용자 이름을 설정하는 버튼 제거 및 관련 코드 삭제
    }

    private fun setupSocketListeners() {
        // 연결 성공
        socket.on(Socket.EVENT_CONNECT, Emitter.Listener {
            runOnUiThread {
//                binding.chatEnterTextView.setText("채팅방에 입장하였습니다.")
                Log.d("SocketIO", "Connected to server")

                // 서버에 사용자 이름 설정
                if (username != null) {
                    socket.emit("set username", username)
                    Log.d("SocketIO", "Username set to $username")

                    // 사용자 입장 메시지 전송
                    //socket.emit("chat message", "$username 님이 입장하였습니다")
                }

            }
        })

        // 연결 에러
        socket.on(Socket.EVENT_CONNECT_ERROR, Emitter.Listener { args ->
            val error = args[0] as? Throwable
            runOnUiThread {
                binding.textViewChat.append("Connection error: ${error?.message}\n")
                Log.e("SocketIO", "Connection error: ${error?.message}")
            }
        })

        // 메시지 수신
        socket.on("chat message", Emitter.Listener { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as? JSONObject
                Log.d("SocketIO", "Received data: $data")

                val message = data?.optString("message", null)
                val username = data?.optString("username", null)
                val timestamp = data?.optString("timestamp", "")
                    ?.let { String(it.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8) }

                Log.d("SocketIO", "Parsed message: $message, username: $username, timestamp: $timestamp")

                runOnUiThread {
                    val displayMessage =
                        if (username == this.username) {
                            "Me : $message ($timestamp)"
                        } else {
                            "$username : $message ($timestamp)"
                        }
                    binding.textViewChat.append("$displayMessage\n")
                }
            }
        })

        // 연결 해제
        socket.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
            runOnUiThread {
                binding.textViewChat.append("Disconnected from server\n")
                Log.d("SocketIO", "Disconnected from server")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            socket.disconnect()
            socket.off()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
