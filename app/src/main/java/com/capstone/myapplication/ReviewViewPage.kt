package com.capstone.myapplication

import android.os.Bundle
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.ApiHelper
import com.example.capstone.Review
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReviewViewPage : AppCompatActivity() {
    private val apiHelper = ApiHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_view)

        val reviewId = intent.getIntExtra("REVIEW_ID", -1)

        CoroutineScope(Dispatchers.IO).launch {
            val reviews = apiHelper.fetchAllReviews()
            val review = reviews.find { it.review_id == reviewId }
            withContext(Dispatchers.Main) {
                if (review != null) {
                    setReviewInfo(review)
                } else {
                    Toast.makeText(this@ReviewViewPage, "리뷰 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 추천 버튼 클릭 이벤트
        val thumbsUpButton = findViewById<RelativeLayout>(R.id.thumbs_up_layout)
        thumbsUpButton.setOnClickListener {
            // 추천 기능도 REST API로 변경 필요(추후 구현)
            Toast.makeText(this, "추천 API 따로 구현 필요!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setReviewInfo(review: Review) {
        val titleTextView = findViewById<TextView>(R.id.review_title)
        val contentTextView = findViewById<TextView>(R.id.review_content)
        val createDateView = findViewById<TextView>(R.id.date)
        val userIdView = findViewById<TextView>(R.id.ID)

        titleTextView.text = review.title
        contentTextView.text = review.content
        createDateView.text = review.created_at
        userIdView.text = review.user_id.toString()

        if (review.review_id != 2) {
            val scroll = findViewById<HorizontalScrollView>(R.id.scroll)
            scroll.visibility = HorizontalScrollView.GONE
        }
    }
}
