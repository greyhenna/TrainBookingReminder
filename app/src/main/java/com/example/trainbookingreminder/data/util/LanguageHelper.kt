package com.example.trainbookingreminder.data.util

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.trainbookingreminder.data.model.AppLanguage

object LanguageHelper {

    /**
     * Changes the application's locale.
     * Uses LocaleManager API on Android 13+ (API 33+) and AppCompatDelegate for API < 33.
     */
    fun setAppLanguage(context: Context, languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as? LocaleManager
            localeManager?.applicationLocales = LocaleList.forLanguageTags(languageCode)
        } else {
            val localeListCompat = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(localeListCompat)
        }
    }

    /**
     * Retrieves the currently active app language code.
     */
    fun getCurrentLanguageCode(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as? LocaleManager
            localeManager?.applicationLocales?.toLanguageTags()
                ?.split(",")
                ?.firstOrNull()
                ?.takeIf { it.isNotEmpty() } ?: "en"
        } else {
            val locales = AppCompatDelegate.getApplicationLocales()
            locales.toLanguageTags()
                .split(",")
                .firstOrNull()
                .takeIf { !it.isNullOrEmpty() } ?: "en"
        }
    }

    fun getLanguageByCode(code: String): AppLanguage {
        return AppLanguage.SUPPORTED_LANGUAGES.firstOrNull { it.code.equals(code, ignoreCase = true) }
            ?: AppLanguage.SUPPORTED_LANGUAGES.first()
    }
}
