package com.example.capstone

class Optimizer {
    // 예산에 따른 최적 부품 선택
    fun selectOptimalParts(
        allParts: Map<String, List<Map<String, String>>>,
        budget: Int,
        percentageDistribution: Map<String, Int>
    ): Map<String, Map<String, String>> {
        val selectedParts = mutableMapOf<String, Map<String, String>>()

        percentageDistribution.forEach { (partType, percentage) ->
            val allocatedBudget = budget * percentage / 100
            val partsForType = allParts[partType] ?: emptyList()
            val optimalPart = partsForType
                .filter { it["price"]?.toIntOrNull() ?: Int.MAX_VALUE <= allocatedBudget }
                .minByOrNull { it["price"]?.toIntOrNull() ?: Int.MAX_VALUE }
            if (optimalPart != null) {
                selectedParts[partType] = optimalPart
            }
        }

        return selectedParts
    }
}
