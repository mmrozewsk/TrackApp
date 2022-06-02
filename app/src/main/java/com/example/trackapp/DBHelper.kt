package com.example.trackapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.trackapp.Record

class DBHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null, DATABASE_VER) {
    companion object {
        private val DATABASE_VER = 2
        private val DATABASE_NAME = "tracks.db"
        //Table
        private val TABLE_NAME = "tracks"
        private val COL_TRACKNAME = "trackname"
        private val COL_TIME = "time"
        private val COL_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY = ("CREATE TABLE $TABLE_NAME ($COL_TRACKNAME TEXT, $COL_TIME TEXT, $COL_DATE DATE)")
        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    fun addRecord(track: String, time: String, date: String){
        val db= this.writableDatabase
        val values = ContentValues()
        values.put(COL_TRACKNAME, track)
        values.put(COL_TIME, time)
        values.put(COL_DATE, date)
        db.insert("tracks", null, values)
        db.close()
    }

    fun getLatest(name: String): Record{
        val selectQuery = "SELECT * FROM tracks WHERE trackname = \" $name\" ORDER BY date DESC LIMIT 1"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.getCount() == 0) {
            val result = Record(cursor.getString(0), cursor.getString(1), cursor.getString(2))
            return result
        }
        db.close()
        return Record()
    }
}