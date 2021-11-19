package com.example.aplikacjazarzadzaniazadaniami

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val List: List<CardView>) : RecyclerView.Adapter<Adapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_view,
            parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = List[position]
        holder.texview1.text = currentItem.text1
        holder.texview2.text = currentItem.text2
        holder.del.setImageResource(currentItem.imageResources)

    }

    override fun getItemCount() = List.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val texview1: TextView = itemView.findViewById(R.id.textview1)
        val texview2: TextView = itemView.findViewById(R.id.textview2)
        val del: ImageView = itemView.findViewById(R.id.del)
    }
}