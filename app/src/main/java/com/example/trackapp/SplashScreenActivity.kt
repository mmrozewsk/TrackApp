package com.example.trackapp

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        val car = findViewById<ImageView>(R.id.car)
        val objectAnimator = ObjectAnimator.ofFloat(car, "translationX", 1500f)
        objectAnimator.duration=3000
        objectAnimator.start()

        Handler().postDelayed({
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        }, 4000.toLong())

    }
}