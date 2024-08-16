package com.example.EliteEchelons

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH = 3000L // 3seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val loadingText = findViewById<TextView>(R.id.loading_text)
        val dancingImage = findViewById<ImageView>(R.id.dancing_image)

        // Load the animation
        val danceAnimation = AnimationUtils.loadAnimation(this, R.anim.animation)
        dancingImage.startAnimation(danceAnimation)

        // Use a Handler to delay the transition to the sign-in activity
        Handler().postDelayed({
            // Start the SignIn activity
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the splash activity so it can't be returned to
        }, SPLASH_DISPLAY_LENGTH)
    }
}