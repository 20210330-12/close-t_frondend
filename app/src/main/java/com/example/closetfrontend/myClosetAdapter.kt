package com.example.closetfrontend

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class myClosetAdapter (private var myClosetItemList: ArrayList<myClosetItem>) :
        RecyclerView.Adapter<myClosetAdapter.myClosetViewHolder>() {

    // 서버에서 불러오기
    val api = RetrofitInterface.create()
    // userId 불러오기
    private lateinit var userId: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myClosetAdapter.myClosetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_closet_recyclerview, parent, false)
        return myClosetViewHolder(view)
    }

    override fun onBindViewHolder(holder: myClosetAdapter.myClosetViewHolder, position: Int) {
        holder.bind(myClosetItemList[position])
        val sharedPref = holder.itemView.context.getSharedPreferences("userId", Context.MODE_PRIVATE)
        userId = sharedPref.getString("userId", "")!!

        // 하트 버튼 누르면 like tag 추가 / 삭제
        holder.myClosetLikeBtn.setOnClickListener {
            // 만약 like tag가 없었다면 api로 like tag 추가하고 full_heart로 바꾸기
            holder.myClosetLikeBtn.setImageResource(R.drawable.full_heart)
            // 만약 like tag가 있었다면 api로 like tag 없애고 empty_heart로 바꾸기
            //holder.myClosetLikeBtn.setImageResource(R.drawable.empty_heart)
        }

        // 상세정보 뜨는건 할 필요가 없지 않을까

    }

    override fun getItemCount(): Int {
        return myClosetItemList.size
    }

    private fun removeMyClosetItem(position: Int) {
        myClosetItemList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    inner class myClosetViewHolder(myClosetItemView: View) : RecyclerView.ViewHolder(myClosetItemView) {
        val myClosetLikeBtn = myClosetItemView.findViewById<ImageButton>(R.id.myClosetlikeBtn)
        val myClosetPic = myClosetItemView.findViewById<ImageView>(R.id.myClosetPic)

        fun bind(item: myClosetItem) {
            // if(like tag가 있으면) { myClosetLikeBtn.setImageResource(R.drawable.full_heart }
            // else { myClosetLikeBtn.setImageResource(R.drawable.empty_heart }
            // 이미지 URL이 있는지 확인하고 Picasso를 사용하여 ImageView에 로드합니다.
            val imageUrl = item.imageUrl // 실제 이미지 URL을 저장하는 myClosetItem 클래스의 필드로 교체하세요.
            Log.e("imageURL", "$imageUrl")
            Picasso.get().load(imageUrl)
                .placeholder(R.drawable.full_heart)
                .error(R.drawable.empty_heart) // 에러 발생 시 로딩될 이미지
                .into(myClosetPic)
        }

    }
}