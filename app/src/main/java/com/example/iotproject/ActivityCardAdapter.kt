package com.example.iotproject

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

class ActivityCardAdapter(private val activityList: List<ActivityCardItem>) : RecyclerView.Adapter<ActivityCardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_activity, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = activityList[position]

        holder.imageView.setImageResource(currentItem.imageResource)
        holder.accessTextView.text = currentItem.access
        holder.dateTextView.text = currentItem.date

        holder.activityCard.setOnClickListener() {
            showActivityImage(holder.imageView.context, holder.imageView.drawable)
        }
    }

    override fun getItemCount(): Int = activityList.size

    private fun showActivityImage(context: Context, content: Drawable) {
        val imageDialog: Dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_activity)
        }

        val image: Drawable? = content.constantState?.newDrawable() ?:
            ResourcesCompat.getDrawable(context.resources, R.drawable.hqdefault, null)


        imageDialog.findViewById<ImageView>(R.id.activityImageView).apply {
            setImageDrawable(image)
            setOnClickListener { imageDialog.cancel() }
        }
        imageDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        imageDialog.show()
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.cardImageView)
        val accessTextView: TextView = itemView.findViewById(R.id.access_textView)
        val dateTextView: TextView = itemView.findViewById(R.id.date_textView)
        val activityCard: CardView = itemView.findViewById(R.id.activityCardView)
    }
}

data class ActivityCardItem (val imageResource: Int, val access: String, val date: String)