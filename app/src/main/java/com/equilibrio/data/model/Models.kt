package com.equilibrio.data.model

data class AppUsageModel(
    val packageName: String,
    val appName: String,
    val minutesUsed: Int,
    val isSocial: Boolean = false
) {
    val formattedTime: String
        get() {
            val h = minutesUsed / 60
            val m = minutesUsed % 60
            return if (h > 0) "${h}h ${m}m" else "${m}m"
        }

    val usagePercent: Int
        get() = minOf(100, minutesUsed * 100 / 120) // relativo a 2h
}

data class Goal(
    val title: String,
    val completed: Boolean
)

data class AppLimit(
    val packageName: String,
    val appName: String,
    val limitMinutes: Int,
    val usedMinutes: Int = 0,
    val enabled: Boolean = true
) {
    val isNearLimit: Boolean get() = usedMinutes >= limitMinutes * 0.8
    val isOverLimit: Boolean get() = usedMinutes >= limitMinutes
}

data class Notification(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "info", // info, warning, success
    val timestamp: Long = 0L,
    val read: Boolean = false
)

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val university: String = "",
    val career: String = "",
    val role: String = "user",
    val streak: Int = 0,
    val createdAt: Long = 0L
)

data class WellnessTip(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val category: String = "bienestar",
    val audience: String = "todos"
)
