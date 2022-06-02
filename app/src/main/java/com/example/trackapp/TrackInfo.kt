package com.example.trackapp
class TrackInfo {
    var name: String? = null
    var tournament: String? = null
    var img: String? = null
    var length: String? = null
    var laps: Int? = null
    var date: String? = null
    var bestTime: String? = null
    var lastTime: String? = null

    constructor(){}

    constructor(
        name:String,
        tournament:String,
        img:String,
        length:String,
        laps:Int,
        date:String,
        bestTime:String,
        lastTime:String){
        this.name = name
        this.tournament = tournament
        this.img = img
        this.length = length
        this.laps = laps
        this.date = date
        this.bestTime = bestTime
        this.lastTime = lastTime
    }
}