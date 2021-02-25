package com.example.iotproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class GateCardAdapter(private val exampleList: List<GateCardItem>) : RecyclerView.Adapter<GateCardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.gate_card, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GateCardAdapter.CardViewHolder, position: Int) {
        val currentItem = exampleList[position]

        holder.gateImage.setImageResource(currentItem.imageResource)
        holder.gateName.text = currentItem.name
        holder.gateLocation.text = currentItem.location
        holder.gateState.text = currentItem.state
        holder.gateId.text = currentItem.id
        //TODO: set click listeners
    }

    override fun getItemCount(): Int = exampleList.size

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //TODO: Customaze the gate values
        val gateImage: ImageView = itemView.findViewById(R.id.gateImageView)
        val gateName: TextView = itemView.findViewById(R.id.gateNameTextView)
        val gateLocation: TextView = itemView.findViewById(R.id.gateLocationTextView)
        val gateState: TextView = itemView.findViewById(R.id.gateStateTextView)
        val gateId: TextView = itemView.findViewById(R.id.gateIdTextView)
    }
}

data class GateCardItem (val imageResource: Int, val name: String, val location: String, val state: String, val id: String)