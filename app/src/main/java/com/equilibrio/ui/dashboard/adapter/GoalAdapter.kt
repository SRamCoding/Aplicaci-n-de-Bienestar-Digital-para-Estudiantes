package com.equilibrio.ui.dashboard.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.equilibrio.data.model.Goal
import com.equilibrio.databinding.ItemGoalBinding

class GoalAdapter(
    private val items: List<Goal>,
    private val onToggle: (Goal, Boolean) -> Unit
) : RecyclerView.Adapter<GoalAdapter.VH>() {

    inner class VH(val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val goal = items[position]
        holder.binding.apply {
            cbGoal.setOnCheckedChangeListener(null)
            cbGoal.isChecked = goal.completed
            tvGoalTitle.text = goal.title
            if (goal.completed) {
                tvGoalTitle.paintFlags = tvGoalTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvGoalTitle.alpha = 0.5f
            } else {
                tvGoalTitle.paintFlags = tvGoalTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvGoalTitle.alpha = 1f
            }
            cbGoal.setOnCheckedChangeListener { _, checked ->
                onToggle(goal, checked)
            }
        }
    }
}
