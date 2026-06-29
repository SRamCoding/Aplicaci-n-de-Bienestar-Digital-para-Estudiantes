package com.equilibrio.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.equilibrio.R
import com.equilibrio.databinding.FragmentStatsBinding
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPeriodButtons()
        setupPieChart()
        setupHourlyChart()
        observeViewModel()
    }

    private fun setupPeriodButtons() {
        listOf(binding.btnToday, binding.btnWeek, binding.btnMonth).forEach { btn ->
            btn.setOnClickListener {
                listOf(binding.btnToday, binding.btnWeek, binding.btnMonth).forEach {
                    it.isSelected = false
                }
                btn.isSelected = true
                when (btn.id) {
                    R.id.btnToday -> viewModel.loadStats("today")
                    R.id.btnWeek  -> viewModel.loadStats("week")
                    R.id.btnMonth -> viewModel.loadStats("month")
                }
            }
        }
        binding.btnToday.isSelected = true
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            isDrawHoleEnabled = true
            holeRadius = 55f
            setHoleColor(resources.getColor(android.R.color.transparent, null))
            setUsePercentValues(true)
            legend.isEnabled = false
            description.isEnabled = false
            setEntryLabelTextSize(0f)
            animateY(1000)
        }
    }

    private fun setupHourlyChart() {
        // Datos de ejemplo para horas de uso
        val entries = (8..20).mapIndexed { i, _ ->
            BarEntry(i.toFloat(), floatArrayOf(
                0f, 5f, 10f, 8f, 25f, 40f, 60f, 55f, 30f, 20f, 15f, 8f, 5f
            )[i])
        }
        val dataSet = BarDataSet(entries, "Uso por hora").apply {
            color = resources.getColor(R.color.teal_200, null)
            setDrawValues(false)
        }
        binding.barChartHourly.apply {
            data = BarData(dataSet)
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            xAxis.labelCount = 5
            animateY(600)
            invalidate()
        }
    }

    private fun observeViewModel() {
        viewModel.totalMinutes.observe(viewLifecycleOwner) { min ->
            val h = min / 60; val m = min % 60
            binding.tvTotalTime.text = "${h}h ${m}m"
        }
        viewModel.unlockCount.observe(viewLifecycleOwner) { count ->
            binding.tvUnlocks.text = count.toString()
        }
        viewModel.changePercent.observe(viewLifecycleOwner) { pct ->
            binding.tvChange.text = if (pct >= 0) "↑ $pct%" else "↓ ${-pct}%"
            binding.tvChange.setTextColor(
                resources.getColor(
                    if (pct > 0) R.color.red_400 else R.color.teal_600, null
                )
            )
        }
        viewModel.streak.observe(viewLifecycleOwner) { streak ->
            binding.tvStreak.text = "$streak"
        }
        viewModel.categoryData.observe(viewLifecycleOwner) { categories ->
            val entries = categories.map { PieEntry(it.second, it.first) }
            val colors = listOf(
                resources.getColor(R.color.amber_400, null),
                resources.getColor(R.color.blue_400, null),
                resources.getColor(R.color.teal_400, null),
                resources.getColor(R.color.gray_200, null)
            )
            val dataSet = PieDataSet(entries, "").apply {
                this.colors = colors
                sliceSpace = 2f
            }
            binding.pieChart.data = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter(binding.pieChart))
                setValueTextSize(0f)
            }
            binding.pieChart.invalidate()
        }
        viewModel.loadStats("today")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
