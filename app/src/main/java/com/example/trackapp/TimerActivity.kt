package com.example.trackapp


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.trackapp.databinding.ActivityTimerBinding
import kotlin.math.roundToInt

class TimerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimerBinding
    private var timerStarted = false
    private lateinit var  serviceIntent: Intent
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        val startButton = findViewById<Button>(R.id.start_button)
        val resetButton = findViewById<Button>(R.id.reset_button)
        val showTime = findViewById<TextView>(R.id.show_time)

        binding.startButton.setOnClickListener { startStopTimer() }
        binding.resetButton.setOnClickListener { resetTimer() }
    }

    private fun resetTimer()
    {
        stopTimer()
        time = 0.0
        binding.showTime.text = getTimeStringFromDouble(time)
    }

    private fun startStopTimer()
    {
        if(timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer()
    {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        binding.startButton.text = "Stop"
        timerStarted = true
    }

    private fun stopTimer()
    {
        stopService(serviceIntent)
        binding.startButton.text = "Start"
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            binding.showTime.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String
    {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hour: Int, min: Int, sec: Int): String = String.format("%02d:%02d:%02d", hour, min, sec)
}
