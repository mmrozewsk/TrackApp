package com.example.trackapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = DBHelper(this.applicationContext)

        val t1 = "00:22:30"
        val t2 = "00:22:26"

        val better = betterTime(t1, t2)
        print("Better time: ")
        println(better)
        supportActionBar?.hide()

        val model = readFromAsset()

        val adapter = RecyclerAdapter(model, this)

        val orientation = resources.configuration.orientation

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            rcv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        }
        else{
            rcv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
        rcv.adapter = adapter

        adapter.setOnClickListener(object : RecyclerAdapter.ClickListener{
            override fun onClick(pos: Int, aView: View) {
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val density = displayMetrics.density

                var width = 0.0
                if(orientation == Configuration.ORIENTATION_PORTRAIT) {
                    println("Dane:")
                    println(density)
                    println(displayMetrics.widthPixels)
                    width = displayMetrics.widthPixels.toDouble()/density
                    println(width)
                }else{
                    println("Dane:")
                    println(density)
                    println(displayMetrics.heightPixels)
                    width = displayMetrics.heightPixels.toDouble()/density
                    println(width)
                }

                println(width)
                if ( width >= 600){
                    changeText(pos)
                }else {
                    print("Pos: ")
                    println(pos)
                    val intent = Intent(aView.context, TrackDetailsActivity::class.java)
                    intent.putExtra("name", pos)
                    startActivity(intent)
                }
            }
        })

    }

    private fun betterTime(t1: String, t2: String): String {
        if (t1[0].digitToInt() > t2[0].digitToInt())
            return t2
        else if (t1[0].digitToInt() < t2[0].digitToInt())
            return t1
        else{
            if (t1[1].digitToInt() > t2[1].digitToInt())
                return t2
            else if (t1[1].digitToInt() < t2[1].digitToInt())
                return t1
            else{
                if (t1[3].digitToInt() > t2[3].digitToInt())
                    return t2
                else if (t1[3].digitToInt() < t2[3].digitToInt())
                    return t1
                else{
                    if (t1[4].digitToInt() > t2[4].digitToInt())
                        return t2
                    else if (t1[4].digitToInt() < t2[4].digitToInt())
                        return t1
                    else{
                        if (t1[6].digitToInt() > t2[6].digitToInt())
                            return t2
                        else if (t1[6].digitToInt() < t2[6].digitToInt())
                            return t1
                        else{
                            if (t1[7].digitToInt() > t2[7].digitToInt())
                                return t2
                            else
                                return t1
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun changeText(pos: Int){
        val db = DBHelper(this.applicationContext)
        val model = readFromAsset(pos)

        val img = findViewById<ImageView>(R.id.track_img)
        val id = this.resources.getIdentifier(model[2], "drawable", this.packageName)
        img.setBackgroundResource(id)

        val length = findViewById<TextView>(R.id.length)
        length.text = "length: " + model[3]

        val laps = findViewById<TextView>(R.id.laps)
        laps.text = "laps: " + model[4]

        val date = findViewById<TextView>(R.id.date)
        date.text = "date: " + model[5]

        val best = findViewById<TextView>(R.id.best_time)
        best.text = "best time: " + model[6]

        val last = findViewById<TextView>(R.id.last_time)
        last.text = "last time: " + db.getLatest(model[0])
    }

    private fun readFromAsset(): List<ItemModel> {

        val modeList = mutableListOf<ItemModel>()

        val bufferReader = application.resources.openRawResource(R.raw.tracks).bufferedReader()
        val jsonString = bufferReader.use {
            it.readText()
        }

        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val model = ItemModel(
                jsonObject.getString("name"),
                jsonObject.getString("tournament"),
                jsonObject.getString("img")
            )
            modeList.add(model)
        }
        return modeList
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

}