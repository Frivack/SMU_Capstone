package com.capstone.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val start = findViewById<ImageView>(R.id.start_main_page)

        start.setOnClickListener {
            val intent = Intent(this, MainPage::class.java)
            startActivity(intent)
        }
    }
}
