package com.example.quickcurrencyconverter // 確保 package 名稱正確

data class SupportedCodesResponse(
    val result: String,
    val documentation: String,
    val terms_of_use: String,
    // supported_codes 是一個包含貨幣代碼和名稱的二維陣列 (例如: [["USD", "US Dollar"], ...])
    val supported_codes: List<List<String>>
)
