package com.equilibrio.ui.limits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.equilibrio.databinding.FragmentLimitsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LimitsFragment : Fragment() {

    private var _binding: FragmentLimitsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Límites en minutos por app
    private val limits = mutableMapOf(
        "instagram" to 120,
        "tiktok"    to 60,
        "twitter"   to 45
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLimitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadLimitsFromFirestore()
        setupButtons()
    }

    private fun loadLimitsFromFirestore() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("limits").document("apps")
            .get()
            .addOnSuccessListener { doc ->
                limits["instagram"] = doc.getLong("instagram")?.toInt() ?: 120
                limits["tiktok"]    = doc.getLong("tiktok")?.toInt()    ?: 60
                limits["twitter"]   = doc.getLong("twitter")?.toInt()   ?: 45
                updateUI()
            }
    }

    private fun updateUI() {
        binding.tvInstagramLimit.text = formatMinutes(limits["instagram"] ?: 120)
        binding.tvTiktokLimit.text    = formatMinutes(limits["tiktok"]    ?: 60)
        binding.tvTwitterLimit.text   = formatMinutes(limits["twitter"]   ?: 45)
    }

    private fun formatMinutes(min: Int): String {
        return if (min >= 60) "${min / 60}h ${min % 60}m" else "${min}m"
    }

    private fun setupButtons() {
        // Instagram
        binding.btnInstagramMinus.setOnClickListener {
            limits["instagram"] = maxOf(15, (limits["instagram"] ?: 120) - 15)
            updateUI()
        }
        binding.btnInstagramPlus.setOnClickListener {
            limits["instagram"] = minOf(480, (limits["instagram"] ?: 120) + 15)
            updateUI()
        }

        // TikTok
        binding.btnTiktokMinus.setOnClickListener {
            limits["tiktok"] = maxOf(15, (limits["tiktok"] ?: 60) - 15)
            updateUI()
        }
        binding.btnTiktokPlus.setOnClickListener {
            limits["tiktok"] = minOf(480, (limits["tiktok"] ?: 60) + 15)
            updateUI()
        }

        // Twitter
        binding.btnTwitterMinus.setOnClickListener {
            limits["twitter"] = maxOf(15, (limits["twitter"] ?: 45) - 15)
            updateUI()
        }
        binding.btnTwitterPlus.setOnClickListener {
            limits["twitter"] = minOf(480, (limits["twitter"] ?: 45) + 15)
            updateUI()
        }

        // Guardar
        binding.btnSaveLimits.setOnClickListener {
            saveLimits()
        }
    }

    private fun saveLimits() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("limits").document("apps")
            .set(limits)
            .addOnSuccessListener {
                Toast.makeText(context, "✅ Límites guardados correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al guardar. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
