package com.example.EliteEchelons

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sneakerhub.helpers.CartDbHelper
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ShoeDetailActivity : AppCompatActivity() {

    private lateinit var shoeNameTextView: TextView
    private lateinit var shoePriceTextView: TextView
    private lateinit var shoeDescriptionTextView: TextView
    private lateinit var shoeBrandTextView: TextView
    private lateinit var sizeSpinner: Spinner
    private lateinit var quantityEditText: EditText
    private lateinit var orderNowButton: Button
    private lateinit var shoeImageView: ImageView
    private lateinit var cartHelper: CartDbHelper

    private var shoeName: String? = null
    private var shoePrice: Double = 0.0
    private var categoryId: String? = null
    private var description: String? = null
    private var photoUrl: String? = null
    private var shoeId: String? = null
    private var selectedSize: String = "6" // Default size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoe_detail)

        // Initialize views
        shoeNameTextView = findViewById(R.id.shoe_name)
        shoePriceTextView = findViewById(R.id.shoe_price)
        shoeDescriptionTextView = findViewById(R.id.shoe_description)
        shoeBrandTextView = findViewById(R.id.shoe_brand)
        sizeSpinner = findViewById(R.id.size_spinner)
        quantityEditText = findViewById(R.id.quantity_edit_text)
        orderNowButton = findViewById(R.id.order_now_button)
        shoeImageView = findViewById(R.id.shoe_image)
        cartHelper = CartDbHelper(this)

        // Retrieve passed shoe details
        shoeId = intent.getStringExtra("shoeId")
        shoeName = intent.getStringExtra("shoeName")
        shoePrice = intent.getDoubleExtra("shoePrice", 0.0)
        categoryId = intent.getStringExtra("categoryId")
        description = intent.getStringExtra("description")
        photoUrl = intent.getStringExtra("photoUrl")

        // Set views with shoe details
        shoeNameTextView.text = shoeName
        shoePriceTextView.text = "KES $shoePrice" // Display with KES
        shoeDescriptionTextView.text = description
        shoeBrandTextView.text = intent.getStringExtra("brand_name")

        // Load shoe image
        if (!photoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.sh1) // Optional: placeholder while loading
                .into(shoeImageView)
        }

        // Set up size spinner listener
        sizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSize = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Set up add to cart button click listener
        orderNowButton.setOnClickListener {
            val quantity = quantityEditText.text.toString().toIntOrNull()

            if (quantity == null || quantity <= 0) {
                Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
            } else {
                checkQuantityAndAddToCart(shoeId!!, quantity)
            }
        }
    }

    private fun checkQuantityAndAddToCart(shoeId: String, quantity: Int) {
        val url = "https://shoeapp2.pythonanywhere.com/shoe_quantity?shoe_id=$shoeId"
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ShoeDetailActivity, "Error fetching quantity: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    val jsonObject = JSONObject(jsonResponse)
                    val availableQuantity = jsonObject.optInt("quantity", -1)

                    runOnUiThread {
                        if (availableQuantity == -1) {
                            Toast.makeText(this@ShoeDetailActivity, "Shoe not found", Toast.LENGTH_SHORT).show()
                        } else if (quantity > availableQuantity) {
                            Toast.makeText(this@ShoeDetailActivity, "Selected quantity exceeds available stock. Available quantity is $availableQuantity", Toast.LENGTH_SHORT).show()
                        } else {
                            // Concatenate size with quantity and save using CartHelper
                            val success = cartHelper.addItem(
                                shoeId, shoeName!!,
                                shoePrice, categoryId!!, description!!, quantity, photoUrl!!
                            )
                            if (success) {
                                updateCartBadge()
                                Toast.makeText(this@ShoeDetailActivity, "Added $quantity items of size $selectedSize to cart", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@ShoeDetailActivity, "Failed to add items to cart", Toast.LENGTH_SHORT).show()
                            }
                        }
                        orderNowButton.text = "Add to Cart"
                        orderNowButton.isEnabled = true
                    }
                } else {
                    runOnUiThread {
                        orderNowButton.text = "Add to Cart"
                        orderNowButton.isEnabled = true
                        Toast.makeText(this@ShoeDetailActivity, "Error fetching quantity: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun updateCartBadge() {
        // Logic to update cart badge after adding items to the cart
        val cartItemCount = cartHelper.getNumItems() // Assume this method returns the total number of items in the cart
        val badgeTextView = findViewById<TextView>(R.id.cart_badge) // Update with your actual badge view id

        badgeTextView?.visibility = if (cartItemCount > 0) View.VISIBLE else View.GONE
        badgeTextView?.text = cartItemCount.toString()
    }
}
