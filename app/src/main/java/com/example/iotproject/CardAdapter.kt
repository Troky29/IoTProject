package com.example.iotproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private val exampleList: List<CardItem>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_card,
                parent, false)

        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = exampleList[position]

        holder.imageView.setImageResource(currentItem.imageResource)
        holder.accessTextView.text = currentItem.access
        holder.dateTextView.text = currentItem.date
    }

    override fun getItemCount(): Int = exampleList.size

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.card_imageView)
        val accessTextView: TextView = itemView.findViewById(R.id.access_textView)
        val dateTextView: TextView = itemView.findViewById(R.id.date_textView)
    }
}

data class CardItem (val imageResource: Int, val access: String, val date: String)