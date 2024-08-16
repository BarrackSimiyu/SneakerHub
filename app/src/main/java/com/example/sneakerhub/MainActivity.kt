package com.example.EliteEchelons

import ApiHelper
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.EliteEchelons.R
import com.example.sneakerhub.Constants.Constants
import com.example.sneakerhub.Models.category
import com.example.sneakerhub.Models.shoes
import com.example.sneakerhub.adapters.CategoryAdapter
import com.example.sneakerhub.adapters.shoeadapter
import com.example.sneakerhub.helpers.CartDbHelper
import com.example.sneakerhub.helpers.PrefsHelper
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var shoeAdapter: shoeadapter
    private lateinit var usernameTextView: TextView
    private lateinit var cartIcon: ImageView
    private lateinit var cartBadge: TextView
    private lateinit var profileImage: CircleImageView

    private lateinit var recyclerViewCategories: RecyclerView
    private lateinit var recyclerViewShoes: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progress: ProgressBar
    private lateinit var searchEditText: EditText

    private var selectedCategoryId: String? = null
    private lateinit var cartDbHelper: CartDbHelper

    private var allShoesList: List<shoes> = listOf() // List to store all shoes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        initializeViews()

        // Initialize CartDbHelper
        cartDbHelper = CartDbHelper(this)



        // Initialize adapters
        initializeAdapters()

        // Set up RecyclerViews
        setupRecyclerViews()

        // Load data from API
        loadCategories()

        // Set up SwipeRefreshLayout
        setupSwipeRefreshLayout()

        // Set up click listeners
        setupClickListeners()

        // Update cart badge
        refreshCartBadge()

        // Fetch FCM token and Firebase Installation ID
        fetchFcmToken()
        fetchInstallationId()

        // Set up search functionality
        setupSearchFunctionality()
        updateUSerInformation()
    }

    private fun updateUSerInformation(){
        // Set up username
        val user = findViewById<TextView>(R.id.user)
        val token = PrefsHelper.getPrefs(applicationContext, "access_token")
        if (token != null) {
            if (token.isEmpty()){
                user.text = "Not Logged in"
                startActivity(Intent(applicationContext, SignIn::class.java))
            }else{
                val name = PrefsHelper.getPrefs(applicationContext, "surname")
                user.text = "Welcome "+ name
            }
        }

    }

    override fun onResume() {
        super.onResume()
        refreshCartBadge() // Refresh badge when returning to the activity
    }

    private fun initializeViews() {
        recyclerViewCategories = findViewById(R.id.recycler_view)
        recyclerViewShoes = findViewById(R.id.recycler)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        progress = findViewById(R.id.progress)
        usernameTextView = findViewById(R.id.user)
        cartIcon = findViewById(R.id.cart_icon)
        cartBadge = findViewById(R.id.cart_badge)
        searchEditText = findViewById(R.id.etsearch)
        profileImage = findViewById(R.id.profile_image)
    }

    private fun initializeAdapters() {
        categoryAdapter = CategoryAdapter(this) { selectedCategory ->
            onCategorySelected(selectedCategory)
        }
        shoeAdapter = shoeadapter(this) { selectedShoe ->
            onShoeSelected(selectedShoe)
        }
    }

    private fun setupRecyclerViews() {
        recyclerViewCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCategories.adapter = categoryAdapter

        recyclerViewShoes.layoutManager = GridLayoutManager(this, 2)
        recyclerViewShoes.adapter = shoeAdapter
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            loadCategories()
            selectedCategoryId?.let { id -> fetchShoesByCategory(id) }
        }
    }

    private fun setupClickListeners() {
        cartIcon.setOnClickListener {
            openCart()
        }

        profileImage.setOnClickListener {
            openMemberProfile()
        }
    }

    private fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "FCM Token: $token")
                // Optionally, send the token to your server or save it locally
                // sendRegistrationToServer(token)
            } else {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
            }
        }
    }

    private fun fetchInstallationId() {
        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val installationId = task.result
                Log.d("FirebaseInstallations", "Installation ID: $installationId")
                // Optionally, send the Installation ID to your server or save it locally
            } else {
                Log.w("FirebaseInstallations", "Fetching Installation ID failed", task.exception)
            }
        }
    }

    private fun loadCategories() {
        val categoriesUrl = "${Constants.BASE_URL}/shoe_category"
        FetchDataTask { response ->
            Log.d("API Response", "Categories response: $response")
            val categoryListType = object : TypeToken<List<category>>() {}.type
            val categories: List<category>? = Gson().fromJson(response, categoryListType)
            if (categories != null) {
                categoryAdapter.setListItems(categories)
                recyclerViewCategories.adapter = categoryAdapter

                // Automatically select the Sneakers category by default
                val sneakerCategory = categories.find { it.category_name.equals("Sneakers", ignoreCase = true) }
                sneakerCategory?.let {
                    progress.visibility = View.VISIBLE
                    onCategorySelected(it)
                }
            }
            swipeRefreshLayout.isRefreshing = false
        }.execute(categoriesUrl)
    }

    private fun fetchShoesByCategory(categoryId: String) {
        val api = "${Constants.BASE_URL}/shoes"
        val body = JSONObject().apply {
            put("category_id", categoryId)
        }
        val helper = ApiHelper(this)
        helper.post(api, body, object : ApiHelper.CallBack {
            override fun onSuccess(result: JSONArray?) {
                // Handle unexpected JSONArray response if necessary
                // Toast removed
            }

            override fun onSuccess(result: JSONObject?) {
                Log.d("APIResponse", "Raw JSON Result: $result")
                if (result != null) {
                    try {
                        val shoesArray = result.getJSONArray("shoes")
                        Log.d("APIResponse", "Extracted Shoes Array: $shoesArray")

                        val gson = GsonBuilder().create()
                        allShoesList = gson.fromJson(shoesArray.toString(), Array<shoes>::class.java).toList()
                        Log.d("ShoesList", "Parsed Shoes List Size: ${allShoesList.size}")

                        shoeAdapter.setListItems(allShoesList)
                        recyclerViewShoes.adapter = shoeAdapter
                        progress.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                    } catch (e: Exception) {
                        Log.e("APIError", "Error parsing shoes data: ${e.message}")
                        showToast("No shoes available")
                        progress.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }

            override fun onFailure(result: String?) {
                showToast("Error: $result")
                Log.d("APIError", "Shoes Fetch Error: $result")
                progress.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun onCategorySelected(selectedCategory: category) {
        selectedCategoryId = selectedCategory.category_id
        selectedCategoryId?.let { id -> fetchShoesByCategory(id) }
    }

    private fun onShoeSelected(selectedShoe: shoes) {
        val intent = Intent(this, ShoeDetailActivity::class.java).apply {
            putExtra("shoeId", selectedShoe.shoe_id)
            putExtra("shoeName", selectedShoe.name)
            putExtra("shoePrice", selectedShoe.price.toDoubleOrNull()) // Pass as Double
            putExtra("categoryId", selectedShoe.category_id)
            putExtra("description", selectedShoe.description)
            putExtra("brand_name", selectedShoe.brand_name)
            putExtra("photoUrl", selectedShoe.photo_url)
        }
        startActivity(intent)
    }

    private fun openCart() {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }

    private fun openMemberProfile() {
        val intent = Intent(this, MemberProfile::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun refreshCartBadge() {
        val cartCount = cartDbHelper.getNumItems()
        if (cartCount > 0) {
            cartBadge.visibility = View.VISIBLE
            cartBadge.text = cartCount.toString()
        } else {
            cartBadge.visibility = View.GONE
        }
    }

    private fun setupSearchFunctionality() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    val filteredShoes = allShoesList.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                    shoeAdapter.setListItems(filteredShoes)
                } else {
                    shoeAdapter.setListItems(allShoesList)
                }
                recyclerViewShoes.adapter = shoeAdapter
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private inner class FetchDataTask(private val callback: (String?) -> Unit) : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String? {
            val url = params[0]
            return try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.use { it.readText() }
                connection.disconnect()
                response
            } catch (e: Exception) {
                Log.e("FetchDataTask", "Error fetching data", e)
                null
            }
        }

        override fun onPostExecute(result: String?) {
            callback(result)
        }
    }
}
