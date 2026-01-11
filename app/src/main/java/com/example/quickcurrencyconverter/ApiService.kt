package com.example.quickcurrencyconverter

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    // {apiKey} 和 {baseCode} 是動態變數
    @GET("v6/{apiKey}/latest/{baseCode}")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("baseCode") baseCode: String
    ): Response<ExchangeRateResponse>

    // 新增一個獲取所有支援貨幣代碼的函式
    @GET("v6/{apiKey}/codes")
    suspend fun getSupportedCodes(@Path("apiKey") apiKey: String): Response<SupportedCodesResponse>
}
