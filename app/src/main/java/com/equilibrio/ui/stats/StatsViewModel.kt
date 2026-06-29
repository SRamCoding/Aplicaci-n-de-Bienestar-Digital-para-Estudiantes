package com.equilibrio.ui.stats

import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _totalMinutes = MutableLiveData<Int>()
    val totalMinutes: LiveData<Int> = _totalMinutes

    private val _unlockCount = MutableLiveData<Int>()
    val unlockCount: LiveData<Int> = _unlockCount

    private val _changePercent = MutableLiveData<Int>()
    val changePercent: LiveData<Int> = _changePercent

    private val _streak = MutableLiveData<Int>()
    val streak: LiveData<Int> = _streak

    private val _categoryData = MutableLiveData<List<Pair<String, Float>>>()
    val categoryData: LiveData<List<Pair<String, Float>>> = _categoryData

    fun loadStats(period: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val usm = getApplication<Application>()
                .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            val (startTime, endTime) = getPeriodRange(period)
            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

            val totalMs = stats.sumOf { it.totalTimeInForeground }
            val totalMin = (totalMs / 60000).toInt()
            _totalMinutes.postValue(totalMin)
            _unlockCount.postValue((10..25).random()) // simulado
            _changePercent.postValue((-20..30).random()) // simulado

            // Categorías
            val social = stats.filter {
                it.packageName.contains("instagram") || it.packageName.contains("tiktok") ||
                it.packageName.contains("twitter") || it.packageName.contains("facebook") ||
                it.packageName.contains("snapchat")
            }.sumOf { it.totalTimeInForeground }

            val entertainment = stats.filter {
                it.packageName.contains("youtube") || it.packageName.contains("netflix") ||
                it.packageName.contains("spotify")
            }.sumOf { it.totalTimeInForeground }

            val messaging = stats.filter {
                it.packageName.contains("whatsapp") || it.packageName.contains("telegram")
            }.sumOf { it.totalTimeInForeground }

            val total = maxOf(totalMs, 1L)
            _categoryData.postValue(listOf(
                "Redes sociales"  to (social * 100f / total),
                "Entretenimiento" to (entertainment * 100f / total),
                "Mensajería"      to (messaging * 100f / total),
                "Otros"           to ((total - social - entertainment - messaging).coerceAtLeast(0) * 100f / total)
            ))

            loadStreak()
        }
    }

    private suspend fun loadStreak() {
        val uid = auth.currentUser?.uid ?: run { _streak.postValue(0); return }
        try {
            val doc = db.collection("users").document(uid).get().await()
            _streak.postValue((doc.getLong("streak") ?: 0L).toInt())
        } catch (e: Exception) {
            _streak.postValue(0)
        }
    }

    private fun getPeriodRange(period: String): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val end = cal.timeInMillis
        when (period) {
            "week" -> cal.add(Calendar.DAY_OF_YEAR, -7)
            "month" -> cal.add(Calendar.MONTH, -1)
            else -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
            }
        }
        return Pair(cal.timeInMillis, end)
    }
}
