package com.capstone.myapplication;

import android.os.Bundle
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.DatabaseHelper

class WritePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writepage)

        val userId = intent.getIntExtra("USER_ID", -1) // 디폴트 값 -1로 변경
        if (userId == -1) {
            Toast.makeText(this, "사용자 ID가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
            finish() // 잘못된 호출 종료
            return
        }
        val titleEditText = findViewById<EditText>(R.id.title)
        val contentEditText = findViewById<EditText>(R.id.content)

        val postButton = findViewById<RelativeLayout>(R.id.post_frame)
        postButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                saveReviewToDatabase(title, content, userId)
                Toast.makeText(this, "리뷰가 작성되었습니다.", Toast.LENGTH_SHORT).show()
                finish() // 리뷰 작성 후 종료
            } else {
                Toast.makeText(this, "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveReviewToDatabase(title: String, content: String, userId: Int) {
        val databaseHelper = DatabaseHelper(applicationContext)


        val isInserted = databaseHelper.insertReview(userId, title, content)

        if (isInserted) {
            Toast.makeText(this, "리뷰가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
            finish() // 작성 후 페이지 닫기
        } else {
            Toast.makeText(this, "리뷰 저장에 실패했습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show()
        }
    }
}
