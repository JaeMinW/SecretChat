package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.chatapp.databinding.ActivityEditNickNameBinding

class EditNickName : AppCompatActivity() {

    private lateinit var binding : ActivityEditNickNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_nick_name)


        binding.ButtonEnter.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            if (isValidUsername(username)) {
                // 유효한 사용자 이름인 경우 MainActivity로 이동
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
                finish()
            } else {
                // 유효하지 않은 사용자 이름인 경우 오류 메시지 표시
                binding.editTextUsername.error = "유효하지 않은 사용자 이름입니다. 다시 입력해 주세요."
            }
        }
    }

    // 사용자 이름 유효성 검사 함수
    private fun isValidUsername(username: String): Boolean {
        // 사용자 이름이 빈 문자열이 아닌지 확인
        return username.isNotEmpty()
    }
}