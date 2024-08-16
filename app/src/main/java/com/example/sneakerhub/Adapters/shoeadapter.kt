package com.example.sneakerhub.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.EliteEchelons.R
import com.example.sneakerhub.Models.shoes

import com.google.android.material.textview.MaterialTextView

class shoeadapter(
    private val context: Context,
    private val onItemClick: (shoes) -> Unit
) : RecyclerView.Adapter<shoeadapter.ViewHolder>() {

    private var itemList2: List<shoes> = listOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brand: MaterialTextView = itemView.findViewById(R.id.brandname)
        val shoe: MaterialTextView = itemView.findViewById(R.id.shoename)
        val price: MaterialTextView = itemView.findViewById(R.id.price)
        val image: ImageView = itemView.findViewById(R.id.shoe_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.single_shoe,
            parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList2[position]
        holder.brand.text = item.brand_name
        holder.shoe.text = item.name
        holder.price.text = "KES ${item.price}"

        Glide.with(context)
            .load(item.photo_url)
            .placeholder(R.drawable.bg3) // Show placeholder while loading
            .error(R.drawable.bg2) // Show error image if loading fails
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return itemList2.size
    }

    fun setListItems(data: List<shoes>) {
        itemList2 = data
        notifyDataSetChanged()
    }

    fun filterList(filteredList: List<shoes>) {
        itemList2 = filteredList
        notifyDataSetChanged()
    }
}
