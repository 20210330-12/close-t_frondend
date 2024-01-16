package com.example.closetfrontend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.closetfrontend.GalleryAdapter.ImagesViewHolder
import com.squareup.picasso.Picasso
import java.io.File

class GalleryAdapter(
    private val context: Context,
    private val imagePathArrayList: ArrayList<String>
) : RecyclerView.Adapter<ImagesViewHolder>() {
    private var listener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.image_list, parent, false)
        return ImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val imageFile = File(imagePathArrayList[holder.adapterPosition])
        if (imageFile.exists()) {
            Picasso.get().load(imageFile).placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageIV)
            holder.itemView.setOnClickListener {
                if (listener != null) {
                    listener!!.onItemClick(position)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return imagePathArrayList.size
    }

    inner class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageIV: ImageView

        init {
            imageIV = itemView.findViewById(R.id.idIVImage)
        }
    }
}