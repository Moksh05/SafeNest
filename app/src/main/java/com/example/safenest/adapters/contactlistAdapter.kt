package com.example.safenest.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.safenest.R

class contactlistAdapter(private val phoneNumbers: MutableList<Pair<String, String>>) :  RecyclerView.Adapter<contactlistAdapter.PhoneNumberViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneNumberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_card, parent, false)
        return PhoneNumberViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhoneNumberViewHolder, position: Int) {
        val (name, number) = phoneNumbers[position]
        holder.nameTextView.text = name
        holder.numberTextView.text = number
    holder.call.setOnClickListener {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }

        holder.itemView.context.startActivity(intent)
    }


    }

    override fun getItemCount(): Int = phoneNumbers.size

    inner class PhoneNumberViewHolder(view : View) : RecyclerView.ViewHolder(view ){
        val card : CardView = view.findViewById(R.id.contact_card)
        val call:ImageView = view.findViewById(R.id.call_button)
        val nameTextView: TextView = view.findViewById(R.id.contact_name)
        val numberTextView: TextView = view.findViewById(R.id.contact_num)

    }





}