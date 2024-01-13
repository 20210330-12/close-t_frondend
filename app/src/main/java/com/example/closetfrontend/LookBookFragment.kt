package com.example.closetfrontend

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LookBookFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LookBookFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var recyclerView: RecyclerView
    private lateinit var lookBookAdapter: LookBookAdapter
    private lateinit var userId: String

    val api = RetrofitInterface.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_look_book, container, false)

        recyclerView = view.findViewById(R.id.idLookBooks)
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        val sharedPref = requireContext().getSharedPreferences("userId", Context.MODE_PRIVATE)
        userId = sharedPref.getString("userId", "")!!

        fetchAllCodies(userId)

        val lookBookDataList: List<MyLookBookDataModel>

        lookBookAdapter = LookBookAdapter(lookBookDataList)
        recyclerView.adapter = lookBookAdapter

        return view
    }

    private fun fetchAllCodies(userId: String) {
        api.getAllCodies(userId).enqueue(object: Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val codiData = response.body()
                    Log.e(ContentValues.TAG, "result: $codiData")
                    val lookBookDataList: List<MyLookBookDataModel> = parseCodiData(codiData)
                    lookBookAdapter = LookBookAdapter(requireContext(), lookBookDataList) { position ->
                        showDetailViewPopup(lookBookDataList[position].codiId)
                    }
                    recyclerView.adapter = lookBookAdapter
                } else {
                    // HTTP 요청이 실패한 경우의 처리
                    Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
            }
        })
    }

    private fun parseCodiData(codiData: JsonObject?): List<MyLookBookDataModel> {
        val lookBookDataList = mutableListOf<MyLookBookDataModel>()

        codiData?.getAsJsonArray("codies")?.forEach { codi ->
            val codiObject = codi.asJsonObject
            val codiId = codiObject.get("codiId").asString
            val styles = parseJsonArray(codiObject.getAsJsonArray("styles"))
            val clothesImages = parseJsonArray(codiObject.getAsJsonArray("clothesImages"))
            val comment = codiObject.get("comment").asString
            val like = codiObject.get("like").asBoolean

            val lookBookDataModel = MyLookBookDataModel(codiId, styles, clothesImages, comment, like)
            lookBookDataList.add(lookBookDataModel)
        }

        return lookBookDataList
    }

    private fun parseJsonArray(jsonArray: JsonArray?): List<String> {
        val resultList = mutableListOf<String>()
        jsonArray?.forEach { element ->
            resultList.add(element.asString)
        }
        return resultList
    }

    public fun showDetailViewPopup(codiId: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_look_book_detail_view)

        val imageView = dialog.findViewById<ImageView>(R.id.idDetailImage)
        val heartIcon = dialog.findViewById<ImageView>(R.id.idHeartIcon)
        val commentText = dialog.findViewById<TextView>(R.id.idCommentText)
        val hashtagsContainer = dialog.findViewById<LinearLayout>(R.id.idHashTagContainer)

        getSelectedCodi(codiId)

        dialog.show()
    }

    private fun getSelectedCodi(codiId: String) {
        api.getSelectedCodi(userId, codiId).enqueue(object: Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val selectedCodiData = response.body()
                    Log.e(ContentValues.TAG, "result: $selectedCodiData")
                    //파싱을 통해 얻은 데이터 처리 해야함!!!
                } else {
                    // HTTP 요청이 실패한 경우의 처리
                    Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
            }
        })
    }
}