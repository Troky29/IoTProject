package com.example.iotproject.fragments.gate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.iotproject.R

class GateCardAdapter(private val gateList: List<GateCardItem>, val context: Context, private val onOpenListener: OnOpenListener) :
        RecyclerView.Adapter<GateCardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_gate, parent, false)
        return CardViewHolder(itemView, onOpenListener)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = gateList[position]

        Glide.with(context).load(currentItem.imageResource)
            .placeholder(R.drawable.hqdefault)
            .into(holder.gateImage)

        holder.gateName.text = currentItem.name
        holder.gateLocation.text = currentItem.location

        holder.gateButton.setOnClickListener {
            holder.onClick(holder.itemView)
        }
    }

    override fun getItemCount(): Int = gateList.size

    class CardViewHolder(itemView: View, private val onOpenListener: OnOpenListener) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val gateImage: ImageView = itemView.findViewById(R.id.gateImageView)
        val gateName: TextView = itemView.findViewById(R.id.gateNameTextView)
        val gateLocation: TextView = itemView.findViewById(R.id.gateLocationTextView)
        val gateButton: Button = itemView.findViewById(R.id.gateButton)

        override fun onClick(view: View?) {
            onOpenListener.onClick(adapterPosition)
        }
    }

    interface OnOpenListener {
        fun onClick(position: Int)
    }
}

data class GateCardItem(val name: String, val location: String, val imageResource: String?)