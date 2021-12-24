package com.shahad.dontsayit.ui.game

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.shahad.dontsayit.R

class Adapter(
    private val playerMapWord: MutableMap<Int, MutableList<String>>,
    private val playerName: String,
    private val roomName: String
) : RecyclerView.Adapter<Adapter.ItemAdapter>() {


    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var roomRef: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return ItemAdapter(view)
    }

    override fun onBindViewHolder(holder: ItemAdapter, position: Int) {
       // Log.i("Adapter playerMapWord: $playerName","$playerMapWord")
      val playerword=playerMapWord[position]!!
        Log.i( "player playerword.indexOf", "$position ${playerword[0]}")

        // Log.i("Adapter playerword: $playerName","$playerword")
       // val player=playerword?.split(":")?.let {
            holder.tvUsername.text =playerword[0]
            holder.tvWord.text=playerword[1]

            if(playerName==playerword[0]){
                holder.itemView.visibility=View.GONE
        //    }
        }

        /*   holder.tvUsername.text =player[0]
            holder.tvWord.text=player[1]

        if(playerName==player[0]){
           holder.itemView.visibility=View.GONE
       }*/

        // holder.bind(word)
    }

    override fun getItemCount(): Int {
        return playerMapWord.size
    }

    inner class ItemAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)
        val imgPlayerAnimation: ImageView = itemView.findViewById(R.id.imgPlayerAnimation)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)

       /* fun bind(word: Word) {
            tvWord.text = "${word.wordAr} ${word.wordEn}"
        }*/

    }
}