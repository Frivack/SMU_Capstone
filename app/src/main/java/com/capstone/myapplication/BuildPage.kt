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
import com.example.capstone.ApiHelper
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
    private val apiHelper = ApiHelper()
    private val orderedPartsKeys = listOf(
        "cpu", "gpu", "motherboard", "ram", "power_supply", "hard_drive", "cooler",
        "fan", "sound_card",  "pc_case", "mouse", "monitor", "keyboard", "headset",
        "earphone", "speaker"
    )
    private val REQUEST_CODE_PART_SELECT = 1000
    class PartSelectDialog : AppCompatActivity() {
        // 아직 비워놔도 되고, 추후 부품 리스트 띄우는 코드 작성
    }
    // 모든 부품 데이터 (카테고리별 부품리스트)
    private var allParts: Map<String, List<Map<String, String>>> = emptyMap()

    // 현재 선택된 부품 리스트
    private var selectedParts: MutableList<Pair<String, Map<String, String>>> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buildpage)

        viewIds.forEachIndexed { index, viewId ->
            val imageView = findViewById<View>(viewId)
            imageView.setOnClickListener {
                // 1. 어떤 부품(카테고리)인지 인덱스, 키, id 등을 넘김
                val categoryKey = orderedPartsKeys[index]

                // 2. 다이얼로그 or 액티비티를 띄움 (여기서는 예시로 PartSelectDialog 액티비티)
                val intent = Intent(this, PartSelectDialog::class.java)
                intent.putExtra("CATEGORY_KEY", categoryKey)
                // 현재 선택된 부품 id 등도 넘길 수 있음
                // intent.putExtra("CURRENT_PART_ID", 현재부품id)
                startActivityForResult(intent, REQUEST_CODE_PART_SELECT + index)
            }
        }
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
        Log.e("MSQLTESTING", "msql start")
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("MSQLTESTING", "connect start")
            val allPartsFetched = apiHelper.fetchAllParts()
            // 변환
            val convertedAllParts = allPartsFetched.mapValues { (_, partsList) ->
                partsList.map { partMap -> partMap.mapValues { (_, value) -> value?.toString() ?: "" } }
            }
            // 여기서 멤버변수에 저장!
            allParts = convertedAllParts

            val optimalParts = selectOptimalParts(convertedAllParts, percentageValues)
            // 이것도 멤버변수로 저장!
            selectedParts = optimalParts.toMutableList()
            withContext(Dispatchers.Main) {
                updateUI(selectedParts)
            }
            Log.e("MSQLTESTING", "msql finish ")
        //connection?.use {
                //val allParts = dbHelper.fetchAllParts(it) // 모든 부품 데이터 가져오기
                //val selectedParts = selectOptimalParts(allParts) // 최적의 부품 선택
                //val selectedParts = selectOptimalParts(allParts, percentageValues)
                // 예산 내에서 최적의 부품 선택

                // UI 업데이트는 Main 스레드에서 처리
                //withContext(Dispatchers.Main) {
                    //loadImages(selectedParts)
                    //updateUI(selectedParts)
                //}
            //}
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

        Log.e("Start Building Page", "RESULT CODE$resultCode")
        Log.e("Start Building Page", "REQUEST CODE$requestCode")
        // 1. 예산 관련 (이미 있던 코드)
        if (requestCode == REQUEST_CODE_BUDGET && resultCode == RESULT_OK) {
            // ... (생략: 네가 이미 쓰는 예산 관련 코드)
        }

        Log.e("Start Building Page", "RESULT CODE$resultCode")

        // 2. 부품 교체 관련 (추가되는 부분)
        // 부품 선택 요청코드의 범위 체크
        if (resultCode == RESULT_OK &&
            requestCode in REQUEST_CODE_PART_SELECT until (REQUEST_CODE_PART_SELECT + orderedPartsKeys.size)
        ) {
            Log.e("Start Building Page", "2")
            val partIndex = requestCode - REQUEST_CODE_PART_SELECT
            val selectedPartId = data?.getStringExtra("SELECTED_PART_ID") ?: return

            // allParts와 selectedParts는 멤버변수화 해두는 게 좋음!
            val categoryKey = orderedPartsKeys[partIndex]
            val allCategoryParts = allParts[categoryKey] // 이제 멤버변수니까 접근 OK!
            val newPart = allCategoryParts?.find { it["id"] == selectedPartId }

            if (newPart != null) {
                Log.e("Start Building Page", "3")
                // selectedParts를 mutableList로 만들었다고 가정 (selectedParts[partIndex] 교체)
                selectedParts[partIndex] = categoryKey to newPart
                updateUI(selectedParts)
                // 남은 예산 등 갱신
                val totalSelectedPrice = selectedParts.sumOf { (_, part) ->
                    part["price"]?.replace(",", "")?.replace(".00", "")?.toIntOrNull() ?: 0
                }
                val remainingBudget = totalBudget - totalSelectedPrice
                updateRemainingBudget(remainingBudget)
            }
        }
        Log.e("Start Building Page", "end")
    }
}
