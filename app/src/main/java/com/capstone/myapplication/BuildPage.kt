package com.capstone.myapplication
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.DatabaseHelper
import com.example.capstone.Optimizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class BuildPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buildpage)

        setupSwichButtons()
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

    companion object {
        const val REQUEST_CODE_BUDGET = 1 // 고유한 요청 코드
    }

    private fun setupSwichButtons()
    {
        val budget_layout = findViewById<RelativeLayout>(R.id.frame_2)

        // 클릭 이벤트
        budget_layout.setOnClickListener {
            val intent = Intent(this, BudgetPage::class.java)
            startActivityForResult(intent, REQUEST_CODE_BUDGET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // BudgetPage에서 반환된 데이터 처리
            val totalBudget = data?.getStringExtra("TOTAL_BUDGET")
            val remainingBudget = data?.getStringExtra("REMAINING_BUDGET")
            // 반환된 데이터를 화면에 표시
            val budgetInput = findViewById<TextView>(R.id.total_budget)
            budgetInput.setText(totalBudget)
            // 추가적으로 남은 예산을 표시하려면 적절한 View에 설정
            val budgetLeft = findViewById<TextView>(R.id.left_budget)
            budgetLeft.setText(remainingBudget)
        }
    }
}