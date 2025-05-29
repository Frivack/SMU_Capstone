package com.capstone.myapplication

import android.os.Bundle
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.DatabaseHelper

class ReviewViewPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_view)

        val reviewId = intent.getIntExtra("REVIEW_ID", -1)
        val review = fetchReviewDetailsFromDatabase(reviewId)

        // 리뷰 상세 내용 설정
        val titleTextView = findViewById<TextView>(R.id.review_title)
        val contentTextView = findViewById<TextView>(R.id.review_content)
        val createDateView = findViewById<TextView>(R.id.date)
        val userIdView = findViewById<TextView>(R.id.ID)
        //val thumbsUpTextView = findViewById<TextView>(R.id.review_thumbs_up_text)
        //val commentsCountTextView = findViewById<TextView>(R.id.review_chat_text)

        titleTextView.text = review.title
        contentTextView.text = review.content
        createDateView.text = review.date
        //thumbsUpTextView.text = review.thumbsUp.toString()
        //commentsCountTextView.text = review.comments.toString()

        if (reviewId != 2)
        {
            val scroll = findViewById<HorizontalScrollView>(R.id.scroll)
            scroll.visibility = HorizontalScrollView.GONE
        }

        // 추천 버튼 클릭 이벤트
        val thumbsUpButton = findViewById<RelativeLayout>(R.id.thumbs_up_layout)
        thumbsUpButton.setOnClickListener {
            incrementThumbsUp(reviewId)
        }
    }

    private fun fetchReviewDetailsFromDatabase(reviewId: Int): Review {
        val databaseHelper = DatabaseHelper(this)
        val query = "SELECT title, content, created_at, user_id, thumbs_up, comments_count FROM user_info.reviews WHERE review_id = ?"
        val emailQuery = "SELECT email FROM user_info.users WHERE user_id = ?"
        var review = Review(reviewId, "", "", "", 0, 0, 0)
        var userEmail = ""

        val connection = databaseHelper.connect()
        connection?.use {
            try {
                val preparedStatement = it.prepareStatement(query)
                preparedStatement.setInt(1, reviewId)
                val resultSet = preparedStatement.executeQuery()

                if (resultSet.next()) {
                    val userId = resultSet.getInt("user_id")
                    review = Review(
                        reviewId = reviewId,
                        title = resultSet.getString("title"),
                        content = resultSet.getString("content"),
                        date = resultSet.getString("created_at"),
                        thumbsUp = resultSet.getInt("thumbs_up"),
                        comments = resultSet.getInt("comments_count"),
                        userId = resultSet.getInt("user_id")
                    )

                    // 사용자 이메일 가져오기
                    val emailStatement = it.prepareStatement(emailQuery)
                    emailStatement.setInt(1, userId)
                    val emailResultSet = emailStatement.executeQuery()
                    if (emailResultSet.next()) {
                        userEmail = emailResultSet.getString("email")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "리뷰 데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        val emailTextView = findViewById<TextView>(R.id.ID)
        emailTextView.text = userEmail

        return review
    }

    private fun incrementThumbsUp(reviewId: Int) {
        val databaseHelper = DatabaseHelper(this)
        val query = "UPDATE user_info.reviews SET thumbs_up = thumbs_up + 1 WHERE review_id = ?"

        val connection = databaseHelper.connect()
        connection?.use {
            try {
                val preparedStatement = it.prepareStatement(query)
                preparedStatement.setInt(1, reviewId)
                val rowsUpdated = preparedStatement.executeUpdate()

                if (rowsUpdated > 0) {
                    Toast.makeText(this, "추천하셨습니다!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "추천 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "추천 업데이트 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
