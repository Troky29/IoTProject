package com.example.iotproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GateCardAdapter(private val exampleList: List<GateCardItem>) : RecyclerView.Adapter<GateCardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GateCardAdapter.CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.gate_card, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GateCardAdapter.CardViewHolder, position: Int) {
        val currentItem = exampleList[position]

        //TODO: Customize the single gate entry
    }

    override fun getItemCount(): Int = exampleList.size

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //TODO: Customazi the gate values
    }
}

data class GateCardItem (val imageResource: Int, val id: String, val location: String, val state: String)