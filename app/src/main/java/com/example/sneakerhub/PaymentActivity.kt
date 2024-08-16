package com.example.EliteEchelons

import ApiHelper
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sneakerhub.Constants.Constants
import com.example.sneakerhub.helpers.CartDbHelper
import com.example.sneakerhub.helpers.PrefsHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import org.json.JSONArray
import org.json.JSONObject

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)

        // Adjust layout for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        val textpay = findViewById<MaterialTextView>(R.id.textpay)
        val phoneEditText = findViewById<EditText>(R.id.phone)
        val btnpay = findViewById<MaterialButton>(R.id.btnpay)

        // Retrieve total cost from database
        val helper = CartDbHelper(applicationContext)
        val totalCost = helper.totalCost()
        textpay.text = "Please Pay KES: $totalCost"
        Log.d("PaymentActivity", "Total cost to pay: $totalCost")

        btnpay.setOnClickListener {
            val phone = phoneEditText.text.toString().trim()

            // Validate phone number input
            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter your phone number.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Prepare API request body
            val api = "${Constants.BASE_URL}/payment"
            val body = JSONObject().apply {
                put("phone", phone)
                put("amount", totalCost)
                put("invoice_no", PrefsHelper.getPrefs(this@PaymentActivity, "invoice_no"))
            }

            // Make API request
            val apiHelper = ApiHelper(applicationContext)
            apiHelper.post2(api, body, object : ApiHelper.CallBack {
                override fun onSuccess(result: JSONArray?) {
                    // Handle JSONArray result
                    Toast.makeText(applicationContext, "Payment success!", Toast.LENGTH_SHORT).show()
                    Log.d("PaymentActivity", "Payment success: $result")
                    // Optionally, clear cart and/or navigate to another screen
                    helper.clearCart() // Clear the cart on successful payment
                }

                override fun onSuccess(result: JSONObject?) {
                    // Handle JSONObject result
                    Toast.makeText(applicationContext, "Payment response: ${result.toString()}", Toast.LENGTH_SHORT).show()
                    Log.d("PaymentActivity", "Payment response: $result")
                    // Optionally, clear cart and/or navigate to another screen
                    helper.clearCart() // Clear the cart on successful payment
                }

                override fun onFailure(result: String?) {
                    // Handle failure
                    Toast.makeText(applicationContext, "Payment failed: $result", Toast.LENGTH_SHORT).show()
                    Log.e("PaymentActivity", "Payment failure: $result")
                }
            })
        }
    }
}
