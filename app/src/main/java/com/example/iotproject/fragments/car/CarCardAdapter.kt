package com.example.iotproject.fragments.car

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.iotproject.R

class CarCardAdapter(private val carList: List<CarCardItem>) : RecyclerView.Adapter<CarCardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_car, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = carList[position]

        holder.license.text = currentItem.license
        holder.brand.text = currentItem.brand
        holder.color.text = currentItem.color
        if (currentItem.nickname != null && currentItem.deadline != null) {
            holder.nickname.text = currentItem.nickname
            holder.deadline.text = currentItem.deadline
        } else {
            if (holder.guest.visibility != View.GONE) {
                TransitionManager.beginDelayedTransition(holder.guest, AutoTransition())
                holder.guest.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = carList.size

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val carImage: ImageView = itemView.findViewById(R.id.carImageView)
        val license: TextView = itemView.findViewById(R.id.licenseTextView)
        val brand: TextView = itemView.findViewById(R.id.brandTextView)
        val color: TextView = itemView.findViewById(R.id.colorTextView)
        val nickname: TextView = itemView.findViewById(R.id.guestNameTextView)
        val deadline: TextView = itemView.findViewById(R.id.deadlineTextView)
        val guest: ConstraintLayout = itemView.findViewById(R.id.guestConstraintLayout)
    }
}

data class CarCardItem(val license: String, val color: String, val brand: String, val nickname: String?, val deadline: String?)