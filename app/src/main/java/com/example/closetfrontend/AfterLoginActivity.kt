package com.example.closetfrontend

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AfterLoginActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var myClosetFragment: Fragment
    private lateinit var lookBookFragment: Fragment
    private lateinit var myPageFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_login)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        myClosetFragment = MyClosetFragment()
        lookBookFragment = LookBookFragment()
        myPageFragment = MyPageFragment()

        supportFragmentManager.beginTransaction().replace(R.id.container, myClosetFragment).commit()


        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.addTab(tabs.newTab().setText("My Closet"))
        tabs.addTab(tabs.newTab().setText("LookBook"))
        tabs.addTab(tabs.newTab().setText("My Page"))

        tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position: Int? = tab?.position
                Log.d("MainActivity", "선택된 탭: " + position.toString())

                var selected: Fragment? = null
                if (position == 0) {
                    selected = myClosetFragment
                } else if (position == 1) {
                    selected = lookBookFragment
                } else {
                    selected = myPageFragment
                }
                supportFragmentManager.beginTransaction().replace(R.id.container, selected).commit()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }

}