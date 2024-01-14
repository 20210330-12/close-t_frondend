package com.example.closetfrontend

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.closetfrontend.LookBookAdapter.LookBookViewHolder

class LookBookAdapter(
    private val context: Context,
    private val codiIds: List<String>,
    private val likes: List<String>,
    private val clothesImageUrls: List<List<String>>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<LookBookViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LookBookViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.lookbook_list, parent, false)
        return LookBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: LookBookViewHolder, position: Int) {
        val codiId = codiIds[position]
        val like = likes[position]
        val clothesImages = clothesImageUrls[position]
        if (clothesImages.size >= 6) {
            holder.lookbookTop.setImageBitmap(displayProcessedImage(clothesImages[0]))
            holder.lookbookBottom.setImageBitmap(displayProcessedImage(clothesImages[1]))
            holder.lookbookOuter.setImageBitmap(displayProcessedImage(clothesImages[2]))
            holder.lookbookOnepiece.setImageBitmap(displayProcessedImage(clothesImages[3]))
            holder.lookbookShoes.setImageBitmap(displayProcessedImage(clothesImages[4]))
            holder.lookbookBag.setImageBitmap(displayProcessedImage(clothesImages[5]))
        }
        holder.itemView.setOnClickListener { view: View? -> itemClickListener.onItemClick(position) }
        if ("like" == like) {
            holder.emptyHeart.visibility = View.GONE
            holder.filledHeart.visibility = View.VISIBLE
        } else {
            holder.emptyHeart.visibility = View.VISIBLE
            holder.filledHeart.visibility = View.GONE
        }
    }

    private fun displayProcessedImage(base64Image: String): Bitmap {
        val decodedBytes =
            Base64.decode(base64Image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    override fun getItemCount(): Int {
        return codiIds.size
    }

    inner class LookBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageIV: ImageView
        val emptyHeart: ImageView
        val filledHeart: ImageView
        val lookbookTop: ImageView
        val lookbookBottom: ImageView
        val lookbookOuter: ImageView
        val lookbookOnepiece: ImageView
        val lookbookShoes: ImageView
        val lookbookBag: ImageView

        init {
            imageIV = itemView.findViewById(R.id.idIVImage)
            emptyHeart = itemView.findViewById(R.id.idEmptyHeart)
            filledHeart = itemView.findViewById(R.id.idHeartFilled)
            lookbookTop = itemView.findViewById(R.id.lookbookTop)
            lookbookBottom = itemView.findViewById(R.id.lookbookBottom)
            lookbookOuter = itemView.findViewById(R.id.lookbookOuter)
            lookbookOnepiece = itemView.findViewById(R.id.lookbookOnepiece)
            lookbookShoes = itemView.findViewById(R.id.lookbookShoes)
            lookbookBag = itemView.findViewById(R.id.lookbookBag)
        }
    }
}