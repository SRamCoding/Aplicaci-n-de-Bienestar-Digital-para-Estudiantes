package com.equilibrio.ui.wellness

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.equilibrio.databinding.FragmentBreathingBinding

class BreathingFragment : Fragment() {

    private var _binding: FragmentBreathingBinding? = null
    private val binding get() = _binding!!

    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var currentPhase = 0
    private var currentCycle = 1
    private val totalCycles = 4

    // fases: Inhala 4s, Sostén 7s, Exhala 8s
    private val phases = listOf(
        Triple("Inhala", 4, 1.3f),
        Triple("Sostén", 7, 1.3f),
        Triple("Exhala", 8, 1.0f)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBreathingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updatePhaseUI()

        binding.btnBreath.setOnClickListener {
            if (!isRunning) startBreathing() else pauseBreathing()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun startBreathing() {
        isRunning = true
        binding.btnBreath.text = "Pausar"
        runPhase()
    }

    private fun pauseBreathing() {
        isRunning = false
        timer?.cancel()
        binding.btnBreath.text = "Continuar"
    }

    private fun runPhase() {
        if (!isRunning) return
        val (label, seconds, _) = phases[currentPhase]
        updatePhaseUI()
        animateCircle(currentPhase)

        timer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isRunning) { cancel(); return }
                binding.tvBreathCount.text = ((millisUntilFinished / 1000) + 1).toString()
            }
            override fun onFinish() {
                if (!isRunning) return
                binding.tvBreathCount.text = "0"
                nextPhase()
            }
        }.start()
    }

    private fun nextPhase() {
        currentPhase++
        if (currentPhase >= phases.size) {
            currentPhase = 0
            currentCycle++
        }
        if (currentCycle > totalCycles) {
            finishExercise()
            return
        }
        binding.tvCycle.text = "Ciclo $currentCycle de $totalCycles"
        runPhase()
    }

    private fun updatePhaseUI() {
        val (label, seconds, _) = phases[currentPhase]
        binding.tvBreathLabel.text = label
        binding.tvBreathCount.text = seconds.toString()
        binding.tvCycle.text = "Ciclo $currentCycle de $totalCycles"
    }

    private fun animateCircle(phase: Int) {
        val scale = if (phase == 2) 1.0f else 1.25f
        val duration = phases[phase].second * 1000L
        val animator = AnimatorSet()
        val scaleX = ObjectAnimator.ofFloat(binding.circleBreath, "scaleX", binding.circleBreath.scaleX, scale)
        val scaleY = ObjectAnimator.ofFloat(binding.circleBreath, "scaleY", binding.circleBreath.scaleY, scale)
        scaleX.duration = duration
        scaleY.duration = duration
        animator.playTogether(scaleX, scaleY)
        animator.start()
    }

    private fun finishExercise() {
        isRunning = false
        binding.tvBreathLabel.text = "¡Completado!"
        binding.tvBreathCount.text = "✓"
        binding.btnBreath.text = "Repetir"
        currentPhase = 0
        currentCycle = 1
        binding.circleBreath.animate().scaleX(1f).scaleY(1f).setDuration(500).start()
    }

    override fun onDestroyView() {
        timer?.cancel()
        super.onDestroyView()
        _binding = null
    }
}
