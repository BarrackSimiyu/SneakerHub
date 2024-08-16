package com.example.sneakerhub.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.EliteEchelons.R
import com.example.sneakerhub.CartItem
import com.example.sneakerhub.helpers.CartDbHelper

class CartAdapter(
    private val context: Context,
    private val onItemRemoved: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var items: MutableList<CartItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = items[position]
        holder.bind(cartItem)

        holder.removeButton.setOnClickListener {
            val shoeId = cartItem.shoe_id
            val helper = CartDbHelper(context)

            // Remove the item from the database
            val isRemoved = helper.clearCartById(shoeId)

            if (isRemoved) {
                // Remove the item from the list and update the adapter
                items.removeAt(position)
                notifyItemRemoved(position)

                // Notify the removal callback
                onItemRemoved(cartItem)
            } else {
                // Handle the case where the item could not be removed from the database
                Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CartItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.shoe_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.shoe_price)
        private val quantityTextView: TextView = itemView.findViewById(R.id.shoe_quantity)
        private val imageView: ImageView = itemView.findViewById(R.id.shoe_image)
        val removeButton: Button = itemView.findViewById(R.id.remove)

        fun bind(cartItem: CartItem) {
            nameTextView.text = cartItem.name
            priceTextView.text = "KES ${cartItem.price}"
            quantityTextView.text = "Quantity: ${cartItem.quantity}"

            // Load image using Glide
            Glide.with(context)
                .load(cartItem.image_url)
                .apply(RequestOptions().placeholder(R.drawable.sh1).error(R.drawable.bg3))
                .into(imageView)
        }
    }
}
