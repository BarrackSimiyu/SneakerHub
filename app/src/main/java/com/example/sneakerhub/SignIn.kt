package com.example.EliteEchelons

import ApiHelper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sneakerhub.Constants.Constants
import com.example.sneakerhub.helpers.PrefsHelper
import org.json.JSONArray
import org.json.JSONObject

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Link to register while in Login
        val linkToRegister = findViewById<TextView>(R.id.tv_sign_up)
        linkToRegister.setOnClickListener {
            startActivity(Intent(applicationContext, SignUp::class.java))
        }

        // Find Views
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.btn_sign_in)


        login.setOnClickListener {
            // Specify the /member_signin endpoint
            val api = Constants.BASE_URL + "/member_signin"
            val helper = ApiHelper(this)
            // Create a JSON Object of email and password
            val body = JSONObject().apply {
                put("email", email.text.toString())
                put("password", password.text.toString())
            }

            helper.post(api, body, object : ApiHelper.CallBack {
                override fun onSuccess(result: JSONArray?) {
                    // Handle unexpected JSONArray response if necessary
                    Toast.makeText(applicationContext, "Unexpected response format", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(result: JSONObject?) {
                    if (result != null) {
                        // Check if the response contains a member object
                        if (result.has("member")) {
                            // Member found, login successful
                            val access_token = result.getString("access_token")
                            val user = result.getString("member") // {} Object user details

                            // Toast a success message
                            Toast.makeText(applicationContext, "Login successful", Toast.LENGTH_SHORT).show()
                            PrefsHelper.savePrefs(applicationContext,"access_token",access_token)
                            // Convert member to an Object
                            val memberObject = JSONObject(user)
                            val memberId = memberObject.optString("user_id", "")
                            val userEmail = memberObject.optString("email", "")
                            val surname = memberObject.optString("surname", "")

                            // Save member, member_id, email, surname to SharedPrefs
                            PrefsHelper.savePrefs(applicationContext, "userObject", user)
                            PrefsHelper.savePrefs(applicationContext, "user_id", memberId)
                            PrefsHelper.savePrefs(applicationContext, "email", userEmail)
                            PrefsHelper.savePrefs(applicationContext, "surname", surname)

                            // Redirect to MainActivity upon successful login
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.putExtra("USER_NAME", surname) // Replace userName with the actual user's name
                            startActivity(intent)
                            finishAffinity()
                        } else {
                            // No member object found, login failed
                            val errorMessage = result.optString("error", "Invalid credentials")
                            Toast.makeText(applicationContext, "Login failed: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // No response from server
                        Toast.makeText(applicationContext, "No response from server", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(result: String?) {
                    // Failed to connect
                    Toast.makeText(applicationContext, "Failed to connect: ${result ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}