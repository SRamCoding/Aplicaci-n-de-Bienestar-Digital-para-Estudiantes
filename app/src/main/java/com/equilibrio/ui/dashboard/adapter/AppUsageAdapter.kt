package com.equilibrio.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.equilibrio.data.model.AppUsageModel
import com.equilibrio.databinding.ItemAppUsageBinding

class AppUsageAdapter(private val items: List<AppUsageModel>) :
    RecyclerView.Adapter<AppUsageAdapter.VH>() {

    private val icons = listOf("📸", "🎵", "💬", "🐦", "📺", "🎮", "📱")

    inner class VH(val binding: ItemAppUsageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAppUsageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvAppIcon.text = icons.getOrElse(position) { "📱" }
            tvAppName.text = item.appName
            tvAppTime.text = item.formattedTime
            pbUsage.progress = item.usagePercent
        }
    }
}
