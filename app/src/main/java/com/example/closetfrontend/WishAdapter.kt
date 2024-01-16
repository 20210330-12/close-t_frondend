package com.example.closetfrontend

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishAdapter (private var clothesList: ArrayList<Clothes>) :
        RecyclerView.Adapter<WishAdapter.clothesViewHolder>() {

    // 서버에서 불러오기
    val api = RetrofitInterface.create()
    // userId 불러오기
    private lateinit var userId: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishAdapter.clothesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_closet_recyclerview, parent, false)
        return clothesViewHolder(view)
    }

    //@SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: WishAdapter.clothesViewHolder, position: Int) {
        holder.bind(clothesList[position])
        val sharedPref = holder.itemView.context.getSharedPreferences("userId", Context.MODE_PRIVATE)
        userId = sharedPref.getString("userId", "")!!


        // 하트 버튼 누르면 like tag 추가 / 삭제
        holder.myClosetLikeBtn.setOnClickListener {
//            val currentTopLikeImage = holder.myClosetLikeBtn.tag as? Int ?: R.drawable.empty_heart
//            val newTopLikeImage = if (currentTopLikeImage == R.drawable.empty_heart) {
//                R.drawable.full_heart
//            } else { R.drawable.empty_heart }
//
//            Log.e("ClothesAdapter", "current: $currentTopLikeImage")
//            Log.e("ClothesAdapter", "new: $newTopLikeImage")
//            Log.e("ClothesAdapter", "empty_heart: ${R.drawable.empty_heart}")
//            Log.e("ClothesAdapter", "full_heart: ${R.drawable.full_heart}")
//            holder.myClosetLikeBtn.setImageResource(newTopLikeImage)
//            holder.myClosetLikeBtn.tag = newTopLikeImage

            if (clothesList[position].like.contains("Like")) {
                // 이때는 이제 none이 되는거니까
                clothesList[position].like = "None"
                holder.myClosetLikeBtn.setImageResource(R.drawable.empty_heart)
            } else {
                // false였던걸 누른 거니까 true가 되고, 하트는 칠해져야 하는거지.
                clothesList[position].like = "Like"
                holder.myClosetLikeBtn.setImageResource(R.drawable.full_heart)
            }
            notifyItemChanged(position)

            // 서버에 like 여부 저장
            // 이건 그냥 보내만 두면 -> like 되어있으면 해제해주고, 안 되어있으면 like해줌
            api.changeLike(userId, clothesList[position].id).enqueue(object:
                Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e(ContentValues.TAG, "result: $result")
                    } else {
                        // HTTP 요청이 실패한 경우의 처리
                        Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
                }
            })

//            notifyDataSetChanged()
        }
        
        
        
        // 길게 누르면 완전히 삭제
        holder.myClosetPic.setOnLongClickListener {
            
            // 완전히 삭제된다는 이미지를 0.5초간 보여주기
            holder.myClosetPic.setImageResource(R.drawable.remove_real)
            
            // 완전히 삭제하기
            api.deleteCloth(userId, clothesList[position].id).enqueue(object:
                Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e(ContentValues.TAG, "result: $result")
                    } else {
                        // HTTP 요청이 실패한 경우의 처리
                        Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
                }
            })
            
            // 0.5초 뒤에 list에서 완전히 제거함
            Handler(Looper.getMainLooper()).postDelayed({
                removeMyClosetItem(position)
                notifyDataSetChanged()
                true
            }, 500)
        }



        // 그리고, 그냥 짧게 누르면 wish 테그 삭제 (샀다는 의미)
        holder.myClosetPic.setOnClickListener {
            // trash 없어진다는 이미지를 0.5초간 보여주기
            holder.myClosetPic.setImageResource(R.drawable.buy_wish)

            // wish tag 없애기
            api.removeWish(userId, clothesList[position].id).enqueue(object:
                Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e(ContentValues.TAG, "result: $result")
                    } else {
                        // HTTP 요청이 실패한 경우의 처리
                        Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
                }
            })

            // 0.5초 뒤에 wish list에서 완전히 제거함
            Handler(Looper.getMainLooper()).postDelayed({
                removeMyClosetItem(position)
                notifyDataSetChanged()
                true
            }, 500)
        }
    }

    private fun displayProcessedImage(imageView: ImageView, base64Image: String) {
        val decodedBytes: ByteArray = Base64.decode(base64Image, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        imageView.setImageBitmap(decodedBitmap)
    }

    override fun getItemCount(): Int {
        return clothesList.size
    }

    private fun removeMyClosetItem(position: Int) {
        clothesList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    inner class clothesViewHolder(myClosetItemView: View) : RecyclerView.ViewHolder(myClosetItemView) {
        val myClosetLikeBtn = myClosetItemView.findViewById<ImageButton>(R.id.myClosetlikeBtn)
        val myClosetPic = myClosetItemView.findViewById<ImageView>(R.id.myClosetPic)

        fun bind(item: Clothes) {
            
            // tag에 like가 있으면 좋아요 눌러놓기
            if(item.like.contains("Like")) {
                myClosetLikeBtn.setImageResource(R.drawable.full_heart)
            } else { myClosetLikeBtn.setImageResource(R.drawable.empty_heart) }


            // 이미지 URL이 있는지 확인하고 Picasso를 사용하여 ImageView에 로드합니다.
            val imageUrl = item.imageUrl // 실제 이미지 URL을 저장하는 myClosetItem 클래스의 필드로 교체하세요.
            //Log.e("imageURL", "$imageUrl")
            Picasso.get().load("http://172.10.7.44:80/images/${imageUrl}").into(myClosetPic)
        }

    }
}