package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.chatapp.databinding.ActivitySpalshBinding

class SpalshActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySpalshBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_spalsh)

        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            var intent = Intent(this, EditNickName::class.java)
            startActivity(intent)
            finish()
        }
        handler.postDelayed(runnable, 3000)

        binding.animationView.setOnClickListener{
            handler.removeCallbacks(runnable)
            var intent = Intent(this, EditNickName::class.java)
            startActivity(intent)
            finish()
        }
    }
}