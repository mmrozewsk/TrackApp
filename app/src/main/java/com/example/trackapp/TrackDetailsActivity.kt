package com.example.trackapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.json.JSONArray
import org.json.JSONObject

class TrackDetailsActivity : AppCompatActivity(), TimerFragment.OnDataPass{
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formatted = current.format(formatter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_details)


        val img = findViewById<ImageView>(R.id.track_img)
        val length = findViewById<TextView>(R.id.length)
        val laps = findViewById<TextView>(R.id.laps)
        val date_of_best_time = findViewById<TextView>(R.id.date)
        val best_time = findViewById<TextView>(R.id.best_time)
        val last_time = findViewById<TextView>(R.id.last_time)
        val timer = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val name: Int = intent.getIntExtra("name", 0)
        val model = readFromAsset(name)
        supportActionBar?.title = model[0]

        Log.d("string", model.toString())
        val id = this.resources.getIdentifier(model[2], "drawable", this.packageName)
        img.setBackgroundResource(id)
        ("Length of one lap: " + model[3]).also { length.text = it }
        ("Laps: " + model[4]).also { laps.text = it }
        ("Date of best time: " + model[5]).also { date_of_best_time.text = it }
        ("Best time: " + model[6]).also { best_time.text = it }
        ("Last time: " + model[7]).also { last_time.text = it }


        val timerFragment = supportFragmentManager.findFragmentById(R.id.TimerFragmentContainerView)
        supportFragmentManager.beginTransaction().apply {
            if (timerFragment != null) {
                hide(timerFragment)
            }
        }.commit()

        timer.setOnClickListener {
            if(img.visibility == View.VISIBLE){
                supportFragmentManager.beginTransaction().apply {
                    if (timerFragment != null) {
                        show(timerFragment)
                        img.visibility = View.INVISIBLE
                    }
                }.commit()
            }
            else{
                supportFragmentManager.beginTransaction().apply {
                    if (timerFragment != null) {
                        hide(timerFragment)
                        img.visibility = View.VISIBLE
                    }
                }.commit()
            }

        }



    }

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

    private fun setTime(id: Int, date: String, time: String) {

        val bufferReader = application.resources.openRawResource(R.raw.tracks).bufferedReader()
        val jsonString = bufferReader.use {
            it.readText()
        }
        val jsonArray = JSONArray(jsonString)
        val jsonObject: JSONObject = jsonArray.getJSONObject(id)
        jsonObject.put("last_time", time)
        if(jsonObject["best_time"] == "" ){
            jsonObject.put("best_time", time)
            jsonObject.put("date", date)
        }
    }
    override fun onDataPass(data: String) {
        setTime(0, data, formatted)
    }
}