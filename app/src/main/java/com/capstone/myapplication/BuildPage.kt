package com.capstone.myapplication
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.DatabaseHelper
import com.example.capstone.Optimizer
import com.mysql.jdbc.log.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BuildPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buildpage)
    }

    fun msql(view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            val dbHelper = DatabaseHelper()
            val optimizer = Optimizer()
            val connection = dbHelper.connect()

            connection?.use {
                val allParts = dbHelper.fetchAllParts(it)

                // 사용자 예산 및 비율 설정
                val userBudget = 1500000
                val percentageDistribution = mapOf(
                    "cpu" to 30, "gpu" to 40, "ram" to 10, "storage" to 10,
                    "motherboard" to 5, "cooler" to 5
                )

                // 부품 선택
                val selectedParts =
                    optimizer.selectOptimalParts(allParts, userBudget, percentageDistribution)

                val totalPrice = selectedParts.values.sumOf { it["price"]?.toIntOrNull() ?: 0 }
                val resultText = StringBuilder("총 가격: $totalPrice 원\n")
                selectedParts.forEach { (partType, partDetails) ->
                    resultText.append(" - $partType: ${partDetails["name"]} (${partDetails["price"]}원)\n")
                }
                resultText.append("--------------------\n")

                withContext(Dispatchers.Main) {
                    //val resultTextView: TextView = findViewById(R.id.estimate_result)
                    //resultTextView.text = resultText.toString()
                }
            }
        }
    }
}