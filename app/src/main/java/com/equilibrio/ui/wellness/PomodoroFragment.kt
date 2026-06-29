package com.equilibrio.ui.wellness

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.equilibrio.databinding.FragmentPomodoroBinding

class PomodoroFragment : Fragment() {

    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!

    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var isBreak = false
    private var workMinutes = 25
    private var breakMinutes = 5
    private var remainingMs = 0L
    private var sessionCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPomodoroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        remainingMs = workMinutes * 60_000L
        updateTimerDisplay(remainingMs)

        binding.btnStartPause.setOnClickListener {
            if (isRunning) pauseTimer() else startTimer()
        }

        binding.btnReset.setOnClickListener { resetTimer() }

        binding.btnWorkMinus.setOnClickListener {
            if (!isRunning) { workMinutes = maxOf(5, workMinutes - 5); resetTimer() }
        }
        binding.btnWorkPlus.setOnClickListener {
            if (!isRunning) { workMinutes = minOf(60, workMinutes + 5); resetTimer() }
        }
        binding.btnBreakMinus.setOnClickListener {
            if (!isRunning) { breakMinutes = maxOf(1, breakMinutes - 1); updateLabels() }
        }
        binding.btnBreakPlus.setOnClickListener {
            if (!isRunning) { breakMinutes = minOf(15, breakMinutes + 1); updateLabels() }
        }

        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }

        updateLabels()
    }

    private fun startTimer() {
        isRunning = true
        binding.btnStartPause.text = "Pausar"
        if (remainingMs <= 0) remainingMs = (if (isBreak) breakMinutes else workMinutes) * 60_000L

        timer = object : CountDownTimer(remainingMs, 1000L) {
            override fun onTick(ms: Long) {
                remainingMs = ms
                updateTimerDisplay(ms)
                val total = (if (isBreak) breakMinutes else workMinutes) * 60_000L
                binding.progressPomodoro.progress = ((total - ms) * 100 / total).toInt()
            }
            override fun onFinish() {
                remainingMs = 0
                if (!isBreak) {
                    sessionCount++
                    binding.tvSessionCount.text = "Sesiones completadas: $sessionCount"
                    isBreak = true
                    binding.tvPhase.text = "🎉 ¡Descanso!"
                } else {
                    isBreak = false
                    binding.tvPhase.text = "📚 Tiempo de estudio"
                }
                isRunning = false
                remainingMs = (if (isBreak) breakMinutes else workMinutes) * 60_000L
                updateTimerDisplay(remainingMs)
                binding.btnStartPause.text = "Iniciar"
                binding.progressPomodoro.progress = 0
            }
        }.start()
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
        binding.btnStartPause.text = "Continuar"
    }

    private fun resetTimer() {
        timer?.cancel()
        isRunning = false
        isBreak = false
        remainingMs = workMinutes * 60_000L
        updateTimerDisplay(remainingMs)
        binding.btnStartPause.text = "Iniciar"
        binding.progressPomodoro.progress = 0
        binding.tvPhase.text = "📚 Tiempo de estudio"
        updateLabels()
    }

    private fun updateTimerDisplay(ms: Long) {
        val min = ms / 60_000
        val sec = (ms % 60_000) / 1000
        binding.tvTimer.text = String.format("%02d:%02d", min, sec)
    }

    private fun updateLabels() {
        binding.tvWorkMinutes.text = "${workMinutes}min"
        binding.tvBreakMinutes.text = "${breakMinutes}min"
        if (!isRunning) {
            remainingMs = workMinutes * 60_000L
            updateTimerDisplay(remainingMs)
        }
    }

    override fun onDestroyView() {
        timer?.cancel()
        super.onDestroyView()
        _binding = null
    }
}
