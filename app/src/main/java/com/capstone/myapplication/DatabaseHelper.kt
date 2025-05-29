package com.example.capstone

import android.content.Context
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.File
import java.io.FileOutputStream
import java.util.Properties

data class Review(
    val reviewId: Int,
    val userId: Int,
    val title: String,
    val content: String,
    val createdAt: String,
    val thumbsUp: Int,
    val commentsCount: Int
)

class DatabaseHelper(private val context: Context) {
    private var sshSession: Session? = null

    private val url = "jdbc:mysql://134.185.108.94:3306/pc_P?useSSL=false"
    private val user = "smu"
    private val password = "pV8qZr@31LwDbf\$eTgXo"
    // 데이터베이스 연결
    fun connect(): Connection? {
        return try {
            DriverManager.getConnection(url, user, password)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    // 🔽 assets에 있는 pem 파일을 내부 저장소로 복사하는 함수
    private fun copyPemFileToInternalStorage(context: Context): String {
        return try {
            val assetManager = context.assets
            val inputStream = assetManager.open("private_key.pem")
            val outFile = File(context.filesDir, "private_key.pem")
            inputStream.use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
            outFile.absolutePath
        } catch (e: Exception) {
            Log.e("copyPem", "❌ 파일 복사 실패: ${e.message}")
            throw RuntimeException("❌ private_key.pem 파일 복사 실패")
        }
    }


    fun disconnect() {
        sshSession?.disconnect()
    }
    // 데이터 가져오기
    fun fetchData(connection: Connection, query: String): List<Map<String, String>> {
        val results = mutableListOf<Map<String, String>>()
        try {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)
            val metaData = resultSet.metaData

            while (resultSet.next()) {
                val row = mutableMapOf<String, String>()
                for (i in 1..metaData.columnCount) {
                    row[metaData.getColumnName(i)] = resultSet.getString(i) ?: "N/A"
                }
                results.add(row)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return results
    }

    // 모든 부품 가져오기
    fun fetchAllParts(connection: Connection): Map<String, List<Map<String, String>>> {
        val tables = listOf(
            "cooler", "cpu", "earphone", "fan", "gpu", "hard_drive", "headset", "keyboard",
            "monitor", "motherboard", "mouse", "pc_case", "pcparts",
            "power_supply", "ram", "sound_card", "speaker", "storage"
        )

        val allParts = mutableMapOf<String, List<Map<String, String>>>()
        for (table in tables) {
            val query = "SELECT * FROM $table"
            allParts[table] = fetchData(connection, query)
        }
        return allParts
    }

    fun insertReview(userId: Int, title: String, content: String): Boolean {
        val query = "INSERT INTO user_info.reviews (user_id, title, content, created_at) VALUES (?, ?, ?, NOW())"
        return try {
            Log.d("insertReview", "리뷰 저장 시작")
            val connection = connect()
            if (connection == null) {
                Log.e("insertReview", "❌ 데이터베이스 연결 실패")
                return false
            }

            connection.use {
                Log.d("insertReview", "✅ 연결 성공, 쿼리 준비 중")
                val preparedStatement = it.prepareStatement(query)
                preparedStatement.setInt(1, userId)
                preparedStatement.setString(2, title)
                preparedStatement.setString(3, content)
                preparedStatement.executeUpdate()
            }

            Log.d("insertReview", "✅ 리뷰 저장 성공")
            true
        } catch (e: Exception) {
            Log.e("insertReview", "❌ 예외 발생: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    fun fetchAllReviews(): List<Review> {
        val reviews = mutableListOf<Review>()
        val query = "SELECT * FROM reviews"

        val connection = connect() ?: return reviews

        try {
            connection.use {
                val statement = it.createStatement()
                val resultSet = statement.executeQuery(query)

                while (resultSet.next()) {
                    val review = Review(
                        reviewId = resultSet.getInt("review_id"),
                        userId = resultSet.getInt("user_id"),
                        title = resultSet.getString("title"),
                        content = resultSet.getString("content"),
                        createdAt = resultSet.getTimestamp("created_at").toString(),
                        thumbsUp = resultSet.getInt("thumbs_up"),
                        commentsCount = resultSet.getInt("comments_count")
                    )
                    reviews.add(review)
                }
            }
        } catch (e: Exception) {
            Log.e("fetchAllReviews", "❌ 리뷰 불러오기 실패: ${e.message}")
            e.printStackTrace()
        }

        return reviews
    }

}
