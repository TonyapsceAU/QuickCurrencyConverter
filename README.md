# QuickCurrencyConverter 🌍

一個基於 Android Studio 開發的即時匯率轉換器。本專案透過串接 REST API 獲取全球最新匯率數據，並提供流暢的使用者操作介面。

## ✨ 功能亮點

*   **即時匯率查詢**：串接 [ExchangeRate-API](www.exchangerate-api.com)，確保數據的準確性與時效性。
*   **雙向同步介面**：
    *   **AutoCompleteTextView**：支援輸入預測，快速定位貨幣代碼。
    *   **Spinner 下拉清單**：提供完整的貨幣列表供瀏覽選擇。
    *   **智慧聯動**：兩者自動同步，無論修改哪一個，另一個都會自動更新為最新狀態。
*   **自動更新列表**：啟動時自動從 API 下載最新的支援貨幣清單，無需手動更新代碼。
*   **異步處理**：使用 Kotlin Coroutines 確保網路請求不卡頓 UI 執行緒。

## 🛠️ 技術棧 (2026 Android 標準)

*   **語言**：[Kotlin](kotlinlang.org)
*   **網路請求**：[Retrofit 2](square.github.io)
*   **JSON 解析**：[Gson](github.com)
*   **UI 綁定**：[View Binding](developer.android.com)
*   **非同步處理**：[Kotlin Coroutines](kotlinlang.orgdocs/coroutines-overview.html)
*   **佈局**：`ConstraintLayout` (用於構建響應式介面)

## 🚀 快速開始

1.  **複製專案**
    ```bash
    git clone github.com
    ```
2.  **獲取 API 金鑰**
    前往 [ExchangeRate-API](www.exchangerate-api.com) 註冊並取得您的免費 API Key。
3.  **設定金鑰**
    在 `MainActivity.kt` 中找到 `API_KEY` 變數並填入：
    ```kotlin
    private val API_KEY = "您的金鑰貼在這裡"
    ```
4.  **編譯並運行**
    使用 Android Studio 開啟專案，點擊 **Run** 按鈕即可在模擬器或實體裝置上執行。

## 📜 學習筆記 (2026)

在開發此專案的過程中，我掌握了以下技能：
1.  處理 Android 16 (API 36) 的 SDK 相容性問題。
2.  配置 Gradle Kotlin DSL (`.kts`) 的依賴項。
3.  實作雙向 UI 監聽器 (`doOnTextChanged` 與 `onItemSelected`) 以提升體驗。
4.  熟練使用 Git 指令將專案部署至 GitHub。

---
由 [TonyapsceAU](github.com) 開發與維護。
