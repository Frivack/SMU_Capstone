package com.capstone.myapplication
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AccountPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val userId = intent.getStringExtra("USER_EMAIL") ?: "Unknown User"

        // 계정 페이지의 ID 표시
        val accountIdTextView = findViewById<TextView>(R.id.user_id)
        accountIdTextView.text = userId
    }
}