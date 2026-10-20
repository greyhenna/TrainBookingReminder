package com.example.trainbookingreminder.data.model

data class AppLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String
) {
    companion object {
        val SUPPORTED_LANGUAGES = listOf(
            AppLanguage("en", "English", "English"),
            AppLanguage("hi", "हिंदी", "Hindi"),
            AppLanguage("bn", "বাংলা", "Bengali"),
            AppLanguage("te", "తెలుగు", "Telugu"),
            AppLanguage("mr", "मराठी", "Marathi"),
            AppLanguage("ta", "தமிழ்", "Tamil"),
            AppLanguage("gu", "ગુજરાતી", "Gujarati"),
            AppLanguage("kn", "ಕನ್ನಡ", "Kannada"),
            AppLanguage("ml", "മലയാളം", "Malayalam"),
            AppLanguage("pa", "ਪੰਜਾਬੀ", "Punjabi"),
            AppLanguage("or", "ଓଡ଼ିଆ", "Odia"),
            AppLanguage("as", "অসমীয়া", "Assamese"),
            AppLanguage("ur", "اردو", "Urdu")
        )
    }
}
