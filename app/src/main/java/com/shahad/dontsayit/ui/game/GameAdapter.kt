package com.shahad.dontsayit.ui.game

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.shahad.dontsayit.databinding.RecyclerviewWordsItemBinding

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
        /*val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_words_item, parent, false)
        return ItemAdapter(view)*/

        val bind:RecyclerviewWordsItemBinding=RecyclerviewWordsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
   return ItemAdapter(bind)
    }

    override fun onBindViewHolder(holder: ItemAdapter, position: Int) {
        val playerWord = playerMapWord[position]

        holder.bind(playerWord,position)


     /*   playerWord?.let {
            holder.tvUsername.text = playerWord[0]//username
            holder.tvWord.text = playerWord[1]//word
            holder.tvWord.setTextColor(Color.BLACK)
            Log.e("$playerName  onBindViewHolder playerMapWord", "$playerMapWord $position")
            Log.e("$playerName  onBindViewHolder playerMapState", "$playerMapState $position")
            if (playerMapState.size > 0 && position < playerMapState.size) {
                holder.tvState.text = playerMapState[position]//state
                Log.e("$playerName Game adapter",playerPic.toString())
                holder.imgPlayerAnimation.load(playerPic[position])//pic

            }
            *//* if (playerName == playerWord[0]) {
                 holder.itemView.visibility = View.GONE
             }*//*

            holder.itemView.setOnClickListener {//change state
                //should change color too
                changePlayerState(playerWord[0], playerMapState[position], position, holder)

                if ( holder.redx.visibility==0){//visiable
                    holder.redx.visibility=View.INVISIBLE
                }else{
                    holder.redx.visibility=View.VISIBLE
                }


            }
        }*/
    }

    private fun changePlayerState(
        playerName: String,
        currentState: String,
        position: Int,
        holder: RecyclerviewWordsItemBinding
    ) {
        //listOfInPlayers.size>1&&
        if (!playerMapState.contains("winner")) {//this doesn't work if i'm the winner
            if (currentState == "in") {
                stateRef.child(playerName).setValue("out")
             //   holder.redx.visibility=View.VISIBLE
               holder.tvState.text = "out"//maybe i don't need to change it here but listener will make it change
                 // holder.itemView.setBackgroundColor(Color.GRAY)
            }
            if (currentState == "out") {//in case winner or out,  maybe make winner unchangeable
                stateRef.child(playerName).setValue("in")
           //     holder.redx.visibility=View.INVISIBLE

                holder.tvState.text = "in"
               // holder.itemView.setBackgroundColor(context.resources.getColor(R.color.white))

            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return playerMapWord.size
    }

    inner class ItemAdapter(val itemBinding: RecyclerviewWordsItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(playerWord: MutableList<String>?, position: Int) {
            playerWord?.let {
                itemBinding.tvUsername.text = playerWord[0]//username
                itemBinding.tvWord.text = playerWord[1]//word
                itemBinding.tvWord.setTextColor(Color.BLACK)
                Log.e("$playerName  onBindViewHolder playerMapWord", "$playerMapWord $position")
                Log.e("$playerName  onBindViewHolder playerMapState", "$playerMapState $position")
                if (playerMapState.size > 0 && position < playerMapState.size) {
                    itemBinding.tvState.text = playerMapState[position]//state
                    if (playerMapState[position]=="out"){
                        itemBinding.redx.visibility=View.VISIBLE

                    }else{
                        itemBinding.redx.visibility=View.INVISIBLE

                    }


                    Log.e("$playerName Game adapter",playerPic.toString())
                    itemBinding.imgPlayerAnimation.load(playerPic[position])//pic

                }
                /* if (playerName == playerWord[0]) {
                     holder.itemView.visibility = View.GONE
                 }*/
itemBinding.imgPlayerAnimation.setOnClickListener {

               // holder.itemView.setOnClickListener {//change state
                    //should change color too
                    changePlayerState(playerWord[0], playerMapState[position], position, itemBinding)

                    if ( itemBinding.redx.visibility==0){//visiable
                        itemBinding.redx.visibility=View.INVISIBLE
                    }else{
                        itemBinding.redx.visibility=View.VISIBLE
                    }


                }
            }
        }
        /* val tvWord: TextView = itemBinding.findViewById(R.id.tvWord)
         val imgPlayerAnimation: ImageView = itemBinding.findViewById(R.id.imgPlayerAnimation)
         val tvUsername: TextView = itemBinding.findViewById(R.id.tvUsername)
         val tvState: TextView = itemBinding.findViewById(R.id.tvState)
         val redx: ImageView = itemBinding.findViewById(R.id.redx)*/

    }
}