package com.capstone.myapplication

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.capstone.ApiHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BuildPage : AppCompatActivity() {
    private var totalBudget: Int = 0 // 예산 데이터
    private val apiHelper = ApiHelper()
    private val orderedPartsKeys = listOf(
        "cpu", "gpu", "motherboard", "ram", "power_supply", "hard_drive", "cooler",
        "fan", "sound_card",  "pc_case", "mouse", "monitor", "keyboard", "headset",
        "earphone", "speaker"
    )
    private val REQUEST_CODE_PART_SELECT = 1000
    private val REQUEST_CODE_BUDGET = 1
    class PartSelectDialog : AppCompatActivity() {
        // 아직 비워놔도 되고, 추후 부품 리스트 띄우는 코드 작성
    }

    private lateinit var partsAdapter: PartsAdapter
    private var allParts: Map<String, List<Map<String, String>>> = mapOf() // 모든 부품 정보 저장
    private var selectedParts: MutableList<Pair<String, Map<String, String>>> = mutableListOf()

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

        setupRecyclerView()

        // 이동 버튼 설정 클릭
        setupSwichButtons()

        // Intent로 전달된 예산 데이터 수신
        totalBudget = intent.getIntExtra("TOTAL_BUDGET", 0)
        Log.d("BuildPage", "받은 예산: $totalBudget")
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

            // 예산이 0원인 경우 "선택된 부품 없음"으로 처리
            if (allocatedBudget == 0) {
                selectedParts.add(key to mapOf("image" to "", "name" to "선택된 부품 없음", "price" to "0"))
                return@forEachIndexed
            }
            Log.d("BuildPage", "$key 카테고리: 할당된 예산 ₩$allocatedBudget, 남은 예산 ₩$remainingBudget")

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

    private fun setupSwichButtons()
    {
        val budgetLayout = findViewById<RelativeLayout>(R.id.frame_2)

        // 클릭 이벤트
        budgetLayout.setOnClickListener {
            val intent = Intent(this, BudgetPage::class.java)
            startActivityForResult(intent, REQUEST_CODE_BUDGET)
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.parts_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 어댑터 초기화. 콜백 함수로 handlePartSelection을 넘겨줌
        //partsAdapter = PartsAdapter(this, selectedParts, allParts) { index, newPart ->
        //    handlePartSelection(index, newPart)
        //}
        //recyclerView.adapter = partsAdapter
    }

    // 부품이 변경되었을 때 호출되는 함수
    private fun handlePartSelection(index: Int, newPart: Map<String, String>) {
        val (categoryKey, _) = selectedParts[index]

        // 1. selectedParts 리스트 업데이트
        selectedParts[index] = categoryKey to newPart

        // 2. 어댑터에 변경 사항 알림 (★중요★: 전체 데이터를 다시 전달)
        //    단일 아이템만 바꾸면 스피너 상태가 꼬일 수 있으므로 전체를 갱신
        partsAdapter.updateData(selectedParts)

        // 3. 남은 예산 등 다른 UI 업데이트
        updateRemainingBudget()

        // 4. (선택) 변경된 부품의 이미지 업데이트
        val newImageUrl = newPart["image"]
        // Glide.with(this).load(newImageUrl).into(...)

        Log.d("BuildPage", "부품 변경: ${categoryKey} -> ${newPart["name"]}")
    }

    // 남은 예산 계산 및 UI 업데이트 함수
    private fun updateRemainingBudget() {
        val totalSelectedPrice = selectedParts.sumOf { (_, part) ->
            part["price"]?.replace(",", "")?.replace(".00", "")?.toIntOrNull() ?: 0
        }
        val remaining = totalBudget - totalSelectedPrice

        val leftBudgetTextView = findViewById<TextView>(R.id.left_budget)
        leftBudgetTextView.text = "₩${String.format("%,d", remaining)}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_BUDGET && resultCode == RESULT_OK) {// BudgetPage에서 반환된 예산 데이터를 수신
            // BudgetPage에서 반환된 예산 데이터를 수신
            val totalBudgetString = data?.getStringExtra("TOTAL_BUDGET") ?: "0"
            totalBudget = totalBudgetString.toIntOrNull() ?: 0
            val remainingBudgetString = data?.getStringExtra("REMAINING_BUDGET") ?: "0"
            val percentageValues = data?.getStringArrayListExtra("PERCENTAGE_VALUES") ?: arrayListOf()
            // UI에 예산 데이터 반영
            findViewById<TextView>(R.id.total_budget).text = "₩${String.format("%,d", totalBudget)}"
            findViewById<TextView>(R.id.left_budget).text = "₩${remainingBudgetString.toIntOrNull() ?: 0}"
            // PERCENTAGE_VALUES 로그 출력 (확인용)
            Log.d("BuildPage", "BudgetPage에서 받은 예산: $totalBudget")
            // 예산을 기반으로 부품 선택 및 UI 업데이트
            CoroutineScope(Dispatchers.IO).launch {
                val allParts = apiHelper.fetchAllParts()
                val convertedAllParts = allParts.mapValues { (_, partsList) ->
                    partsList.map { partMap ->
                        partMap.mapValues { (_, value) -> value?.toString() ?: "" }
                    }
                }

                this@BuildPage.allParts = convertedAllParts

                val initialSelectedParts = selectOptimalParts(convertedAllParts, percentageValues)

                this@BuildPage.selectedParts = initialSelectedParts.toMutableList()

                withContext(Dispatchers.Main) {
                    val partSelectionCallback = { index: Int, newPart: Map<String, String> ->
                        handlePartSelection(index, newPart)
                    }
                    partsAdapter = PartsAdapter(
                        this@BuildPage,
                        this@BuildPage.selectedParts,
                        this@BuildPage.allParts,
                        partSelectionCallback
                    )

                    findViewById<RecyclerView>(R.id.parts_recycler_view).adapter = partsAdapter

                    updateRemainingBudget() // 최초 남은 예산 업데이트
                }
            }
        }
    }
}
