package com.example.iotproject.fragments.activity

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.iotproject.R

class ActivityCardAdapter(private val activityList: List<ActivityCardItem>, val context: Context)
    : RecyclerView.Adapter<ActivityCardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_activity, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = activityList[position]

        Glide.with(context).load(currentItem.imageResource)
            .placeholder(R.drawable.hqdefault)
            .into(holder.activityImage)

        holder.activityAccess.text = currentItem.access
        holder.activityDate.text = currentItem.date

        holder.activityImage.setOnClickListener() {
            showActivityImage(holder.activityImage.context, currentItem.imageResource)
        }
    }

    override fun getItemCount(): Int = activityList.size

    private fun showActivityImage(context: Context, imageResource: String?) {
        val imageDialog: Dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_activity)
        }

        val image = imageDialog.findViewById<ImageView>(R.id.activityDialogImageView)

        Glide.with(context).load(imageResource)
            .placeholder(R.drawable.hqdefault)
            .into(image)

        image.setOnClickListener {
                imageDialog.cancel()
        }

        imageDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        imageDialog.show()
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityImage: ImageView = itemView.findViewById(R.id.activityImageView)
        val activityAccess: TextView = itemView.findViewById(R.id.access_textView)
        val activityDate: TextView = itemView.findViewById(R.id.dateTextView)
    }
}

data class ActivityCardItem (val access: String, val date: String, val imageResource: String?)