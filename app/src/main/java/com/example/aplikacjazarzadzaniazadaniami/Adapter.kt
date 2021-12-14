package com.example.aplikacjazarzadzaniazadaniami

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val List: List<CardView>, private val listener: OnItemClickListener, private val listener1: OnItemLongClickListener) :
    RecyclerView.Adapter<Adapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_view,
            parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = List[position]

        holder.textview1.text = currentItem.text1
        holder.textview2.text = currentItem.text2
        holder.textview3.text = currentItem.text3
        holder.textview4.text = currentItem.text4

        when (currentItem.text4) {
            "Priorytet: Najwyższy" -> holder.textview4.setTextColor(Color.parseColor("#FF0000"))
            "Priorytet: Wysoki" -> holder.textview4.setTextColor(Color.parseColor("#FFA500"))
            "Priorytet: Średni" -> holder.textview4.setTextColor(Color.parseColor("#c2b12f"))
            "Priorytet: Niski" -> holder.textview4.setTextColor(Color.parseColor("#0000FF"))
            "Priorytet: Najniższy" -> holder.textview4.setTextColor(Color.parseColor("#147306"))
        }
    }

    override fun getItemCount() = List.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener{

        val textview1: TextView = itemView.findViewById(R.id.textview1)
        val textview2: TextView = itemView.findViewById(R.id.textview2)
        val textview3: TextView = itemView.findViewById(R.id.textview3)
        val textview4: TextView = itemView.findViewById(R.id.textview4)

        init{
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener1.onItemLongClick(position)
            }
            return true
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener{
        fun onItemLongClick(position: Int)
    }
}