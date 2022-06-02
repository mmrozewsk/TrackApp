package com.example.trackapp


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.trackapp.databinding.ActivityTimerBinding
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

        val timeList = findViewById<ListView>(R.id.ranking)
        val db = DBHelper(this)
        val name: Int = intent.getIntExtra("name", 0)
        val model = readFromAsset(name)
        val trackname = model[0]
        val getList = db.getBests(trackname)
        val listItems = arrayOfNulls<String>(getList.size)
        timeList.invalidateViews()
        for(i in getList.indices){
            listItems[i] = getList[i].date + " time: " + getList[i].time
        }

        val adapter = ArrayAdapter(this, R.layout.single_item, listItems)
        timeList.adapter = adapter


        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))


        binding.startButton.setOnClickListener { startStopTimer() }
        binding.resetButton.setOnClickListener { resetTimer() }
        binding.saveButton.setOnClickListener  {
            saveTime()
            adapter.notifyDataSetChanged()}

    }



    private fun saveTime(){
        val name: Int = intent.getIntExtra("name", 0)
        val db = DBHelper(this)
        val model = readFromAsset(name)

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)

        val time = binding.showTime.text

        db.updateTime(model[0], time.toString(), formatted)
        db.newLap(model[0], formatted, time.toString())
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

    private fun readFromAsset(int: Int): List<String> {

        val modeList = ArrayList<String>()

        val bufferReader = application.resources.openRawResource(R.raw.tracks).bufferedReader()
        val jsonString = bufferReader.use {
            it.readText()
        }

        val jsonArray = JSONArray(jsonString)
        val jsonObject: JSONObject = jsonArray.getJSONObject(int)
        modeList.add(jsonObject.getString("name"))
        modeList.add(jsonObject.getString("tournament"))
        modeList.add(jsonObject.getString("img"))
        modeList.add(jsonObject.getString("length"))
        modeList.add(jsonObject.getString("laps"))
        modeList.add(jsonObject.getString("date"))
        modeList.add(jsonObject.getString("best_time"))
        modeList.add(jsonObject.getString("last_time"))
        return modeList
    }
}

