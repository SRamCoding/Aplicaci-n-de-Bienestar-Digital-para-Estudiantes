package com.equilibrio.ui.dashboard

import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.equilibrio.data.model.AppUsageModel
import com.equilibrio.data.model.Goal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _totalUsageToday = MutableLiveData<Int>()
    val totalUsageToday: LiveData<Int> = _totalUsageToday

    private val _goalPercentage = MutableLiveData<Int>()
    val goalPercentage: LiveData<Int> = _goalPercentage

    private val _topApps = MutableLiveData<List<AppUsageModel>>()
    val topApps: LiveData<List<AppUsageModel>> = _topApps

    private val _goals = MutableLiveData<List<Goal>>()
    val goals: LiveData<List<Goal>> = _goals

    private val _dailyTip = MutableLiveData<String>()
    val dailyTip: LiveData<String> = _dailyTip

    private val _alertMessage = MutableLiveData<String?>()
    val alertMessage: LiveData<String?> = _alertMessage

    private val tips = listOf(
        "Activa el modo 'No molestar' durante las horas de estudio para aumentar tu concentración.",
        "Practica la regla 20-20-20: cada 20 minutos, mira a 6 metros de distancia por 20 segundos.",
        "Evita revisar el teléfono la primera hora de la mañana para empezar el día con claridad.",
        "Programa descansos de 5 minutos cada hora para mantener tu productividad alta.",
        "Desactivar las notificaciones no esenciales puede reducir las interrupciones hasta un 60%."
    )

    fun loadData() {
        loadUserName()
        loadUsageStats()
        loadGoals()
        _dailyTip.value = tips[(System.currentTimeMillis() / 86400000 % tips.size).toInt()]
    }

    private fun loadUserName() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(uid).get().await()
                _userName.value = doc.getString("name") ?: "Estudiante"
            } catch (e: Exception) {
                _userName.value = "Estudiante"
            }
        }
    }

    private fun loadUsageStats() {
        viewModelScope.launch(Dispatchers.IO) {
            val usm = getApplication<Application>()
                .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startTime = cal.timeInMillis
            val endTime = System.currentTimeMillis()

            val stats = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime
            )

            val socialPackages = setOf(
                "com.instagram.android",
                "com.zhiliaoapp.musically",
                "com.twitter.android",
                "com.facebook.katana",
                "com.snapchat.android"
            )

            val appList = stats
                .filter { it.totalTimeInForeground > 0 }
                .sortedByDescending { it.totalTimeInForeground }
                .take(5)
                .map { stat ->
                    val pm = getApplication<Application>().packageManager
                    val appName = try {
                        pm.getApplicationLabel(
                            pm.getApplicationInfo(stat.packageName, 0)
                        ).toString()
                    } catch (e: Exception) {
                        stat.packageName.substringAfterLast(".")
                    }
                    val minutes = (stat.totalTimeInForeground / 60000).toInt()
                    AppUsageModel(
                        packageName = stat.packageName,
                        appName = appName,
                        minutesUsed = minutes,
                        isSocial = stat.packageName in socialPackages
                    )
                }

            val totalMin = appList.sumOf { it.minutesUsed }
            val socialMin = appList.filter { it.isSocial }.sumOf { it.minutesUsed }
            val limitMin = 120 // 2 horas por defecto

            _topApps.postValue(appList)
            _totalUsageToday.postValue(totalMin)
            _goalPercentage.postValue(
                if (totalMin > 0) minOf(100, (limitMin * 100 / totalMin)) else 100
            )

            if (socialMin >= limitMin * 0.8) {
                val h = socialMin / 60
                val m = socialMin % 60
                _alertMessage.postValue(
                    "Llevas ${h}h ${m}m en redes sociales hoy. Tu límite es ${limitMin / 60}h."
                )
            } else {
                _alertMessage.postValue(null)
            }
        }
    }

    private fun loadGoals() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date())
                val doc = db.collection("users").document(uid)
                    .collection("goals").document(today).get().await()

                val defaultGoals = listOf(
                    Goal("Descanso 5 min cada hora", doc.getBoolean("rest") ?: false),
                    Goal("Máx. 2h en redes sociales", doc.getBoolean("social") ?: false),
                    Goal("Leer 30 min sin pantalla", doc.getBoolean("reading") ?: false),
                    Goal("Ejercicio 20 min", doc.getBoolean("exercise") ?: false)
                )
                _goals.value = defaultGoals
            } catch (e: Exception) {
                _goals.value = listOf(
                    Goal("Descanso 5 min cada hora", false),
                    Goal("Máx. 2h en redes sociales", false),
                    Goal("Leer 30 min sin pantalla", false),
                    Goal("Ejercicio 20 min", false)
                )
            }
        }
    }

    fun toggleGoal(goal: Goal, checked: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())
        val key = when (goal.title) {
            "Descanso 5 min cada hora"  -> "rest"
            "Máx. 2h en redes sociales" -> "social"
            "Leer 30 min sin pantalla"  -> "reading"
            "Ejercicio 20 min"          -> "exercise"
            else -> return
        }
        db.collection("users").document(uid)
            .collection("goals").document(today)
            .set(mapOf(key to checked), com.google.firebase.firestore.SetOptions.merge())

        val current = _goals.value?.toMutableList() ?: return
        val idx = current.indexOfFirst { it.title == goal.title }
        if (idx >= 0) {
            current[idx] = goal.copy(completed = checked)
            _goals.value = current
        }

        val done = current.count { it.completed }
        _goalPercentage.value = (done * 100 / current.size)
    }
}
