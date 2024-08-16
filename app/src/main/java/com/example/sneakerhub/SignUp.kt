package com.example.EliteEchelons

import ApiHelper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sneakerhub.Constants.Constants
import com.example.sneakerhub.Models.Locations
import com.example.sneakerhub.helpers.PrefsHelper
import org.json.JSONArray
import org.json.JSONObject

class SignUp : AppCompatActivity() {
    private lateinit var spinner: Spinner
    private lateinit var selectedItemText: TextView
    private var locations: List<Locations> = emptyList()
    private var location_id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val login = findViewById<TextView>(R.id.login)
        login.setOnClickListener {
            val intent = Intent(applicationContext, SignIn::class.java)
            startActivity(intent)
        }

        // Initialize Spinner and TextView
        spinner = findViewById(R.id.spinner)
        selectedItemText = findViewById(R.id.selectedItemText)

        // Hard-coded data for locations
        locations = listOf(
            Locations("1", "Nairobi"),
            Locations("2", "Kisumu"),
            Locations("3", "Nakuru")
        )

        // Populate Spinner with hard-coded data
        populateSpinner()

        // Set up Spinner item selected listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (locations.isNotEmpty()) {
                    val selectedLocation = locations[position]
                    location_id = selectedLocation.location_id
                    selectedItemText.text = "Selected Location: ${selectedLocation.location}"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(applicationContext, "Please Select a location", Toast.LENGTH_SHORT).show()
            }
        }

        val signup = findViewById<Button>(R.id.btn_sign_up)
        signup.setOnClickListener {
            // Handle sign-up
            val surname = findViewById<EditText>(R.id.surname)
            val others = findViewById<EditText>(R.id.others)
            val email = findViewById<EditText>(R.id.email)
            val phone = findViewById<EditText>(R.id.phone)
            val confirm = findViewById<EditText>(R.id.confirmpassword)
            val password = findViewById<EditText>(R.id.password)

            val surnameText = surname.text.toString()
            val othersText = others.text.toString()
            val emailText = email.text.toString()
            val phoneText = phone.text.toString()
            val passwordText = password.text.toString()
            val confirmPasswordText = confirm.text.toString()

            // Validate that all fields are filled
            when {
                surnameText.isEmpty() -> {
                    Toast.makeText(applicationContext, "Surname is required", Toast.LENGTH_SHORT).show()
                }
                othersText.isEmpty() -> {
                    Toast.makeText(applicationContext, "Other names are required", Toast.LENGTH_SHORT).show()
                }
                emailText.isEmpty() -> {
                    Toast.makeText(applicationContext, "Email is required", Toast.LENGTH_SHORT).show()
                }
                phoneText.isEmpty() -> {
                    Toast.makeText(applicationContext, "Phone number is required", Toast.LENGTH_SHORT).show()
                }
                passwordText.isEmpty() -> {
                    Toast.makeText(applicationContext, "Password is required", Toast.LENGTH_SHORT).show()
                }
                confirmPasswordText.isEmpty() -> {
                    Toast.makeText(applicationContext, "Confirm password is required", Toast.LENGTH_SHORT).show()
                }
                location_id.isEmpty() -> {
                    Toast.makeText(applicationContext, "Location is required", Toast.LENGTH_SHORT).show()
                }
                passwordText.length < 6 -> { // Example: minimum length is 6
                    Toast.makeText(applicationContext, "Password is too short. Minimum length is 6 characters.", Toast.LENGTH_SHORT).show()
                }
                passwordText != confirmPasswordText -> {
                    Toast.makeText(applicationContext, "Password does not match", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Proceed with sign-up
                    val api = Constants.BASE_URL + "/member_signup"
                    val helper = ApiHelper(applicationContext)
                    val body = JSONObject()
                    body.put("surname", surnameText)
                    body.put("others", othersText)
                    body.put("email", emailText)
                    body.put("phone", phoneText)
                    body.put("password", passwordText)
                    body.put("location_id", location_id)

                    helper.post(api, body, object : ApiHelper.CallBack {
                        override fun onSuccess(result: JSONArray?) {
                            // Handle success if needed
                        }

                        override fun onSuccess(result: JSONObject?) {
                            // Check for specific error messages in the server response
                            if (result != null && result.has("error")) {
                                val errorMessage = result.getString("error")
                                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                            } else {
                                // Sign-up successful
                                // Save user details in SharedPreferences
                                PrefsHelper.savePrefs(applicationContext, "userEmail", emailText)
                                PrefsHelper.savePrefs(applicationContext, "userName", surnameText)

                                Toast.makeText(applicationContext, "Sign up successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(applicationContext, SignIn::class.java)
                                startActivity(intent)
                                finish() // Close the Sign-Up activity
                            }
                        }

                        override fun onFailure(result: String?) {
                            // Handle error response from server
                            val errorMessage = result ?: "Unknown error"
                            Toast.makeText(applicationContext, "Failed to sign up: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }

    private fun populateSpinner() {
        if (locations.isNotEmpty()) {
            val locationNames = locations.map { it.location }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locationNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        } else {
            Toast.makeText(this, "No locations available", Toast.LENGTH_SHORT).show()
        }
    }
}
