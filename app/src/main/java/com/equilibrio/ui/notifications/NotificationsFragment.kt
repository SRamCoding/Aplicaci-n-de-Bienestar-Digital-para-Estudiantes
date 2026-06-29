package com.equilibrio.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.equilibrio.data.model.Notification
import com.equilibrio.databinding.FragmentNotificationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        loadNotifications()
    }

    private fun loadNotifications() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { doc ->
                    Notification(
                        id        = doc.id,
                        title     = doc.getString("title")     ?: "",
                        body      = doc.getString("body")      ?: "",
                        type      = doc.getString("type")      ?: "info",
                        timestamp = doc.getLong("timestamp")   ?: 0L,
                        read      = doc.getBoolean("read")     ?: false
                    )
                }.ifEmpty { demoNotifications() }

                binding.rvNotifications.layoutManager = LinearLayoutManager(context)
                binding.rvNotifications.adapter = NotificationAdapter(list)
                binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                val demo = demoNotifications()
                binding.rvNotifications.layoutManager = LinearLayoutManager(context)
                binding.rvNotifications.adapter = NotificationAdapter(demo)
            }
    }

    private fun demoNotifications(): List<Notification> = listOf(
        Notification("1", "Límite próximo – Instagram",
            "Llevas 1h 45min. Tu límite diario es 2h. Considera tomar un descanso.",
            "warning", System.currentTimeMillis() - 720_000),
        Notification("2", "Recordatorio de descanso",
            "Llevas 55 min estudiando. ¡Toma 5 minutos para estirarte!",
            "info", System.currentTimeMillis() - 3_600_000),
        Notification("3", "¡Meta cumplida!",
            "Completaste tu meta de descanso cada hora. ¡Excelente!",
            "success", System.currentTimeMillis() - 7_200_000),
        Notification("4", "Nuevo consejo de bienestar",
            "¿Sabías que 30 min de ejercicio mejoran la memoria? Revísalo en Bienestar.",
            "info", System.currentTimeMillis() - 86_400_000)
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
