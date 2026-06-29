package com.equilibrio.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.equilibrio.data.model.Notification
import com.equilibrio.databinding.ItemNotificationBinding
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private val items: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.VH>() {

    inner class VH(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val n = items[position]
        holder.binding.apply {
            tvNotifTitle.text = n.title
            tvNotifBody.text  = n.body
            tvNotifTime.text  = formatTime(n.timestamp)
            tvNotifIcon.text  = when (n.type) {
                "warning" -> "⚠️"
                "success" -> "✅"
                else      -> "ℹ️"
            }
        }
    }

    private fun formatTime(ts: Long): String {
        if (ts == 0L) return ""
        val diff = System.currentTimeMillis() - ts
        return when {
            diff < 3_600_000  -> "Hace ${diff / 60_000} min"
            diff < 86_400_000 -> "Hace ${diff / 3_600_000} horas"
            else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ts))
        }
    }
}
