package com.shahad.dontsayit.ui.game

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.shahad.dontsayit.data.model.Player
import com.shahad.dontsayit.databinding.RecyclerviewWordsItemBinding

class GameAdapter(
    private val playerObjList: Map<String, Player>,
    private val mListener: ItemListener?
) : RecyclerView.Adapter<GameAdapter.ItemAdapter>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter {

        val bind: RecyclerviewWordsItemBinding =
            RecyclerviewWordsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemAdapter(bind)
    }

    override fun onBindViewHolder(holder: ItemAdapter, position: Int) {

        val player = playerObjList[playerObjList.keys.elementAt(position)]
        holder.bind(player!!)

    }


    override fun getItemCount(): Int {
        return playerObjList.size
    }

    interface ItemListener {
        fun onItemClick(item: Player?)
    }

    inner class ItemAdapter(val itemBinding: RecyclerviewWordsItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var item: Player? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(player: Player?) {
            player?.let {
                item = player
                itemBinding.tvUsername.text = player.name//username
                itemBinding.tvWord.text = player.word//word
                itemBinding.tvWord.setTextColor(Color.BLACK)
                itemBinding.imgPlayer.load(player.pic)//pic
                if (player.state == "out") {
                    itemBinding.redx.visibility = View.VISIBLE

                } else {
                    itemBinding.redx.visibility = View.INVISIBLE

                }


            }
        }

        override fun onClick(v: View) {
            mListener?.onItemClick(item)

        }
    }
}