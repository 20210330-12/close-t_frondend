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
import androidx.constraintlayout.widget.ConstraintLayout
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

    private lateinit var updatedLike: Array<String>

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

        val sharedPreferences = holder.itemView.context.getSharedPreferences("userId", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")!!

        updatedLike = likes.toTypedArray()
        if (updatedLike[position] == "like") {
            holder.emptyHeart.setImageResource(R.drawable.heart_filled)
        } else {
            holder.emptyHeart.setImageResource(R.drawable.empty_heart)
        }
        Log.e("updatedLIke", updatedLike.toString())
        holder.emptyHeart.setOnClickListener {
            likeCodi(userId, codiId, position)
            Log.e("like", like)
            if (updatedLike[position] == "none") {
                // false였던 걸 누른거니까 true가 되고, 하트는 칠해져야.
                holder.emptyHeart.setImageResource(R.drawable.full_heart)
//                holder.filledHeart.visibility = View.VISIBLE
            } else {
                // true였던 걸 누른거니까 false가 되고, 하트는 없어져야.
                holder.emptyHeart.setImageResource(R.drawable.empty_heart)
//                holder.filledHeart.visibility = View.INVISIBLE
            }
        }
    }

    private fun likeCodi(userId: String, codiId: String, position: Int) {
        RetrofitInterface.create().likeCodi(userId, codiId).enqueue(object :
            Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val updatedStatus = response.body()?.getAsJsonPrimitive("result").toString().subSequence(30, 34)
                    Log.e("likeCodi", updatedStatus.toString())
                    updatedLike[position] = updatedStatus.toString()
                    //notifyDataSetChanged()
                } else {
                    Log.e("likeCodi", "failed to like codi")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("likeCodi", "failed")
            }
        })
    }


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
            itemView.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)
            }
            val constraintLayout = itemView.findViewById<ConstraintLayout>(R.id.idIVImage)
            lookbookTop = constraintLayout.findViewById(R.id.lookbookTop)
            lookbookBottom = constraintLayout.findViewById(R.id.lookbookBottom)
            lookbookOuter = constraintLayout.findViewById(R.id.lookbookOuter)
            lookbookOnepiece = constraintLayout.findViewById(R.id.lookbookOnepiece)
            lookbookShoes = constraintLayout.findViewById(R.id.lookbookShoes)
            lookbookBag = constraintLayout.findViewById(R.id.lookbookBag)
        }

//        fun bind(item: String) {
//            if (item == "like") {
//                emptyHeart.setImageResource(R.drawable.full_heart)
//            } else {
//                emptyHeart.setImageResource(R.drawable.empty_heart)
//            }
//        }
    }
}