package com.example.safenest.adapters

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.safenest.R



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SliderviewAdapter(private val cardList: List<Card>) : RecyclerView.Adapter<SliderviewAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.card_img)
        val cardTitle: TextView = view.findViewById(R.id.cardtittle)
        val cardDescription: TextView = view.findViewById(R.id.description)
        val back :RelativeLayout = view.findViewById(R.id.feature_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feature_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        holder.cardImage.setImageResource(card.imageResId)
        holder.cardTitle.text = card.title
        holder.cardDescription.text = card.description
        holder.back.setBackgroundResource(card.backresID)

        holder.itemView.setOnClickListener {
            val formattedNumber = card.description.replace("-", "")
            Log.d("line37","$formattedNumber")
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$formattedNumber")
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = cardList.size
}

data class Card(
    val imageResId: Int,
    val title: String,
    val description: String,
    val backresID : Int
)
