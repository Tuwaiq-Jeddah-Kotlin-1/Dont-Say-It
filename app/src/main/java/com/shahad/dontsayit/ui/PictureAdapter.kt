package com.shahad.dontsayit.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.model.ProfilePicture

class PictureAdapter(
    private val profilePictureList: Array<ProfilePicture>,
    private val mListener: ItemListener?
) :
    RecyclerView.Adapter<PictureAdapter.ItemAdapter>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_profilepicture_item, parent, false)
        return ItemAdapter(view)
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

    inner class ItemAdapter(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imgPicture: ImageView = itemView.findViewById(R.id.imgPic)
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