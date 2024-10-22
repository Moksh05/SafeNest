package com.example.safenest.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.safenest.databinding.ItemSafePlaceBinding
import com.example.safenest.models.SafePlace

class SafePlacesAdapter(
    private val safePlaces: List<SafePlace>,
    private val onItemClick: (SafePlace) -> Unit
) : RecyclerView.Adapter<SafePlacesAdapter.SafePlaceViewHolder>() {

    inner class SafePlaceViewHolder(private val binding: ItemSafePlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(safePlace: SafePlace) {
            binding.safePlaceName.text = safePlace.name
            // Load the icon using Glide
            Glide.with(binding.safePlaceIcon.context)
                .load(safePlace.icon)
                .into(binding.safePlaceIcon)

            binding.root.setOnClickListener {
                onItemClick(safePlace) // Trigger the lambda when item is clicked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SafePlaceViewHolder {
        val binding =
            ItemSafePlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SafePlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SafePlaceViewHolder, position: Int) {
        holder.bind(safePlaces[position])
    }

    override fun getItemCount(): Int = safePlaces.size
}
