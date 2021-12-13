package com.shahad.dontsayit.ui.game

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shahad.dontsayit.R

class Adapter(): RecyclerView.Adapter<Adapter.ItemAdapter>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ItemAdapter, position: Int) {
        holder.bind()
        TODO("Not yet implemented")

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
   inner class ItemAdapter (itemView: View) : RecyclerView.ViewHolder(itemView){
      val tvWord: TextView = itemView.findViewById(R.id.tvWord)
      val imgPlayerAnimation: ImageView = itemView.findViewById(R.id.imgPlayerAnimation)
      val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)

       fun bind(){

       }

    }
}