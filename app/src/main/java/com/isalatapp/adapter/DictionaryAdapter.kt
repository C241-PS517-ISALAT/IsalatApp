package com.isalatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.isalatapp.databinding.ItemDictionaryBinding

class DictionaryAdapter(
    private val items: List<Pair<String, String>>,
    private val itemClickListener: (String, String) -> Unit
) : RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
        val binding = ItemDictionaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DictionaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DictionaryViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, itemClickListener)
    }

    override fun getItemCount(): Int = items.size

    class DictionaryViewHolder(private val binding: ItemDictionaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<String, String>, itemClickListener: (String, String) -> Unit) {
            binding.alphabetTextView.text = item.first
            Glide.with(binding.root.context)
                .load(item.second)
                .into(binding.alphabetImageView)
            binding.root.setOnClickListener {
                val animation: Animation = AlphaAnimation(0.3f, 1.0f)
                animation.duration = 300
                binding.root.startAnimation(animation)

                itemClickListener(item.first, item.second)
            }
        }
    }
}