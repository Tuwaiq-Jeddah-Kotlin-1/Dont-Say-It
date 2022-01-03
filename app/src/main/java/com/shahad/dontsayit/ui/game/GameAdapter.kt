package com.shahad.dontsayit.ui.game

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.shahad.dontsayit.R

class GameAdapter(
    private val playerMapWord: MutableMap<Int, MutableList<String>>,
    roomName: String,
    private val playerName: String,
    private val playerMapState: MutableList<String>,
    private val playerPic: MutableList<String>,
    private val context: Context
    //  private val context: Context
) : RecyclerView.Adapter<GameAdapter.ItemAdapter>() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val stateRef: DatabaseReference = database.getReference("rooms/$roomName/state")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_words_item, parent, false)
        return ItemAdapter(view)
    }

    override fun onBindViewHolder(holder: ItemAdapter, position: Int) {
        val playerWord = playerMapWord[position]

        playerWord?.let {
            holder.tvUsername.text = playerWord[0]//username
            holder.tvWord.text = playerWord[1]//word
            Log.e("$playerName  onBindViewHolder playerMapWord", "$playerMapWord $position")
            Log.e("$playerName  onBindViewHolder playerMapState", "$playerMapState $position")
            if (playerMapState.size > 0 && position < playerMapState.size) {
                holder.tvState.text = playerMapState[position]//state
                Log.e("$playerName Game adapter",playerPic.toString())
                holder.imgPlayerAnimation.load(playerPic[position])//pic

            }
            /* if (playerName == playerWord[0]) {
                 holder.itemView.visibility = View.GONE
             }*/

            holder.itemView.setOnClickListener {//change state
                //should change color too
                changePlayerState(playerWord[0], playerMapState[position], position, holder)

            }
        }
    }

    private fun changePlayerState(
        playerName: String,
        currentState: String,
        position: Int,
        holder: ItemAdapter
    ) {
        //listOfInPlayers.size>1&&
        if (!playerMapState.contains("winner")) {//this doesn't work if i'm the winner
            if (currentState == "in") {
                stateRef.child(playerName).setValue("out")
                holder.tvState.text = "out"//maybe i don't need to change it here but listener will make it change
                  holder.itemView.setBackgroundColor(Color.GRAY)
            }
            if (currentState == "out") {//in case winner or out,  maybe make winner unchangeable
                stateRef.child(playerName).setValue("in")
                holder.tvState.text = "in"
                holder.itemView.setBackgroundColor(context.resources.getColor(R.color.white))

            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return playerMapWord.size
    }

    inner class ItemAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)
        val imgPlayerAnimation: ImageView = itemView.findViewById(R.id.imgPlayerAnimation)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvState: TextView = itemView.findViewById(R.id.tvState)

    }
}