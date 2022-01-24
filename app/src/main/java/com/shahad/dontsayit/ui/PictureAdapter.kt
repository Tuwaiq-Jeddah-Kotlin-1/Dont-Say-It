package com.shahad.dontsayit.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.databinding.RecyclerviewProfilepictureItemBinding

class PictureAdapter(
    private val profilePictureList: Array<ProfilePicture>,
    private val mListener: ItemListener?
) :
    RecyclerView.Adapter<PictureAdapter.ItemAdapter>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter {
        val binding = RecyclerviewProfilepictureItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemAdapter(binding)
    }

    override fun onBindViewHolder(holder: ItemAdapter, position: Int) {
        val picture = profilePictureList[position]
        holder.imgPicture.load(picture.picture)

        holder.onBind(picture.picture)

    }

    override fun getItemCount(): Int {
        return profilePictureList.size
    }

    interface ItemListener {
        fun onItemClick(item: String?)
    }

    inner class ItemAdapter(binding: RecyclerviewProfilepictureItemBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        val imgPicture = binding.imgPic
        var item: String? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun onBind(pic: String) {
            item = pic
        }

        override fun onClick(v: View) {
            mListener?.onItemClick(item)
        }
    }
}