package com.capstone.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PartsAdapter(
    private val context: Context,
    private var selectedParts: MutableList<Pair<String, Map<String, String>>>,
    private val allParts: Map<String, List<Map<String, String>>>, // 모든 부품 목록
    private val onPartSelected: (Int, Map<String, String>, List<Pair<String, Map<String, String>>>) -> Unit
) : RecyclerView.Adapter<PartsAdapter.PartViewHolder>() {

    private var isInitialBinding = true // 초기 바인딩 시 스피너 이벤트 방지 플래그

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_part, parent, false)
        return PartViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartViewHolder, position: Int) {
        // onBindViewHolder가 재호출될 때 이벤트가 무한 루프에 빠지는 것을 방지
        isInitialBinding = true
        holder.bind(position)
        isInitialBinding = false
    }

    override fun getItemCount(): Int = selectedParts.size

    fun updateData(newPartsList: List<Pair<String, Map<String, String>>>) {
        this.selectedParts = newPartsList.toMutableList()
        notifyDataSetChanged()
    }

    inner class PartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val partImage: ImageView = itemView.findViewById(R.id.part_image)
        private val partNameSpinner: Spinner = itemView.findViewById(R.id.part_name_spinner)
            private val partPrice: TextView = itemView.findViewById(R.id.part_price)

        fun bind(position: Int) {
            val (categoryKey, selectedPartData) = selectedParts[position]
            Glide.with(context).load(selectedPartData["image"]).into(partImage)

            // 1. 스피너에 들어갈 데이터 (부품 이름 리스트)
            val categoryParts = allParts[categoryKey] ?: emptyList()
            val partNames = categoryParts.map { it["name"] ?: "알 수 없는 부품" }

            val displayList = mutableListOf("선택된 부품 없음")
            displayList.addAll(partNames)

            // 2. 스피너 어댑터 설정
            val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, displayList)
            partNameSpinner.adapter = spinnerAdapter

            // 3. 현재 선택된 부품을 스피너에서 선택 상태로 설정
            val currentPartName = selectedPartData["name"]
            val currentIndex = categoryParts.indexOfFirst { it["name"] == currentPartName }
            partNameSpinner.setSelection(if (currentIndex != -1) currentIndex + 1 else 0, false)

            // 4. 현재 선택된 부품의 가격을 TextView에 설정
            val currentPrice = selectedPartData["price"]?.replace(".00", "")?.toIntOrNull() ?: 0
            partPrice.text = "₩${String.format("%,d", currentPrice)}"

            // 5. 스피너 선택 이벤트 리스너
            partNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, spinnerPosition: Int, id: Long) {
                    if (isInitialBinding) return

                    val newSelectedPart = if (spinnerPosition > 0) {
                        categoryParts[spinnerPosition - 1]
                    } else {
                        mapOf("name" to "선택된 부품 없음", "price" to "0", "image" to "")
                    }

                    // 선택된 부품이 현재 부품과 다를 경우에만 콜백 호출 (무한 루프 방지)
                    if (newSelectedPart["name"] != selectedParts[position].second["name"]) {
                        onPartSelected(position, newSelectedPart, selectedParts)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }
}