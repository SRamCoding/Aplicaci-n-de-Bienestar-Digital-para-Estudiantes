package com.equilibrio.ui.wellness

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.equilibrio.R
import com.equilibrio.databinding.FragmentWellnessBinding

class WellnessFragment : Fragment() {

    private var _binding: FragmentWellnessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWellnessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardBreathing.setOnClickListener {
            findNavController().navigate(R.id.action_wellnessFragment_to_breathingFragment)
        }

        binding.cardPomodoro.setOnClickListener {
            findNavController().navigate(R.id.action_wellnessFragment_to_pomodoroFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
