package com.example.EliteEchelons

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sneakerhub.CartItem
import com.example.sneakerhub.Constants.Constants
import com.example.sneakerhub.adapters.CartAdapter
import com.example.sneakerhub.helpers.CartDbHelper
import com.example.sneakerhub.helpers.PrefsHelper
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Random

class CartActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback
    private var userId: String? = null
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userId = PrefsHelper.getPrefs(applicationContext, "user_id")

        val invoiceNo = generateInvoiceNumber()

        // Save Invoice No to Prefs
        PrefsHelper.savePrefs(applicationContext, "invoice_no", invoiceNo)

        val helper = CartDbHelper(applicationContext)
        val checkoutButton = findViewById<MaterialButton>(R.id.checkoutbtn)

        if (helper.totalCost() == 0.0) {
            checkoutButton.visibility = View.GONE
        }

        checkoutButton.setOnClickListener {
            handleCheckout() // Call handleCheckout on click
        }

        val total = findViewById<MaterialTextView>(R.id.totalcost)
        total.text = helper.totalCost().toString()

        val recycler = findViewById<RecyclerView>(R.id.cartrecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)

        cartAdapter = CartAdapter(this) { cartItem ->
            // Handle item removal here
            val success = helper.deleteItem(cartItem.shoe_id)
            if (success) {
                Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show()
                updateCartItems() // Refresh the list
            } else {
//                Toast.makeText(this, "Failed to remove item", Toast.LENGTH_SHORT).show()
            }
        }
        recycler.adapter = cartAdapter

        updateCartItems() // Initial load

        // Set up the EditText for location
        val locationEditText = findViewById<MaterialTextView>(R.id.location)
        locationEditText.setOnClickListener {
            requestLocation() // Request location when the EditText is clicked
        }
    }

    private fun updateCartItems() {
        val helper = CartDbHelper(applicationContext)
        val cursor = helper.getAllItems()
        val cartItems = mutableListOf<CartItem>()
        while (cursor.moveToNext()) {
            val shoeId = cursor.getString(cursor.getColumnIndexOrThrow(CartDbHelper.COLUMN_SHOE_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(CartDbHelper.COLUMN_NAME))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow(CartDbHelper.COLUMN_PRICE))
            val categoryId =
                cursor.getString(cursor.getColumnIndexOrThrow(CartDbHelper.COLUMN_CATEGORY_ID))
            val description =
                cursor.getString(cursor.getColumnIndexOrThrow(CartDbHelper.COLUMN_DESCRIPTION))
            val quantityWithSize = cursor.getString(cursor.getColumnIndexOrThrow(CartDbHelper.COLUMN_QUANTITY))
            val imageUrl =
                cursor.getString(cursor.getColumnIndexOrThrow(CartDbHelper.COLUMN_IMAGE_URL))

            // Create CartItem with quantity concatenated with size
            val cartItem =
                CartItem(shoeId, name, price, categoryId, description, quantityWithSize, imageUrl)
            cartItems.add(cartItem)
        }
        cursor.close() // Don't forget to close the cursor

        cartAdapter.updateItems(cartItems)
    }

    // Generate Invoice Number Function
    private fun generateInvoiceNumber(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        val currentTime = Date()
        val timestamp = dateFormat.format(currentTime)

        // You can use a random number or a sequential number to add uniqueness to the invoice number
        // For example, using a random number:
        val random = Random()
        val randomNumber = random.nextInt(1000) // Change the upper bound as needed

        // Combine the timestamp and random number to create the invoice number
        return "INV-$timestamp-$randomNumber" // Unique
    }

    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                123
            )
        } else {
            getLocation() // Get Lat and Lon
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val place = getAddress(LatLng(it.latitude, it.longitude))
                    Toast.makeText(applicationContext, "Here: $place", Toast.LENGTH_SHORT).show()

                    // Update the location EditText with the current location
                    val locationEditText = findViewById<MaterialTextView>(R.id.location)
                    locationEditText.text = "Current Location \n $place"

                    requestNewLocation()
                } ?: run {
                    Toast.makeText(applicationContext, "Searching Location", Toast.LENGTH_SHORT)
                        .show()
                    requestNewLocation()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Error: $e", Toast.LENGTH_SHORT).show()
                requestNewLocation()
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation() {
        Log.d("LocationRequest", "Requesting New Location")
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 10000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    location?.let {
                        // Save the coordinates in Prefs
                        PrefsHelper.savePrefs(
                            applicationContext,
                            "latitude",
                            location.latitude.toString()
                        )
                        PrefsHelper.savePrefs(
                            applicationContext,
                            "longitude",
                            location.longitude.toString()
                        )
                    } ?: run {
                        Toast.makeText(applicationContext, "Check GPS", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            mLocationCallback,
            Looper.getMainLooper()
        )
    }

    private fun handleCheckout() {
        val helper = CartDbHelper(applicationContext)
        val cursor = helper.getAllItems()
        val items = mutableListOf<JSONObject>()

        // Extract items from cursor
        while (cursor.moveToNext()) {
            val shoeId = cursor.getString(cursor.getColumnIndexOrThrow(CartDbHelper.COLUMN_SHOE_ID))
            val quantity = cursor.getString(cursor.getColumnIndexOrThrow(CartDbHelper.COLUMN_QUANTITY))
            Toast.makeText(applicationContext, "$quantity", Toast.LENGTH_SHORT).show()
            val jsonObject = JSONObject().apply {
                put("shoeId", shoeId)
                put("quantity", quantity.toInt())
            }
            items.add(jsonObject)
        }
        cursor.close()  // Don't forget to close the cursor

        val userIdInt = userId?.toIntOrNull() ?: 0

        val jsonObject = JSONObject().apply {
            put("userId", userIdInt)
            put("items", JSONArray(items))
        }

        val client = OkHttpClient()
        val url = "${Constants.BASE_URL}/create_order"

        // Get the checkout button
        val checkoutButton = findViewById<MaterialButton>(R.id.checkoutbtn)

        // Change button text to "Processing your order..."
        checkoutButton.text = "Processing your order..."
        checkoutButton.isEnabled = false  // Optionally disable the button to prevent further clicks

        val jsonBody =
            jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val access_token = PrefsHelper.getPrefs(applicationContext,"access_token")
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization","Bearer $access_token")
            .post(jsonBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error: $e", Toast.LENGTH_SHORT).show()
                    // Revert the button text and enable the button again
                    checkoutButton.text = "Checkout"
                    checkoutButton.isEnabled = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        val location = findViewById<MaterialTextView>(R.id.location).text.toString()
                        PrefsHelper.savePrefs(applicationContext, "address", location)

                        // Clear the cart after successful checkout
                        helper.clearCart()

                        // Revert the button text and enable the button again
                        checkoutButton.text = "Checkout"
                        checkoutButton.isEnabled = true

                        Toast.makeText(applicationContext, "Order Placed", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, PaymentActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Failed to place order: ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Revert the button text and enable the button again
                        checkoutButton.text = "Checkout"
                        checkoutButton.isEnabled = true
                    }
                }
            }
        })
    }

    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(applicationContext)
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        return addresses?.get(0)?.getAddressLine(0) ?: "Unknown Location"
    }
}
