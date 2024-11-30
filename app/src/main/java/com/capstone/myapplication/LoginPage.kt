package com.capstone.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)

        setupSwichButtons()
    }
    
    private fun setupSwichButtons()
    {
        val summit = findViewById<TextView>(R.id.summit)

        summit.setOnClickListener {
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, MainPage::class.java)
            startActivity(intent)
        }
    }
}