package com.equilibrio.data

import android.app.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.equilibrio.MainActivity
import com.equilibrio.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.Calendar

class UsageMonitorService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val db    = FirebaseFirestore.getInstance()
    private val auth  = FirebaseAuth.getInstance()
    private val CHANNEL_ID = "equilibrio_monitor"
    private val NOTIF_ID   = 1001

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIF_ID, buildForegroundNotification())
        startMonitoring()
    }

    private fun startMonitoring() {
        scope.launch {
            while (isActive) {
                checkUsageLimits()
                delay(5 * 60_000L) // cada 5 minutos
            }
        }
    }

    private suspend fun checkUsageLimits() {
        val uid = auth.currentUser?.uid ?: return
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val stats = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            cal.timeInMillis, System.currentTimeMillis()
        )

        // Cargar límites guardados en Firestore
        val limitsDoc = try {
            db.collection("users").document(uid)
                .collection("limits").document("apps").get()
                .await()
        } catch (e: Exception) { return }

        val socialPackages = mapOf(
            "com.instagram.android"  to (limitsDoc.getLong("instagram") ?: 120L),
            "com.zhiliaoapp.musically" to (limitsDoc.getLong("tiktok") ?: 60L),
            "com.twitter.android"    to (limitsDoc.getLong("twitter") ?: 45L)
        )

        for ((pkg, limitMin) in socialPackages) {
            val stat = stats.find { it.packageName == pkg } ?: continue
            val usedMin = stat.totalTimeInForeground / 60_000L

            if (usedMin >= limitMin * 0.8 && usedMin < limitMin) {
                val pm = packageManager
                val appName = try {
                    pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
                } catch (e: Exception) { pkg }
                sendAlert("Límite próximo – $appName",
                    "Llevas ${usedMin}min de ${limitMin}min permitidos hoy.")
            } else if (usedMin >= limitMin) {
                val appName = try {
                    packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(pkg, 0)).toString()
                } catch (e: Exception) { pkg }
                sendAlert("¡Límite alcanzado! – $appName",
                    "Has superado tu límite diario de ${limitMin}min en $appName.")
            }
        }
    }

    private fun sendAlert(title: String, text: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(System.currentTimeMillis().toInt(), notif)

        // Guardar en Firestore para mostrar en pantalla de notificaciones
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("notifications").add(
            mapOf(
                "title"     to title,
                "body"      to text,
                "type"      to "warning",
                "timestamp" to System.currentTimeMillis(),
                "read"      to false
            )
        )
    }

    private fun buildForegroundNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentTitle("Equilibrio activo")
            .setContentText("Monitoreando tu uso digital")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pi)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "Monitor de uso",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Servicio de monitoreo en segundo plano" }
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }

    private fun com.google.android.gms.tasks.Task<com.google.firebase.firestore.DocumentSnapshot>.await()
        = kotlinx.coroutines.tasks.await(this)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
