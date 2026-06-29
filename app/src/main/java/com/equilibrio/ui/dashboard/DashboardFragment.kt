package com.equilibrio.ui.dashboard

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.equilibrio.R
import com.equilibrio.databinding.FragmentDashboardBinding
import com.equilibrio.ui.dashboard.adapter.AppUsageAdapter
import com.equilibrio.ui.dashboard.adapter.GoalAdapter
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkUsagePermission()
        setupWeeklyChart()
        observeViewModel()

        binding.ivNotification.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_notificationsFragment)
        }
    }

    private fun checkUsagePermission() {
        val appOps = requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            requireContext().packageName
        )
        if (mode != AppOpsManager.MODE_ALLOWED) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    private fun setupWeeklyChart() {
        val entries = listOf(
            BarEntry(0f, 2.1f), BarEntry(1f, 3.4f), BarEntry(2f, 1.8f),
            BarEntry(3f, 3.6f), BarEntry(4f, 4.0f), BarEntry(5f, 3.2f),
            BarEntry(6f, 1.5f)
        )
        val dataSet = BarDataSet(entries, "Horas de uso").apply {
            color = resources.getColor(R.color.teal_400, null)
            setDrawValues(false)
        }
        val days = listOf("L", "M", "X", "J", "V", "S", "D")
        binding.barChart.apply {
            data = BarData(dataSet).apply { barWidth = 0.5f }
            xAxis.valueFormatter = IndexAxisValueFormatter(days)
            xAxis.granularity = 1f
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            setDrawGridBackground(false)
            animateY(800)
            invalidate()
        }
    }

    private fun observeViewModel() {
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.tvGreeting.text = "Hola, ${name.split(" ").first()} 👋"
        }

        viewModel.totalUsageToday.observe(viewLifecycleOwner) { minutes ->
            val h = minutes / 60
            val m = minutes % 60
            binding.tvTotalUsage.text = "${h}h ${m}m"
        }

        viewModel.goalPercentage.observe(viewLifecycleOwner) { pct ->
            binding.tvGoalPct.text = "$pct%"
            binding.progressGoal.progress = pct
        }

        viewModel.topApps.observe(viewLifecycleOwner) { apps ->
            binding.rvTopApps.layoutManager = LinearLayoutManager(context)
            binding.rvTopApps.adapter = AppUsageAdapter(apps)
        }

        viewModel.goals.observe(viewLifecycleOwner) { goals ->
            binding.rvGoals.layoutManager = LinearLayoutManager(context)
            binding.rvGoals.adapter = GoalAdapter(goals) { goal, checked ->
                viewModel.toggleGoal(goal, checked)
            }
        }

        viewModel.dailyTip.observe(viewLifecycleOwner) { tip ->
            binding.tvTip.text = tip
        }

        viewModel.alertMessage.observe(viewLifecycleOwner) { alert ->
            if (alert.isNullOrEmpty()) {
                binding.cardAlert.visibility = View.GONE
            } else {
                binding.cardAlert.visibility = View.VISIBLE
                binding.tvAlert.text = alert
            }
        }

        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
