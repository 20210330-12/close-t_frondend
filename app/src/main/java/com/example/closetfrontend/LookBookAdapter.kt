package com.example.closetfrontend

import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class LookBookAdapter(private val context: Context, private val dataSet: List<MyLookBookDataModel>, private val itemClickLIstener: (Int) -> Unit):
    RecyclerView.Adapter<LookBookAdapter.LookBookViewHolder> () {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LookBookAdapter.LookBookViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.lookbook_list, parent, false)
        return LookBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: LookBookAdapter.LookBookViewHolder, position: Int) {
        val currentItem = dataSet[position]

        Picasso.get().load(currentItem.clothesImages).into(holder.imageIV)
        //여러 clothesImages을 다 한꺼번에 어떻게 보여줄지 상의!!!

        holder.itemView.setOnClickListener {
            itemClickLIstener.invoke(position)
        }

        if (currentItem.like == "like") {
            holder.emptyHeart.visibility = View.GONE
            holder.filledHeart.visibility = View.VISIBLE
        } else {
            holder.emptyHeart.visibility = View.VISIBLE
            holder.filledHeart.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class LookBookViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageIV: ImageView = itemView.findViewById(R.id.idIVImage)
        val emptyHeart: ImageView = itemView.findViewById(R.id.idEmptyHeart)
        val filledHeart: ImageView = itemView.findViewById(R.id.idHeartFilled)
    }

}