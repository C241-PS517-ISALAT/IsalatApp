package com.isalatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.isalatapp.R

class DictionaryAdapter(
    private val items: List<Pair<String, List<String>>>,
    private val onClick: (String, List<String>) -> Unit
) : RecyclerView.Adapter<DictionaryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val letterTextView: TextView = view.findViewById(R.id.alphabetTextView)
        private val imageView: ImageView = view.findViewById(R.id.alphabetImageView)

        fun bind(item: Pair<String, List<String>>) {
            letterTextView.text = item.first
            if (item.second.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(item.second[0])
                    .into(imageView)
            }

            itemView.setOnClickListener {
                onClick(item.first, item.second)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_dictionary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}