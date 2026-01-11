package com.example.quickcurrencyconverter


import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.quickcurrencyconverter.ApiService
import com.example.quickcurrencyconverter.databinding.ActivityMainBinding // 這裡的 package name 可能需要根據您的實際專案名稱修改
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // 請替換為您的 API 金鑰！
    private val API_KEY = "102f673577ce7d849d7b045a"
    private val BASE_URL = "https://v6.exchangerate-api.com/"
    private var currencyCodes = emptyArray<String>()

    private var finalFromCurrency: String = ""
    private var finalToCurrency: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 在啟動時獲取貨幣列表
        fetchCurrencyCodes()

        // --- 新增自動完成的 Adapter ---
        val adapter = ArrayAdapter<String>(
            this,
            R.layout.simple_dropdown_item_1line, // 使用 Android 內建的簡單佈局
            currencyCodes // 我們第二步準備的列表
        )

        // 將 Adapter 綁定到兩個 AutoCompleteTextView
        binding.etFromCurrency.setAdapter(adapter)
        binding.etToCurrency.setAdapter(adapter)

        // 設定按下一個字元後就開始搜尋
        binding.etFromCurrency.threshold = 1
        binding.etToCurrency.threshold = 1
        // ------------------------------------

        binding.btnConvert.setOnClickListener {
            Log.d("mine", "可以填入想要讓系統彈出的訊息，這樣能幫助在除錯時可以更好分辨是哪個部分出錯。")
            convertCurrency()
        }
    }

    private fun fetchCurrencyCodes() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create<ApiService>(ApiService::class.java)

                val response = apiService.getSupportedCodes(API_KEY)

                if (response.isSuccessful && response.body()?.result == "success") {
                    val codesList = response.body()?.supported_codes
                    if (codesList != null) {
                        currencyCodes = codesList.map { it[0] }.toTypedArray() // 確保只取出代碼

                        withContext(Dispatchers.Main) {
                            // 1. 設定 AutoCompleteTextView 的 Adapter (使用簡單下拉樣式)
                            val autoCompleteAdapter = ArrayAdapter<String>(
                                this@MainActivity,
                                android.R.layout.simple_dropdown_item_1line,
                                currencyCodes
                            )
                            binding.etFromCurrency.setAdapter(autoCompleteAdapter)
                            binding.etToCurrency.setAdapter(autoCompleteAdapter)
                            binding.etFromCurrency.threshold = 1
                            binding.etToCurrency.threshold = 1

                            // 2. 設定 Spinner 的 Adapter (使用 Spinner 專用樣式)
                            val spinnerAdapter = ArrayAdapter<String>(
                                this@MainActivity,
                                android.R.layout.simple_spinner_item,
                                currencyCodes
                            )
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.spinnerFromCurrency.adapter = spinnerAdapter
                            binding.spinnerToCurrency.adapter = spinnerAdapter

                            Toast.makeText(this@MainActivity, "貨幣列表已更新", Toast.LENGTH_SHORT).show()

                            // --- 1. Spinner 驅動 AutoCompleteTextView (已完成) ---
                            binding.spinnerFromCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    val selected = currencyCodes[position]
                                    // 只有當輸入框內容不同時才更新，避免無限迴圈
                                    if (binding.etFromCurrency.text.toString() != selected) {
                                        binding.etFromCurrency.setText(selected)
                                    }
                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            }

                            binding.spinnerToCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    val selected = currencyCodes[position]
                                    if (binding.etToCurrency.text.toString() != selected) {
                                        binding.etToCurrency.setText(selected)
                                    }
                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            }

                            // --- 2. AutoCompleteTextView 反向驅動 Spinner (新增) ---

                            // 來源幣別輸入監聽
                            binding.etFromCurrency.doOnTextChanged { text, _, _, _ ->
                                val input = text.toString().trim().uppercase()
                                // 在陣列中尋找匹配的索引
                                val index = currencyCodes.indexOf(input)
                                if (index != -1) {
                                    // 如果輸入的是有效代碼，讓 Spinner 跳轉
                                    binding.spinnerFromCurrency.setSelection(index)
                                }
                            }

                            // 目標幣別輸入監聽
                            binding.etToCurrency.doOnTextChanged { text, _, _, _ ->
                                val input = text.toString().trim().uppercase()
                                val index = currencyCodes.indexOf(input)
                                if (index != -1) {
                                    binding.spinnerToCurrency.setSelection(index)
                                }
                            }
                        }
                    }
                } else {
                    Log.e("mine", "無法獲取代碼列表: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("mine", "獲取代碼時發生錯誤", e)
            }
        }
    }

    private fun convertCurrency() {
        Log.d("mine", "開始進行網路請求...");
        val amountStr = binding.etAmount.text.toString().trim()

        // 直接拿取追蹤變數的值，或是為了保險起見，再次即時讀取輸入框
        val fromCurrency = binding.etFromCurrency.text.toString().trim().uppercase()
        val toCurrency = binding.etToCurrency.text.toString().trim().uppercase()

        if (amountStr.isEmpty() || fromCurrency.isEmpty() || toCurrency.isEmpty()) {
            Toast.makeText(this, "請輸入或選擇幣別", Toast.LENGTH_SHORT).show()
            return
        }

        if (amountStr.isEmpty() || fromCurrency.isEmpty() || toCurrency.isEmpty()) {
            Toast.makeText(this, "請填寫所有欄位", Toast.LENGTH_SHORT).show()
            Log.d("mine", "請填寫所有欄位");
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "請輸入有效的金額", Toast.LENGTH_SHORT).show()
            Log.d("mine", "請輸入有效的金額");
            return
        }

        // 使用 Coroutine 在背景執行緒執行網路請求
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create<ApiService>(ApiService::class.java)

                // 呼叫 API
                val response = apiService.getLatestRates(API_KEY, fromCurrency)

                if (response.isSuccessful) {
                    val rateResponse = response.body()
                    if (rateResponse?.result == "success") {
                        val targetRate = rateResponse.conversion_rates[toCurrency]

                        if (targetRate != null) {
                            val result = amount * targetRate
                            // 切換回主執行緒更新 UI
                            withContext(Dispatchers.Main) {
                                binding.tvResult.text = String.format("目標:%.2f %s", result, toCurrency)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@MainActivity, "找不到目標幣別匯率", Toast.LENGTH_SHORT).show()
                                Log.d("mine", "找不到目標幣別匯率");
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "API 錯誤: ${rateResponse?.result}", Toast.LENGTH_LONG).show()
                            Log.d("mine", "API 錯誤");
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "網路請求失敗: ${response.code()}", Toast.LENGTH_LONG).show()
                        val errorCode = response.code()
                        val errorBody = response.errorBody()?.string() // 獲取 API 回傳的錯誤 JSON 內容

                        Log.e("mine", "網路請求失敗，代碼: $errorCode, 內容: $errorBody")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "發生錯誤: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.d("mine", "發生錯誤");
                }
            }
        }
    }
}
