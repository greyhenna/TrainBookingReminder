package com.example.trainbookingreminder.ui.viewmodel

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainbookingreminder.R
import com.example.trainbookingreminder.data.model.AppLanguage
import com.example.trainbookingreminder.data.model.BookingInfo
import com.example.trainbookingreminder.data.model.BookingType
import com.example.trainbookingreminder.data.model.ThemeMode
import com.example.trainbookingreminder.data.repository.ThemePreferencesRepository
import com.example.trainbookingreminder.data.util.CalendarHelper
import com.example.trainbookingreminder.data.util.IrctcCalculator
import com.example.trainbookingreminder.data.util.LanguageHelper
import com.example.trainbookingreminder.notification.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class MainUiState(
    val journeyDate: LocalDate = LocalDate.now().plusDays(15),
    val journeyDateFormatted: String = LocalDate.now().plusDays(15).format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")),
    val bookings: List<BookingInfo> = IrctcCalculator.calculateAllBookings(LocalDate.now().plusDays(15)),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val currentLanguage: AppLanguage = AppLanguage.SUPPORTED_LANGUAGES.first(),
    val userMessage: String? = null,
    val showExactAlarmSettingsDialog: Boolean = false
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var themeRepository: ThemePreferencesRepository? = null

    init {
        val initialDate = _uiState.value.journeyDate
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")
        val formattedJourneyDate = initialDate.format(dateFormatter)
        val initialBookings = IrctcCalculator.calculateAllBookings(initialDate)
        _uiState.update {
            it.copy(
                journeyDateFormatted = formattedJourneyDate,
                bookings = initialBookings
            )
        }
    }

    fun initialize(context: Context) {
        val repo = themeRepository ?: ThemePreferencesRepository(context.applicationContext).also {
            themeRepository = it
        }

        viewModelScope.launch {
            repo.themeModeFlow.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }

        val langCode = LanguageHelper.getCurrentLanguageCode(context)
        val currentLang = LanguageHelper.getLanguageByCode(langCode)
        _uiState.update { it.copy(currentLanguage = currentLang) }

        val currentDate = _uiState.value.journeyDate
        updateBookingsForDate(currentDate, context)
    }

    fun selectThemeMode(mode: ThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
        viewModelScope.launch {
            themeRepository?.setThemeMode(mode)
        }
    }

    fun selectLanguage(language: AppLanguage, context: Context) {
        _uiState.update { it.copy(currentLanguage = language) }
        LanguageHelper.setAppLanguage(context, language.code)
        updateBookingsForDate(_uiState.value.journeyDate, context)
    }

    fun selectJourneyDate(newDate: LocalDate, context: Context) {
        _uiState.update { it.copy(journeyDate = newDate) }
        updateBookingsForDate(newDate, context)
    }

    fun toggleNotification(
        bookingInfo: BookingInfo,
        enable: Boolean,
        context: Context
    ) {
        if (enable) {
            val result = AlarmScheduler.scheduleNotification(
                context = context,
                bookingInfo = bookingInfo,
                journeyDateFormatted = _uiState.value.journeyDateFormatted
            )

            when (result) {
                is AlarmScheduler.ScheduleResult.Success -> {
                    _uiState.update { it.copy(userMessage = result.message) }
                    refreshNotificationStates(context)
                }
                is AlarmScheduler.ScheduleResult.NeedsExactAlarmPermission -> {
                    _uiState.update {
                        it.copy(
                            showExactAlarmSettingsDialog = true,
                            userMessage = context.getString(R.string.exact_alarm_needed_msg)
                        )
                    }
                }
                is AlarmScheduler.ScheduleResult.Error -> {
                    _uiState.update { it.copy(userMessage = result.message) }
                }
            }
        } else {
            AlarmScheduler.cancelNotification(context, bookingInfo.type)
            val cancelledMsg = context.getString(R.string.reminder_cancelled, bookingInfo.type.getTitle(context))
            _uiState.update { it.copy(userMessage = cancelledMsg) }
            refreshNotificationStates(context)
        }
    }

    fun addToCalendar(bookingInfo: BookingInfo, context: Context) {
        CalendarHelper.addBookingToCalendar(
            context = context,
            bookingInfo = bookingInfo,
            journeyDateFormatted = _uiState.value.journeyDateFormatted
        )
    }

    fun refreshNotificationStates(context: Context) {
        val currentDate = _uiState.value.journeyDate
        updateBookingsForDate(currentDate, context)
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun dismissExactAlarmDialog() {
        _uiState.update { it.copy(showExactAlarmSettingsDialog = false) }
    }

    private fun updateBookingsForDate(journeyDate: LocalDate, context: Context) {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)?.applicationLocales?.get(0) ?: Locale.getDefault()
        } else {
            AppCompatDelegate.getApplicationLocales().get(0) ?: Locale.getDefault()
        }

        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", locale)
        val formattedJourneyDate = journeyDate.format(dateFormatter)

        val scheduledMap = mutableMapOf<BookingType, Boolean>()
        BookingType.entries.forEach { type ->
            val info = IrctcCalculator.calculateBookingInfo(journeyDate, type)
            val timeMillis = info.notificationDateTime.toInstant().toEpochMilli()
            scheduledMap[type] = AlarmScheduler.isNotificationScheduled(context, type, timeMillis)
        }

        val updatedBookings = IrctcCalculator.calculateAllBookings(
            journeyDate = journeyDate,
            scheduledMap = scheduledMap
        )

        _uiState.update {
            it.copy(
                journeyDateFormatted = formattedJourneyDate,
                bookings = updatedBookings
            )
        }
    }
}
