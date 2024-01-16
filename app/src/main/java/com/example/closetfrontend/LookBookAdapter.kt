package com.example.closetfrontend

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.closetfrontend.LookBookAdapter.LookBookViewHolder
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LookBookAdapter(
    private val context: Context,
    private val codiIds: List<String>,
    private val likes: List<String>,
    private val clothesImageUrls: List<List<String?>>,
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
            Picasso.get().load("http://172.10.7.44:80/images/${clothesImages[0]}").into(holder.lookbookTop)
            Picasso.get().load("http://172.10.7.44:80/images/${clothesImages[1]}").into(holder.lookbookBottom)
            Picasso.get().load("http://172.10.7.44:80/images/${clothesImages[2]}").into(holder.lookbookOuter)
            Picasso.get().load("http://172.10.7.44:80/images/${clothesImages[3]}").into(holder.lookbookOnepiece)
            Picasso.get().load("http://172.10.7.44:80/images/${clothesImages[4]}").into(holder.lookbookShoes)
            Picasso.get().load("http://172.10.7.44:80/images/${clothesImages[5]}").into(holder.lookbookBag)
//
        }
        holder.itemView.setOnClickListener { view: View? -> itemClickListener.onItemClick(position) }
        if ("like" == like) {
            holder.emptyHeart.setImageResource(R.drawable.full_heart)
//            holder.emptyHeart.visibility = View.INVISIBLE
//            holder.filledHeart.visibility = View.VISIBLE
        } else {
            holder.emptyHeart.setImageResource(R.drawable.empty_heart)
//            holder.emptyHeart.visibility = View.VISIBLE
//            holder.filledHeart.visibility = View.INVISIBLE
        }

        val sharedPreferences = holder.itemView.context.getSharedPreferences("userId", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")!!
//        holder.emptyHeart.setOnClickListener {
//            likeCodi(userId, codiId)
//            if (likes[position] == "none") {
//                holder.emptyHeart.visibility = View.GONE
//                holder.filledHeart.visibility = View.VISIBLE
//            } else {
//                holder.emptyHeart.visibility = View.VISIBLE
//                holder.filledHeart.visibility = View.GONE
//            }
//        }

        holder.emptyHeart.setOnClickListener {
            if (likes[position] == "none") {
                // false였던 걸 누른거니까 true가 되고, 하트는 칠해져야.
                holder.emptyHeart.setImageResource(R.drawable.heart_filled)
//                holder.filledHeart.visibility = View.VISIBLE
            } else {
                // true였던 걸 누른거니까 false가 되고, 하트는 없어져야.
                holder.emptyHeart.setImageResource(R.drawable.heart_icon)
//                holder.filledHeart.visibility = View.INVISIBLE
            }
            notifyItemChanged(position)
            likeCodi(userId, codiId)
        }
    }

    private fun likeCodi(userId: String, codiId: String) {
        RetrofitInterface.create().likeCodi(userId, codiId).enqueue(object :
            Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    Log.e("likeCodi", response.body().toString())
                } else {
                    Log.e("likeCodi", "failed to like codi")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("likeCodi", "failed")
            }
        })
    }

    /*
    private fun displayProcessedImage(base64Image: String): Bitmap {
        val decodedBytes =
            Base64.decode(base64Image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

     */

    override fun getItemCount(): Int {
        return codiIds.size
    }

    inner class LookBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val imageIV: ImageView
        val emptyHeart: ImageButton
//        val filledHeart: ImageView
        val lookbookTop: ImageView
        val lookbookBottom: ImageView
        val lookbookOuter: ImageView
        val lookbookOnepiece: ImageView
        val lookbookShoes: ImageView
        val lookbookBag: ImageView

        init {
            //imageIV = itemView.findViewById(R.id.idIVImage)
            emptyHeart = itemView.findViewById(R.id.idEmptyHeart)
//            filledHeart = itemView.findViewById(R.id.idHeartFilled)
            lookbookTop = itemView.findViewById(R.id.lookbookTop)
            lookbookBottom = itemView.findViewById(R.id.lookbookBottom)
            lookbookOuter = itemView.findViewById(R.id.lookbookOuter)
            lookbookOnepiece = itemView.findViewById(R.id.lookbookOnepiece)
            lookbookShoes = itemView.findViewById(R.id.lookbookShoes)
            lookbookBag = itemView.findViewById(R.id.lookbookBag)
        }

        fun bind(item: String) {
            if (item == "like") {
                emptyHeart.setImageResource(R.drawable.heart_filled)
            } else {
                emptyHeart.setImageResource(R.drawable.heart_icon)
            }
        }
    }
}