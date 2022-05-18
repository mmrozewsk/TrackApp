package com.example.trackapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlin.math.roundToInt


class TimerFragment : Fragment() {
    var time = 0.0
    private var timerStarted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.timer_fragment, container, false)

        val startButton = view.findViewById<Button>(R.id.startButton)
        val resetButton = view.findViewById<Button>(R.id.resetButton)
        val stopButton = view.findViewById<Button>(R.id.stopButton)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        val TimerValueTextView = view.findViewById<TextView>(R.id.TimerValueTextView)

        val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
                TimerValueTextView.text = getTimeStringFromDouble(time)
            }
        }


        activity?.registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))


        startButton.setOnClickListener {
            if(!timerStarted){
                startTimer()
            }
        }

        resetButton.setOnClickListener {
            resetTimer(TimerValueTextView)
        }

        stopButton.setOnClickListener {
            stopTimer()
        }

        saveButton.setOnClickListener {
            saveTime(TimerValueTextView)
        }

        return view
    }

    private lateinit var dataPasser: OnDataPass

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }

    private fun passData(data: String){
        dataPasser.onDataPass(data)
    }

    interface OnDataPass {
        fun onDataPass(data: String)
    }

    private fun saveTime(TimerValueTextView: TextView?) {
        if (TimerValueTextView != null) {
            passData(TimerValueTextView.text.toString())
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun resetTimer(TimerValueTextView: TextView) {
        stopTimer()
        time = 0.0
        TimerValueTextView.text = makeTimeString(0, 0, 0)
    }


    private fun startTimer() {
        val serviceIntent = Intent(activity?.applicationContext, TimerService::class.java)
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        activity?.startService(serviceIntent)
        timerStarted = true
    }

    private fun stopTimer() {
        val serviceIntent = Intent(activity?.applicationContext, TimerService::class.java)
        activity?.stopService(serviceIntent)
        timerStarted = false
    }

}