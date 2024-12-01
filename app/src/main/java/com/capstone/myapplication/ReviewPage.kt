package com.capstone.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.myapplication.BuildPage.Companion.REQUEST_CODE_BUDGET
import com.example.capstone.DatabaseHelper

class ReviewPage : AppCompatActivity() {

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

        // 설정: RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val reviews = fetchReviewsFromDatabase()
        if (reviews.isNotEmpty()) {
            val adapter = ReviewAdapter(reviews) { review ->
                val intent = Intent(this, ReviewViewPage::class.java)
                intent.putExtra("REVIEW_ID", review.reviewId)
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        } else {
            Toast.makeText(this, "리뷰가 없습니다.", Toast.LENGTH_SHORT).show()
        }

        // 리뷰 작성 버튼 클릭
        writeButton.setOnClickListener {
            val intent = Intent(this, WritePage::class.java)
            startActivity(intent)
        }

        setupSwichButtons(userId)
    }

    private fun fetchReviewsFromDatabase(): List<Review> {
        val databaseHelper = DatabaseHelper()
        val reviews = mutableListOf<Review>()

        val query = "SELECT review_id, user_id, title, content, thumbs_up, comments_count FROM user_info.reviews"
        val connection = databaseHelper.connect()

        connection?.use {
            try {
                val statement = it.createStatement()
                val resultSet = statement.executeQuery(query)

                while (resultSet.next()) {
                    val review = Review(
                        reviewId = resultSet.getInt("review_id"),
                        title = resultSet.getString("title"),
                        content = resultSet.getString("content"),
                        date = "", // 데이터베이스에서 날짜 추가 가능
                        thumbsUp = resultSet.getInt("thumbs_up"),
                        comments = resultSet.getInt("comments_count"),
                        userId = resultSet.getInt("user_id")
                    )
                    reviews.add(review)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "리뷰 데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        return reviews
    }

    private fun setupSwichButtons(userId: Int)
    {
        val write = findViewById<TextView>(R.id.to_write)

        // 클릭 이벤트
        write.setOnClickListener {
            val intent = Intent(this, WritePage::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
    }
}
