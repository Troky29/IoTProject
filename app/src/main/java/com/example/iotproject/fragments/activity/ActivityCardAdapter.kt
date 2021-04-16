package com.example.iotproject.fragments.activity

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.iotproject.Constants.Companion.State
import com.example.iotproject.R

class ActivityCardAdapter(private val activityList: List<ActivityCardItem>,
                          val context: Context,
                          private val onActionListener: OnActionListener)
    : RecyclerView.Adapter<ActivityCardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_activity, parent, false)
        return CardViewHolder(itemView, onActionListener)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = activityList[position]

        Glide.with(context).load(currentItem.imageResource)
            .placeholder(R.drawable.hqdefault)
            .into(holder.activityImage)

        //TODO: instead of changing background see if we can diffferentiate
        holder.activityAccess.text = currentItem.access
        if (holder.activityAccess.text != "Pending")
            holder.cardBackground.background = ContextCompat.getDrawable(context, R.drawable.toolbar_background)

        holder.activityDate.text = currentItem.date

        holder.activityImage.setOnClickListener {
            showActivityImage(holder.activityImage.context, currentItem.imageResource)
        }

        holder.acceptButton.setOnClickListener {
            holder.action = State.ALLOW
            holder.onClick(holder.itemView)
        }

        holder.denyButton.setOnClickListener {
            holder.action = State.DENY
            holder.onClick(holder.itemView)
        }

        holder.reportButton.setOnClickListener {
            holder.action = State.REPORT
            holder.onClick(holder.itemView)
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

    class CardViewHolder(itemView: View, private val onOpenListener: OnActionListener) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val activityImage: ImageView = itemView.findViewById(R.id.activityImageView)
        val activityAccess: TextView = itemView.findViewById(R.id.accessTextView)
        val activityDate: TextView = itemView.findViewById(R.id.dateTextView)
        val acceptButton: ImageButton = itemView.findViewById(R.id.acceptImageButton)
        val denyButton: ImageButton = itemView.findViewById(R.id.denyImageButton)
        val reportButton: ImageButton = itemView.findViewById(R.id.reportImageButton)
        val cardBackground: RelativeLayout = itemView.findViewById(R.id.activityCardRelativeLayout)
        var action = State.IGNORE

        override fun onClick(view: View?) {
            val activityCard: RelativeLayout = itemView.findViewById(R.id.activityCardRelativeLayout)
            for (child  in activityCard.children) {
                child.alpha = 0.5f
                //child.isEnabled = false
            }
            onOpenListener.onClick(adapterPosition, action)
        }
    }

    interface OnActionListener {
        fun onClick(position: Int, action: State)
    }
}

data class ActivityCardItem (val access: String, val date: String, val imageResource: String?)