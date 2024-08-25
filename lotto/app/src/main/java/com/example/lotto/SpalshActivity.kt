package com.example.lotto

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.lotto.databinding.ActivitySpalshBinding

class SpalshActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpalshBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_spalsh)

        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        handler.postDelayed(runnable, 3000)

        //애니메이션 클릭시 MainActivity 이동하기 위해 handler에 붙어있는 콜백 제거
        binding.animationView.setOnClickListener {
            handler.removeCallbacks(runnable)
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}