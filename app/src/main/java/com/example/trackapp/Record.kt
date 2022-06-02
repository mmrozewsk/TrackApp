package com.example.trackapp

class Record {
    var name : String? = null
    var time : String? = null
    var date : String? = null

    constructor(){}

    constructor(name:String, time:String, date : String){
        this.name = name
        this.time = time
        this.date = date
    }
}