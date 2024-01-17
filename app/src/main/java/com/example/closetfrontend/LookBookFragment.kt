package com.example.closetfrontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetfrontend.RetrofitInterface.Companion.create
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LookBookFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var lookBookAdapter: LookBookAdapter
    private lateinit var codiIds: ArrayList<String>
    private lateinit var likes: ArrayList<String>
    private lateinit var clothesImageUrls: ArrayList<List<String?>>
    private lateinit var heartButton: ImageButton
    private var emptyHeart: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_look_book, container, false)
        recyclerView = view.findViewById(R.id.idLookBooks)
        codiIds = ArrayList()
        likes = ArrayList()
        clothesImageUrls = ArrayList()

        lookBookAdapter = LookBookAdapter(
            requireContext(),
            codiIds,
            likes,
            clothesImageUrls,
            object : LookBookAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    Log.d("LookBookAdapter", "item is being clicked")
                    onCodiItemClick(position)
                }
            })

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = lookBookAdapter

        heartButton = view.findViewById(R.id.idHeartButton)
        heartButton.setOnClickListener {
            if (emptyHeart) {
                heartButton.setBackgroundResource(R.drawable.heart_filled)
                codiIds.clear()
                getLikedCodies()
                emptyHeart = !emptyHeart
            } else {
                heartButton.setBackgroundResource(R.drawable.empty_heart)
                codiIds.clear()
                getAllCodies()
                emptyHeart = !emptyHeart
            }
        }

        getAllCodies()
        return view
    }

    private fun getAllCodies() {
        val sharedPreferences =
            requireContext().getSharedPreferences("userId", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")

        val retrofitInterface = RetrofitInterface.create()
        retrofitInterface.getAllCodies(userId!!).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    parseResponse(response.body())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to get codi information",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun parseResponse(response: JsonObject?) {
        val codiIdsArray = response?.getAsJsonArray("codiIds")
        val likesArray = response?.getAsJsonArray("likes")
        val clothesImageUrlsArray = response?.getAsJsonArray("clothesImageUrls")

        codiIdsArray?.let {
            for (i in 0 until it.size()) {
                codiIds.add(it[i].asString)
                likes.add(likesArray?.get(i)?.asString ?: "")

                val clothesImages = ArrayList<String?>()
                val clothesImagesArray = clothesImageUrlsArray?.get(i)

                if (clothesImagesArray != null && clothesImagesArray.isJsonArray) {
                    val jsonArray = clothesImagesArray.asJsonArray
                    for (j in 0 until jsonArray.size()) {
                        val imageUrl = jsonArray[j].takeIf { !it.isJsonNull }?.asString
                        clothesImages.add(imageUrl)
                    }
                }
//                clothesImagesArray?.let {
//                    for (j in 0 until it.size()) {
//                        val imageUrl = it[j]?.asString
//                        clothesImages.add(imageUrl)
//                    }
//                }
                clothesImageUrls.add(clothesImages)
            }
        }

        lookBookAdapter.notifyDataSetChanged()
    }

    private fun getLikedCodies() {
        val sharedPreferences = requireContext().getSharedPreferences("userId", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")!!

        val retrofitInterface = RetrofitInterface.create()
        retrofitInterface.getLikedCodies(userId).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    parseResponse(response.body())
                } else {
                    Toast.makeText(requireContext(), "Failed to get liked codies", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onCodiItemClick(position: Int) {
        Log.d("LookBookFragment", "Item clicked at position $position")

        val selectedCodiId = codiIds[position]
        val intent = Intent(requireContext(), LookBookDetailViewActivity::class.java)
        intent.putExtra("codiId", selectedCodiId)
        startActivity(intent)
    }
}