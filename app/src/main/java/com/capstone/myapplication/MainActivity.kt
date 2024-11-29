package com.capstone.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // 뷰 찾기
        val page = findViewById<ImageView>(R.id.start_main_page)

        // 클릭 리스너 설정
        page.setOnClickListener {
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, MainPage::class.java)
            startActivity(intent)
        }
    }
}
