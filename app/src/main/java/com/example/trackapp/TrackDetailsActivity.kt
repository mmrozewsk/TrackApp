package com.example.trackapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject

class TrackDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_details)
        val db = DBHelper(this.applicationContext)


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
        ("Last time: " + db.getLatest(model[0])).also { last_time.text = it }


        timer.setOnClickListener{
            val intent = Intent(this, TimerActivity::class.java)
            intent.putExtra("trackname", model[0])
            startActivity(intent)
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
}