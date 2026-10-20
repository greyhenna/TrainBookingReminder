package com.example.trainbookingreminder.data.model

import android.content.Context
import androidx.annotation.StringRes
import com.example.trainbookingreminder.R
import java.time.LocalTime

/**
 * Represents the three IRCTC booking quota types with localized resource IDs.
 */
enum class BookingType(
    @param:StringRes val titleRes: Int,
    @param:StringRes val subtitleRes: Int,
    val openingTime: LocalTime,
    val advanceDays: Long,
    val reminderLeadMinutes: Long = 15L
) {
    GENERAL(
        titleRes = R.string.quota_general_title,
        subtitleRes = R.string.quota_general_subtitle,
        openingTime = LocalTime.of(8, 0),
        advanceDays = 60
    ),
    AC_TATKAL(
        titleRes = R.string.quota_ac_tatkal_title,
        subtitleRes = R.string.quota_ac_tatkal_subtitle,
        openingTime = LocalTime.of(10, 0),
        advanceDays = 1
    ),
    SL_TATKAL(
        titleRes = R.string.quota_sl_tatkal_title,
        subtitleRes = R.string.quota_sl_tatkal_subtitle,
        openingTime = LocalTime.of(11, 0),
        advanceDays = 1
    );

    fun getTitle(context: Context): String = context.getString(titleRes)
    fun getSubtitle(context: Context): String = context.getString(subtitleRes)
}
