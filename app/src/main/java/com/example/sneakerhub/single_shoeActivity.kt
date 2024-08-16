package com.example.EliteEchelons

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.sneakerhub.adapters.ShoeAdapter
//import com.example.sneakerhub.Models.Shoes
import com.google.gson.GsonBuilder
import com.example.sneakerhub.Constants.Constants
import cz.msebera.android.httpclient.client.methods.RequestBuilder.put
import org.json.JSONArray
import org.json.JSONObject

class single_shoeActivity : AppCompatActivity() {
//    lateinit var itemList2: List<Shoes>
//    lateinit var secondRecyclerView: RecyclerView
//    lateinit var ShoeAdapter: ShoeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_single_shoe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//
//        secondRecyclerView = findViewById(R.id.recycler)
////        ShoeAdapter= ShoeAdapter(applicationContext)// Use the appropriate constructor
//        secondRecyclerView.layoutManager = GridLayoutManager(this,2)
//
//        secondfetch()
//
//    }
//    fun secondfetch() {
//            val api = "${Constants.BASE_URL}/Shoes"
//            val body = JSONObject().apply {
//                put("category_id", intent.extras?.getString("key1"))
//            }
//            Log.d("RequestBody", body.toString())
//
//            val helper = ApiHelper(this)
//            helper.post(api, body, object : ApiHelper.CallBack {
//                override fun onSuccess(result: JSONArray?) {
//                    Log.d("APIResponse", result.toString())
//                    val gson = GsonBuilder().create()
//                    itemList2 = gson.fromJson(result.toString(), Array<Shoes>::class.java).toList()
//                    ShoeAdapter.setListItems(itemList2)
//                    secondRecyclerView.adapter = ShoeAdapter
//
//                }
//
//                override fun onSuccess(result: JSONObject?) {
//                    Log.d("APIResponse", result.toString())
//                    Toast.makeText(applicationContext, result.toString(), Toast.LENGTH_SHORT).show()
//
//                }
//
//                override fun onFailure(result: String?) {
//                    Log.e("APIError", "Error: $result")
//                    Toast.makeText(applicationContext, "Error: $result", Toast.LENGTH_SHORT).show()
//                }
//            })
       }


  }