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


        val img = findViewById<ImageView>(R.id.track_img)
        val length = findViewById<TextView>(R.id.length)
        val laps = findViewById<TextView>(R.id.laps)
        val date_of_best_time = findViewById<TextView>(R.id.date)
        val best_time = findViewById<TextView>(R.id.best_time)
        val last_time = findViewById<TextView>(R.id.last_time)
        val timer = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val name: Int = intent.getIntExtra("name", 0)

        val db = DBHelper(this)
        val getList = db.getAllTracks()
        val model = getList[name]
        supportActionBar?.title = model.name

        val id = this.resources.getIdentifier(model.img, "drawable", this.packageName)
        img.setBackgroundResource(id)
        ("Length of one lap: " + model.length).also { length.text = it }
        ("Laps: " + model.laps).also { laps.text = it }
        ("Date of best time: " + model.date).also { date_of_best_time.text = it }
        ("Best time: " + model.bestTime).also { best_time.text = it }
        ("Last time: " + model.lastTime).also { last_time.text = it }


        timer.setOnClickListener{
            val intent = Intent(this, TimerActivity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }



    }


}