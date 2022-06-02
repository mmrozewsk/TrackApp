package com.example.trackapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.Math.max
import java.lang.Math.min
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DBHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null, DATABASE_VER) {
    companion object {
        private val DATABASE_VER = 3
        private val DATABASE_NAME = "tracks_data.db"
        //Table
        private val TABLE_NAME = "tracks"
        private val COL_NAME = "name"
        private val COL_TOURNAMENT = "tournament"
        private val COL_IMG = "img"
        private val COL_LENGTH = "length"
        private val COL_LAPS = "laps"
        private val COL_DATE = "date"
        private val COL_BEST_TIME = "best_time"
        private val COL_LAST_TIME = "last_time"


        private val TABLE_NAME2 = "results"
        private val COL_TIME2 = "time"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY = ("CREATE TABLE $TABLE_NAME ($COL_NAME TEXT PRIMARY KEY, $COL_TOURNAMENT TEXT, $COL_IMG TEXT, $COL_LENGTH TEXT, $COL_LAPS INTEGER, $COL_DATE TEXT, $COL_BEST_TIME TEXT, $COL_LAST_TIME TEXT)")
        db!!.execSQL(CREATE_TABLE_QUERY)

        val CREATE_TABLE_QUERY2 = ("CREATE TABLE $TABLE_NAME2 ($COL_NAME TEXT, $COL_DATE TEXT, $COL_TIME2 TEXT)")
        db!!.execSQL(CREATE_TABLE_QUERY2)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME2")
        onCreate(db)
    }


    fun addTrack(track: TrackInfo): Boolean{
        val db = this.writableDatabase
        val values = ContentValues()
        val trackInput = track.name
        val query = "SELECT * FROM tracks where name = \"$trackInput\""
        val cursor = db.rawQuery(query, null)
        if (cursor.count == 0) {
            values.put(COL_NAME, track.name)
            values.put(COL_TOURNAMENT, track.tournament)
            values.put(COL_IMG, track.img)
            values.put(COL_LENGTH, track.length)
            values.put(COL_LAPS, track.laps)
            values.put(COL_DATE, track.date)
            values.put(COL_BEST_TIME, track.bestTime)
            values.put(COL_LAST_TIME, track.lastTime)

            db.insert("tracks", null, values)
            db.close()
            return true
        }
        return false
    }


    fun getAllTracks(): List<TrackInfo>{
        val listOfUsers = mutableListOf<TrackInfo>()
        val selectQuery = "SELECT * FROM tracks"
        val db = this.writableDatabase
        val cursor =  db.rawQuery(selectQuery, null)
        println("cursor count: " + cursor.count)
        if(cursor.moveToFirst()){
            do {
                val track = TrackInfo()
                track.name = cursor.getString(0)
                track.tournament = cursor.getString(1)
                track.img = cursor.getString(2)
                track.length = cursor.getString(3)
                track.laps = cursor.getInt(4)
                track.date = cursor.getString(5)
                track.bestTime = cursor.getString(6)
                track.lastTime = cursor.getString(7)
                listOfUsers.add(track)
            } while (cursor.moveToNext())
        }
        db.close()
        println(listOfUsers)
        return listOfUsers
    }

    fun newLap(name: String, date: String, time: String){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_NAME, name)
        values.put(COL_DATE, date)
        values.put(COL_TIME2, time)
        db.insert("results", null, values)
        db.close()
    }

    fun getBests(name: String): List<Record>{
        val db = this.writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME2 where name = \"$name\""
        val cursor =  db.rawQuery(selectQuery, null)
        cursor.moveToFirst()
        val range = min(10, cursor.count)
        var i = 0
        val result = mutableListOf<Record>()
        if(cursor.moveToFirst()){
            do {
                val temp = Record()
                temp.name = cursor.getString(0)
                temp.date = cursor.getString(1)
                temp.time = cursor.getString(2)
                result.add(temp)
                i += 1
                cursor.moveToNext()
            } while (i<range)
        }
        db.close()
        return result
    }

    fun updateTime(name: String, time: String, date:String){
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        val db = this.writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME where name = \"$name\""
        val cursor =  db.rawQuery(selectQuery, null)
        cursor.moveToFirst()
        val values = ContentValues()
        val curr_best_time = cursor.getString(6)
        if(curr_best_time == "" || betterTime(curr_best_time, time) == time){
            values.put(COL_BEST_TIME, time)
            values.put(COL_DATE, formatted)
        }
        values.put(COL_LAST_TIME, time)
        db.update("tracks", values, "name = ?", arrayOf(name))

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


}