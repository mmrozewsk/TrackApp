package com.example.trackapp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
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
                val intent = Intent(aView.context, TrackDetailsActivity::class.java)
                intent.putExtra("name", pos)
                startActivity(intent)
            }
        })

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

}