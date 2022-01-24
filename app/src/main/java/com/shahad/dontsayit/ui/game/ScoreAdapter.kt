package com.shahad.dontsayit.ui.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.model.Player

class ScoreAdapter(private val playersList: MutableMap<String, Player>) :
    RecyclerView.Adapter<ScoreAdapter.ItemAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreAdapter.ItemAdapter {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_score_item, parent, false)
        return ItemAdapter(view)
    }

    override fun onBindViewHolder(holder: ItemAdapter, position: Int) {
        val sortedList = playersList.keys.sorted()
        holder.num.text = "${position + 1}."

        holder.playerName.text = sortedList.elementAt(position)//username
        holder.score.text = playersList[sortedList.elementAt(position)]!!.score.toString()//score
    }

    override fun getItemCount(): Int {
        return playersList.size
    }

    inner class ItemAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val num: TextView = itemView.findViewById(R.id.tvPlayersnumber)
        val playerName: TextView = itemView.findViewById(R.id.tvPlayerName)
        val score: TextView = itemView.findViewById(R.id.tvScore)
    }
}