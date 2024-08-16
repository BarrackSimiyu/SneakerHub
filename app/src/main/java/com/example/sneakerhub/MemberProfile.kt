package com.example.EliteEchelons

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sneakerhub.helpers.PrefsHelper
import de.hdodenhof.circleimageview.CircleImageView

class MemberProfile : AppCompatActivity() {

    companion object {
        private const val TOKEN_KEY = "access_token" // Define your token key here
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_member_profile)

        // Adjust padding for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI elements
        val profileImage = findViewById<CircleImageView>(R.id.profile_image)
        val profileName = findViewById<TextView>(R.id.profile_name)
        val profileEmail = findViewById<TextView>(R.id.profile_email)
        val logoutButton = findViewById<Button>(R.id.logout)

        // Set static profile image
        profileImage.setImageResource(R.drawable.icon1) // Replace with your static image resource

        // Retrieve user details from preferences
        val name = PrefsHelper.getPrefs(this, "surname")
        val email = PrefsHelper.getPrefs(this, "email")

        // Display user details
        profileName.text = name
        profileEmail.text = email

        // Set up logout button
        logoutButton.setOnClickListener {
            PrefsHelper.clearPrefsByKey(this, TOKEN_KEY) // Use the correct key
            startActivity(Intent(this, SignIn::class.java))
            finishAffinity()
        }
    }
}
