package com.capstone.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setContentView(R.layout.activity_start) // ✅ 먼저 레이아웃 붙이고

        val start = findViewById<ImageView>(R.id.start_main_page)
        Log.d("MainActivity", "뷰 찾기 성공했나? $start")

        start.setOnClickListener {
            Log.d("MainActivity", "클릭됨")
            val intent = Intent(this, LoginPage::class.java)
            //val intent = Intent(this, MainPage::class.java)
            startActivity(intent)
        }
    }
}
