package com.example.lotto

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.lotto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val lotteryButton = binding.lotteryButton
        val number1 = binding.number1
        val number2 = binding.number2
        val number3 = binding.number3
        val number4 = binding.number4
        val number5 = binding.number5
        val number6 = binding.number6

        val lotteryNumbers = arrayListOf(
            number1,
            number2,
            number3,
            number4,
            number5,
            number6
        )

        //익명 클래스로 객체 생성 : object
        val countDownTimer = object : CountDownTimer(3000, 100){
            override fun onFinish() {

            }

            override fun onTick(p0: Long) {
                lotteryNumbers.forEach {
                    //randon() : 0~0.999 사이 랜덥값 생성
                    val randomNumber = (Math.random() * 45 + 1).toInt()
                    it.text = "$randomNumber"
                }
            }
        }

        lotteryButton.setOnClickListener {
            if(lotteryButton.isAnimating){
                lotteryButton.cancelAnimation()
                countDownTimer.cancel()
            }else{
                lotteryButton.playAnimation()
                countDownTimer.start()
            }

        }

    }
}