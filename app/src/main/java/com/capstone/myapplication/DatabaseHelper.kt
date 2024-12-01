package com.example.capstone

import java.sql.Connection
import java.sql.DriverManager

class DatabaseHelper {
    private val url = "jdbc:mysql://34.64.196.101:3306/pc_P?useSSL=false"
    private val user = "smu"
    private val password = "xdaWPOS@RapzsLL-u,2CTlni0_PC'+@E\$,kobx2l{(_,;+aPim"

    // 데이터베이스 연결
    fun connect(): Connection? {
        return try {
            DriverManager.getConnection(url, user, password)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
            val connection = connect() // 기존의 연결 함수 사용
            connection?.use {
                val preparedStatement = it.prepareStatement(query)
                preparedStatement.setInt(1, userId)
                preparedStatement.setString(2, title)
                preparedStatement.setString(3, content)
                preparedStatement.executeUpdate()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
