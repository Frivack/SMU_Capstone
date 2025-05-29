package com.capstone.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BudgetPage : AppCompatActivity() {

    // 변수 초기화
    private lateinit var totalBudgetEdit: EditText // 총 예산 입력을 위한 EditText
    private lateinit var remainingBudgetText: TextView // 남은 예산을 표시할 TextView
    private var totalBudget: Int = 0 // 초기 총 예산
    private var remainingBudget: Int = totalBudget

    // SeekBar와 TextView를 저장할 리스트
    private val seekBars = mutableListOf<SeekBar>()
    private val percentageTexts = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        // 텍스트뷰와 SeekBar 초기화
        seekBars.add(findViewById(R.id.cpu_seekBar))
        seekBars.add(findViewById(R.id.gpu_seekBar))
        seekBars.add(findViewById(R.id.mainboard_seekBar))
        seekBars.add(findViewById(R.id.ram_seekBar))
        seekBars.add(findViewById(R.id.power_seekBar))
        seekBars.add(findViewById(R.id.hard_seekBar))
        seekBars.add(findViewById(R.id.cooler_seekBar))
        seekBars.add(findViewById(R.id.fan_seekBar))
        seekBars.add(findViewById(R.id.soundcard_seekBar))
        seekBars.add(findViewById(R.id.pccase_seekBar))
        seekBars.add(findViewById(R.id.mouse_seekBar))
        seekBars.add(findViewById(R.id.monitor_seekBar))
        seekBars.add(findViewById(R.id.keyboard_seekBar))
        seekBars.add(findViewById(R.id.headset_seekBar))
        seekBars.add(findViewById(R.id.earphone_seekBar))
        seekBars.add(findViewById(R.id.speaker_seekBar))

        percentageTexts.add(findViewById(R.id.cpu_percentage))
        percentageTexts.add(findViewById(R.id.gpu_percentage))
        percentageTexts.add(findViewById(R.id.mainboard_percentage))
        percentageTexts.add(findViewById(R.id.ram_percentage))
        percentageTexts.add(findViewById(R.id.power_percentage))
        percentageTexts.add(findViewById(R.id.hard_percentage))
        percentageTexts.add(findViewById(R.id.cooler_percentage))
        percentageTexts.add(findViewById(R.id.fan_percentage))
        percentageTexts.add(findViewById(R.id.soundcard_percentage))
        percentageTexts.add(findViewById(R.id.pccase_percentage))
        percentageTexts.add(findViewById(R.id.mouse_percentage))
        percentageTexts.add(findViewById(R.id.monitor_percentage))
        percentageTexts.add(findViewById(R.id.keyboard_percentage))
        percentageTexts.add(findViewById(R.id.headset_percentage))
        percentageTexts.add(findViewById(R.id.earphone_percentage))
        percentageTexts.add(findViewById(R.id.speaker_percentage))

        // 각 SeekBar에 리스너 추가
        for (i in seekBars.indices) {
            setupSeekBar(seekBars[i], percentageTexts[i])
        }

        // 뷰 초기화
        totalBudgetEdit = findViewById(R.id.total_budget_edit)
        remainingBudgetText = findViewById(R.id.left_budget)

        // 초기 총 예산 텍스트 설정
        totalBudgetEdit.setText(totalBudget.toString())
        remainingBudgetText.text = "남은 예산: ₩${String.format("%,d", remainingBudget)}"

        totalBudgetEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트 변경 전
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 중
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 변경 후
                val newBudget = s.toString().toIntOrNull()
                if (newBudget != null && newBudget > 0) {
                    totalBudget = newBudget
                    updateRemainingBudget()
                } else if (s.isNullOrEmpty()) {
                    // 아무 값도 입력되지 않은 경우 초기화
                    remainingBudgetText.text = "남은 예산: -"
                } else {
                    Toast.makeText(this@BudgetPage, "올바른 금액을 입력하세요.", Toast.LENGTH_SHORT).show()
                }

                updateSeekBars()
            }
        })

        val currentBudget = intent.getStringExtra("CURRENT_BUDGET")
        val totalBudgetEdit = findViewById<EditText>(R.id.total_budget_edit)
        totalBudgetEdit.setText(currentBudget)

        // 완료 버튼 클릭 시 데이터 반환
        val finishButton = findViewById<RelativeLayout>(R.id.finish_button)
        finishButton.setOnClickListener {
            val totalBudget = totalBudgetEdit.text.toString()
            val remainingBudgetString = remainingBudgetText.text.toString()
            val extractedBudget = remainingBudgetString.replace("[^\\d]".toRegex(), "").toIntOrNull()
            val percentageValues = percentageTexts.map { it.text.toString() }

            val resultIntent = Intent()
            resultIntent.putExtra("TOTAL_BUDGET", totalBudget)
            resultIntent.putExtra("REMAINING_BUDGET", extractedBudget.toString())
            resultIntent.putStringArrayListExtra("PERCENTAGE_VALUES", ArrayList(percentageValues))

            setResult(RESULT_OK, resultIntent)
            finish() // 현재 액티비티 종료
        }

        // 드롭다운 메뉴 생성
        val spinner = findViewById<Spinner>(R.id.spinner_budget_options)

        // 드롭다운 메뉴 데이터 정의
        val options = listOf("Option 1", "Option 2", "Option 3", "Option 4")


        val adapter = ArrayAdapter(
            this,
            R.layout.custom_spinner_item, // 커스텀 레이아웃
            options
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // 선택된 아이템
                val selectedItem = options[position]
                Toast.makeText(this@BudgetPage, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무것도 선택되지 않은 경우 처리
            }
        }
    }

    private fun setupSeekBar(seekBar: SeekBar, valueText: TextView) {
        seekBar.max = 100 // SeekBar는 퍼센트로 설정 (최대 100%)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateRemainingBudget()
                // SeekBar 값(%)과 금액 변환 값을 표시
                val allocatedAmount = (progress * totalBudget) / 100
                valueText.text = "$progress% \n(${formatCurrency(allocatedAmount)})"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar를 터치하기 시작했을 때 처리
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar 터치를 끝냈을 때 처리
            }
        })
    }

    private fun updateRemainingBudget() {
        // 모든 SeekBar 값(%)을 금액으로 변환하여 합산
        var totalUsed = 0
        for (seekBar in seekBars) {
            totalUsed += (seekBar.progress * totalBudget) / 100
        }

        // 남은 예산 계산
        remainingBudget = totalBudget - totalUsed

        // 남은 예산 업데이트
        remainingBudgetText.text = "남은 예산: ${formatCurrency(remainingBudget)}"

        // 예산 초과 시 경고 표시
        if (totalUsed > totalBudget) {
            remainingBudgetText.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        } else {
            remainingBudgetText.setTextColor(resources.getColor(android.R.color.black, null))
        }
    }

    private fun updateSeekBars() {
        // 총 예산이 변경되었을 때 모든 SeekBar와 텍스트 업데이트
        for ((index, seekBar) in seekBars.withIndex()) {
            val progress = seekBar.progress
            val allocatedAmount = (progress * totalBudget) / 100
            percentageTexts[index].text = "$progress% \n(${formatCurrency(allocatedAmount)})"
        }

        // 남은 예산도 재계산
        updateRemainingBudget()
    }

    private fun formatCurrency(amount: Int): String {
        // 숫자를 ₩ 단위로 포맷팅
        return "₩${String.format("%,d", amount)}"
    }
}
