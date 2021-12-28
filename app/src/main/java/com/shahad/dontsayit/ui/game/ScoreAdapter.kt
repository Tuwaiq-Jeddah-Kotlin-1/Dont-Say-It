package com.shahad.dontsayit.ui.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shahad.dontsayit.R

class ScoreAdapter(private val playersList:List<String>,private val ScoreList:List<Int>) : RecyclerView.Adapter<ScoreAdapter.ItemAdapter>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreAdapter.ItemAdapter {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_score_item, parent, false)
        return ItemAdapter(view)
    }

    override fun onBindViewHolder(holder:ItemAdapter, position: Int) {
        val sortedScore=ScoreList.sorted()
            holder.num.text="${position+1}."
            holder.playerName.text = playersList[position]//username
            holder.score.text = ScoreList[position].toString()//score
    }

    override fun getItemCount(): Int {
       return ScoreList.size
    }
    inner class ItemAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val num:TextView = itemView.findViewById(R.id.tvPlayersnumber)
         val playerName:TextView = itemView.findViewById(R.id.tvPlayerName)
         val score:TextView = itemView.findViewById(R.id.tvScore)
    }
}