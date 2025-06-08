package com.capstone.myapplication

import android.os.Bundle
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.ApiHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WritePage : AppCompatActivity() {
    private val apiHelper = ApiHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writepage)

        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            Toast.makeText(this, "사용자 ID가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val titleEditText = findViewById<EditText>(R.id.title)
        val contentEditText = findViewById<EditText>(R.id.content)
        val postButton = findViewById<RelativeLayout>(R.id.post_frame)

        postButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                saveReviewToServer(title, content, userId)
            } else {
                Toast.makeText(this, "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveReviewToServer(title: String, content: String, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val isInserted = apiHelper.insertReview(userId, title, content)
            withContext(Dispatchers.Main) {
                if (isInserted) {
                    Toast.makeText(this@WritePage, "리뷰가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@WritePage, "리뷰 저장에 실패했습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
