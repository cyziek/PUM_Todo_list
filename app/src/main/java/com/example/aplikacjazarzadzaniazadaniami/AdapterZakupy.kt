package com.example.aplikacjazarzadzaniazadaniami

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterZakupy(private val List: List<CardViewZakupy>) : RecyclerView.Adapter<AdapterZakupy.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_view_zakupy,
            parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = List[position]
        holder.textview1.text = currentItem.text1
        holder.check1.isChecked = true

    }

    override fun getItemCount() = List.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textview1: TextView = itemView.findViewById(R.id.textview1)
        val check1: CheckBox = itemView.findViewById(R.id.check1)
    }
}