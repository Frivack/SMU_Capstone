package com.capstone.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.capstone.ApiHelper
import com.example.capstone.Review

class ReviewPage : AppCompatActivity() {
    private val apiHelper = ApiHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)


        val userId = intent.getIntExtra("USER_ID", 0)
        val userName = intent.getStringExtra("USER_NAME")

        userName?.let {
            val idView = findViewById<TextView>(R.id.user_id)
            idView.text = it
        }

        val recyclerView = findViewById<RecyclerView>(R.id.review_list)
        val writeButton = findViewById<TextView>(R.id.to_write)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // 리뷰를 REST API로 불러오기
        CoroutineScope(Dispatchers.IO).launch {
            val reviews = apiHelper.fetchAllReviews()
            withContext(Dispatchers.Main) {
                if (reviews.isNotEmpty()) {
                    val adapter = ReviewAdapter(reviews) { review ->
                        val intent = Intent(this@ReviewPage, ReviewViewPage::class.java)
                        intent.putExtra("REVIEW_ID", review.review_id)
                        startActivity(intent)
                    }
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(this@ReviewPage, "리뷰가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        writeButton.setOnClickListener {
            val intent = Intent(this, WritePage::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        setupSwichButtons(userId)
    }

    private fun setupSwichButtons(userId: Int) {
        val write = findViewById<TextView>(R.id.to_write)
        write.setOnClickListener {
            val intent = Intent(this, WritePage::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
    }
}
