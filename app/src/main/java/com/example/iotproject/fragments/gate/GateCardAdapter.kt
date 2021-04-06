package com.example.iotproject.fragments.gate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iotproject.R


class GateCardAdapter(private val gateList: List<GateCardItem>) : RecyclerView.Adapter<GateCardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_gate, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = gateList[position]

        holder.gateImage.setImageResource(currentItem.imageResource)
        holder.gateName.text = currentItem.name
        holder.gateLocation.text = currentItem.location

        holder.gateButton.setOnClickListener() {
            //TODO:set open operation for the corresponding gate

        }
    }

    override fun getItemCount(): Int = gateList.size

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //TODO: Customaze the gate values
        val gateImage: ImageView = itemView.findViewById(R.id.gateImageView)
        val gateName: TextView = itemView.findViewById(R.id.gateNameTextView)
        val gateLocation: TextView = itemView.findViewById(R.id.gateLocationTextView)
        val gateButton: Button = itemView.findViewById<Button>(R.id.gateButton)
    }
}

data class GateCardItem (val imageResource: Int, val name: String, val location: String)