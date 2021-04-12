package com.example.iotproject.fragments.gate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.iotproject.R
import kotlin.coroutines.coroutineContext


class GateCardAdapter(private val gateList: List<GateCardItem>,val context: Context)
    : RecyclerView.Adapter<GateCardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_gate, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = gateList[position]
        //TODO: make right
        //holder.gateImage.setImageBitmap(currentItem.imageResource)
        if (!currentItem.imageResource.isNullOrEmpty())
            Glide.with(context).load(currentItem.imageResource).into(holder.gateImage)
        else
            holder.gateImage.setImageResource(R.drawable.hqdefault)
        holder.gateName.text = currentItem.name
        holder.gateLocation.text = currentItem.location

        holder.gateButton.setOnClickListener() {
            //TODO:set open operation for the corresponding gate
        }
    }

    override fun getItemCount(): Int = gateList.size

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gateImage: ImageView = itemView.findViewById(R.id.gateImageView)
        val gateName: TextView = itemView.findViewById(R.id.gateNameTextView)
        val gateLocation: TextView = itemView.findViewById(R.id.gateLocationTextView)
        val gateButton: Button = itemView.findViewById(R.id.gateButton)
    }
}

data class GateCardItem(val name: String, val location: String, val imageResource: String?)