package com.heartcare.agni.data.local.enums

enum class RiskCategoryEnum(val label: String, val riskRange: IntRange) {
    LESS_THAN_FIVE("Less than 5%", 0..4),
    FIVE_TO_NINE("5% – 9%", 5..9),
    TEN_TO_NINETEEN("10% – 19%", 10..19),
    TWENTY_TO_TWENTY_NINE("20% – 29%", 20..29),
    THIRTY_OR_MORE("30% or more", 30..100);

    companion object {
        fun getRiskCategoryList(): List<String> = RiskCategoryEnum.entries.map { it.label }
    }
}