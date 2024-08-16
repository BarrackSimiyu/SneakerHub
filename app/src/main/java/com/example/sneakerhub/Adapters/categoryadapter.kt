package com.example.sneakerhub.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.EliteEchelons.R

import com.example.sneakerhub.Models.category

class CategoryAdapter(
    private val context: Context,
    private val onItemClick: (category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var itemList: List<category> = listOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.category_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_category,
            parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = itemList[position]
        holder.button.text = category.category_name
        holder.button.setOnClickListener {
            onItemClick(category)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setListItems(data: List<category>) {
        itemList = data
        notifyDataSetChanged()
    }
}
