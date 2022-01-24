package com.shahad.dontsayit.ui.game

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shahad.dontsayit.data.model.Player
import com.shahad.dontsayit.databinding.RecyclerviewScoreItemBinding

class ScoreAdapter(private val playersList: MutableMap<String, Player>) :
    RecyclerView.Adapter<ScoreAdapter.ItemAdapter>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreAdapter.ItemAdapter {
        val binding = RecyclerviewScoreItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemAdapter(binding)
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

    inner class ItemAdapter(binding: RecyclerviewScoreItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val num: TextView = binding.tvPlayersnumber
        val playerName: TextView = binding.tvPlayerName
        val score: TextView = binding.tvScore
    }
}