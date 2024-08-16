package com.example.EliteEchelons

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var confirmationTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        confirmationTextView = findViewById(R.id.confirmation_text_view)
        confirmationTextView.text = "Your order has been placed successfully!"
    }
}
