package com.capstone.myapplication

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.capstone.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BuildPage : AppCompatActivity() {

    // XML에서 각 View의 ID를 배열로 정의
    private val viewIds = listOf(
        R.id.img31, R.id.img32, R.id.img33, R.id.img34, R.id.img35, R.id.img36,
        R.id.img37, R.id.img38, R.id.img39, R.id.img310, R.id.img311, R.id.img312,
        R.id.img313, R.id.img314, R.id.img315, R.id.img316
    )
    private val textComponentIds = listOf(
        R.id.compnent31, R.id.compnent32, R.id.compnent33, R.id.compnent34, R.id.compnent35, R.id.compnent36,
        R.id.compnent37, R.id.compnent38, R.id.compnent39, R.id.compnent310, R.id.compnent311, R.id.compnent312,
        R.id.compnent313, R.id.compnent314, R.id.compnent315, R.id.compnent316
    )
    private val textPriceIds = listOf(
        R.id.price31, R.id.price32, R.id.price33, R.id.price34, R.id.price35, R.id.price36,
        R.id.price37, R.id.price38, R.id.price39, R.id.price310, R.id.price311, R.id.price312,
        R.id.price313, R.id.price314, R.id.price315, R.id.price316
    )
    private var totalBudget: Int = 0 // 예산 데이터

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buildpage)

        // 예산 텍스트뷰 기본 메시지 설정
        val budgetTextView = findViewById<TextView>(R.id.total_budget)
        budgetTextView.text = "클릭하여 예산을 설정하세요" // 기본 메시지 설정

        // 예산 텍스트뷰 클릭 시 BudgetPage로 이동
        budgetTextView.setOnClickListener {
            val intent = Intent(this, BudgetPage::class.java)
            startActivityForResult(intent, REQUEST_CODE_BUDGET)
        }

        // 전원 버튼 설정 클릭
        setupSwichButtons()

        // Intent로 전달된 예산 데이터 수신
        totalBudget = intent.getIntExtra("TOTAL_BUDGET", 0)
        Log.d("BuildPage", "받은 예산: $totalBudget")
    }

    fun msql(view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            val dbHelper = DatabaseHelper(applicationContext) // 기존 DatabaseHelper 사용
            val connection = dbHelper.connect() // 데이터베이스 연결

            connection?.use {
                val allParts = dbHelper.fetchAllParts(it) // 모든 부품 데이터 가져오기
                //val selectedParts = selectOptimalParts(allParts) // 최적의 부품 선택
                val selectedParts = selectOptimalParts(allParts, percentageValues)
                // 예산 내에서 최적의 부품 선택

                // UI 업데이트는 Main 스레드에서 처리
                withContext(Dispatchers.Main) {
                    //loadImages(selectedParts)
                    updateUI(selectedParts)
                }
            }
        }
    }

    private fun selectOptimalParts(
        allParts: Map<String, List<Map<String, String>>>,
        percentageValues: List<String> // percentageValues를 매개변수로 받음
    ): List<Pair<String, Map<String, String>>> {
        val orderedPartsKeys = listOf(
            "cpu", "gpu", "motherboard", "ram", "power_supply", "hard_drive", "cooler",
            "fan", "sound_card",  "pc_case", "mouse", "monitor", "keyboard", "headset",
            "earphone", "speaker"
        )

        val selectedParts = mutableListOf<Pair<String, Map<String, String>>>()
        var remainingBudget = totalBudget // 초기 예산
        val selectedPartIds = mutableSetOf<String>() // 선택된 부품 ID를 저장

        orderedPartsKeys.forEachIndexed { index, key ->
            val parts = allParts[key]

            // 카테고리에 할당된 예산 (percentageValues에서 가져옴)
            val allocatedBudget = if (index < percentageValues.size) {
                val budgetString = percentageValues[index]
                // 괄호 안의 금액을 추출
                val matchResult = Regex("\\(₩([\\d,]+)\\)").find(budgetString)
                matchResult?.groupValues?.get(1)?.replace(",", "")?.toIntOrNull() ?: 0
            } else {
                0
            }

            Log.e("EEEEEEEEEEEEEE", "$allocatedBudget")

            // 예산이 0원인 경우 "선택된 부품 없음"으로 처리
            if (allocatedBudget == 0) {
                selectedParts.add(key to mapOf("image" to "", "name" to "선택된 부품 없음", "price" to "0"))
                return@forEachIndexed
            }
            Log.e("XXXXXXXXXXXXXXXXXXXXX", percentageValues[index])

            // 사용 가능한 예산 (남은 예산 고려)
            //val effectiveBudget = minOf(remainingBudget, allocatedBudget)

            Log.d("BuildPage", "$key 카테고리: 할당된 예산 ₩$allocatedBudget, 남은 예산 ₩$remainingBudget")

            //val performanceKey = performanceCriteria[key]
            // 예산이 0원이거나 부품이 없을 경우 기본값 추가
            if (allocatedBudget == 0 || parts == null || parts.isEmpty()) {
                selectedParts.add(key to mapOf("image" to "", "name" to "선택된 부품 없음", "price" to "0"))
                return@forEachIndexed
            }

            // 예산 내에서 가장 비싼 부품 선택
            val bestPart = parts.filter { part ->
                val price = part["price"]?.replace(",", "")?.replace(".00", "")?.toIntOrNull() ?: 0
                price <= allocatedBudget // 예산 내 부품만 고려
            }.maxByOrNull { part ->
                part["price"]?.replace(",", "")?.replace(".00", "")?.toIntOrNull() ?: 0
            }

            if (bestPart != null) {
                selectedParts.add(key to bestPart)
                remainingBudget -= bestPart["price"]?.replace(",", "")?.replace(".00", "")?.toIntOrNull() ?: 0
                Log.d("Selected Part", "Category: $key, Selected: ${bestPart["name"]}, Price: ${bestPart["price"]}")
            } else {
                selectedParts.add(key to mapOf("image" to "", "name" to "선택된 부품 없음", "price" to "0"))
                Log.d("Selected Part", "Category: $key, No suitable part found.")
            }
        }

        // 선택되지 않은 카테고리는 "선택된 부품 없음"으로 채움
        while (selectedParts.size < orderedPartsKeys.size) {
            selectedParts.add("placeholder" to mapOf("image" to "", "name" to "선택된 부품 없음", "price" to "0"))
        }

        // 선택된 부품들의 가격 합산
        val totalSelectedPrice = selectedParts.sumOf { (_, part) ->
            part["price"]?.replace(",", "")?.replace(".00", "")?.toIntOrNull() ?: 0
        }
        remainingBudget = totalBudget - totalSelectedPrice // 남은 예산 계산
        updateRemainingBudget(remainingBudget) // UI 업데이트

        Log.d("BuildPage", "최종 선택된 부품 리스트: ${selectedParts.map { it.second["name"] }}")
        return selectedParts
    }



    private fun updateRemainingBudget(remainingBudget: Int) {
        runOnUiThread {
            val budgetLeftTextView = findViewById<TextView>(R.id.left_budget)
            budgetLeftTextView.text = "₩${String.format("%,d", remainingBudget)}"
        }
    }


    private fun updateUI(selectedParts: List<Pair<String, Map<String, String>>>) {
        selectedParts.forEachIndexed { index, (_, part) ->
            if (index >= viewIds.size) return@forEachIndexed // 뷰 ID를 초과하지 않도록 제한

            val viewId = viewIds[index]
            val view = findViewById<View>(viewId) // 이미지 뷰를 가져옴

            // 텍스트 뷰 ID 가져오기
            val imageViewId = viewIds[index]
            val nameViewId = resources.getIdentifier("compnent3${index + 1}", "id", packageName)
            val priceViewId = resources.getIdentifier("price3${index + 1}", "id", packageName)

            val imageView = findViewById<View>(imageViewId)
            val nameView = findViewById<TextView>(nameViewId)
            val priceView = findViewById<TextView>(priceViewId)

            // 부품 정보 가져오기
            val imageUrl = part["image"]
            val name = part["name"] ?: "선택된 부품 없음"
            val price = part["price"]?.replace(",", "")?.replace(".00", "")?.toIntOrNull()
                ?.let { String.format("₩%,d", it) } ?: "가격 없음"
            // TextView에 부품 이름과 가격 설정
            nameView?.text = name
            priceView?.text = price

            // 이미지 URL 가져오기
            //val imageUrl = part["image"]
            if (!imageUrl.isNullOrEmpty()) {
                // Glide로 이미지 로드
                Glide.with(this)
                    .load(imageUrl)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            imageView.background = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // 필요 시 자리 표시자 처리
                        }
                    })
                Log.d("BuildPage", "부품 ${part["name"]}의 이미지를 로드했습니다.")
            } else {
                Log.d("BuildPage", "부품 ${part["name"]}에 이미지 URL이 없습니다.")
            }

            // 로그 출력 (디버깅용)
            Log.d("BuildPage", "텍스트 뷰: ${nameView?.id}, 이름: $name, 가격: $price")
            // 텍스트 설정
            nameView.text = name
            priceView.text = price
        }
    }

    companion object {
        const val REQUEST_CODE_BUDGET = 1 // 고유한 요청 코드
    }

    private fun setupSwichButtons()
    {
        val budgetLayout = findViewById<RelativeLayout>(R.id.frame_2)

        // 클릭 이벤트
        budgetLayout.setOnClickListener {
            val intent = Intent(this, BudgetPage::class.java)
            startActivityForResult(intent, REQUEST_CODE_BUDGET)
        }
    }

    private var percentageValues: List<String> = emptyList() // 전역 변수로 선언

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_BUDGET && resultCode == RESULT_OK) {
            // BudgetPage에서 반환된 예산 데이터를 수신
            // TOTAL_BUDGET 수신 및 처리
            val totalBudgetString = data?.getStringExtra("TOTAL_BUDGET") ?: "0"
            totalBudget = totalBudgetString.toIntOrNull() ?: 0
            // REMAINING_BUDGET 수신 및 처리
            val remainingBudgetString = data?.getStringExtra("REMAINING_BUDGET") ?: "0"
            // PERCENTAGE_VALUES 수신 및 처리
            val percentageValues = data?.getStringArrayListExtra("PERCENTAGE_VALUES") ?: arrayListOf()
            // UI에 예산 데이터 반영
            // UI에 예산 데이터 반영
            findViewById<TextView>(R.id.total_budget).text = "₩${String.format("%,d", totalBudget)}"
            findViewById<TextView>(R.id.left_budget).text = "₩${remainingBudgetString.toIntOrNull() ?: 0}"
            // PERCENTAGE_VALUES 로그 출력 (확인용)
            Log.d("BuildPage", "BudgetPage에서 받은 예산: $totalBudget")
            // 예산을 기반으로 부품 선택 및 UI 업데이트
            // 부품 선택 및 UI 업데이트
            CoroutineScope(Dispatchers.IO).launch {
                val dbHelper = DatabaseHelper(applicationContext)
                val connection = dbHelper.connect()

                connection?.use {
                    val allParts = dbHelper.fetchAllParts(it)

                    // percentageValues를 전달하여 selectOptimalParts 호출
                    val selectedParts = selectOptimalParts(allParts, percentageValues)

                    // UI 업데이트는 Main 스레드에서 처리
                    withContext(Dispatchers.Main) {
                        updateUI(selectedParts)
                    }
                }
            }
        }
    }
}
