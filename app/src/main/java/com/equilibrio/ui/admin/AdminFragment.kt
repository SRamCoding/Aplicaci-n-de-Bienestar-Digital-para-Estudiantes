package com.equilibrio.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.equilibrio.data.model.User
import com.equilibrio.data.model.WellnessTip
import com.equilibrio.databinding.FragmentAdminBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStats()
        loadUsers()

        binding.btnPublishTip.setOnClickListener { publishTip() }
        binding.btnSearchUser.setOnClickListener { searchUser() }
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun loadStats() {
        db.collection("users").get()
            .addOnSuccessListener { snap ->
                binding.tvTotalUsers.text = snap.size().toString()
                val today = System.currentTimeMillis() - 86_400_000L
                val newToday = snap.documents.count { (it.getLong("createdAt") ?: 0L) > today }
                binding.tvNewToday.text = "+$newToday"
            }
    }

    private fun loadUsers() {
        db.collection("users").limit(20).get()
            .addOnSuccessListener { snap ->
                val users = snap.documents.mapNotNull { doc ->
                    User(
                        uid    = doc.id,
                        name   = doc.getString("name")  ?: "Sin nombre",
                        email  = doc.getString("email") ?: "",
                        streak = doc.getLong("streak")?.toInt() ?: 0,
                        role   = doc.getString("role")  ?: "user"
                    )
                }
                binding.rvUsers.layoutManager = LinearLayoutManager(context)
                binding.rvUsers.adapter = UserAdapter(users) { user ->
                    Toast.makeText(context, "Usuario: ${user.name}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun searchUser() {
        val query = binding.etSearchUser.text.toString().trim()
        if (query.isEmpty()) { loadUsers(); return }

        db.collection("users")
            .orderBy("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener { snap ->
                val users = snap.documents.mapNotNull { doc ->
                    User(
                        uid   = doc.id,
                        name  = doc.getString("name")  ?: "Sin nombre",
                        email = doc.getString("email") ?: "",
                        role  = doc.getString("role")  ?: "user"
                    )
                }
                binding.rvUsers.adapter = UserAdapter(users) {}
                if (users.isEmpty()) {
                    Toast.makeText(context, "No se encontraron usuarios", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun publishTip() {
        val title   = binding.etTipTitle.text.toString().trim()
        val content = binding.etTipContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(context, "Completa título y contenido", Toast.LENGTH_SHORT).show()
            return
        }

        val tip = WellnessTip(
            title     = title,
            content   = content,
            category  = binding.spinnerCategory.selectedItem?.toString() ?: "bienestar",
            audience  = binding.spinnerAudience.selectedItem?.toString() ?: "todos"
        )

        db.collection("tips").add(tip)
            .addOnSuccessListener {
                Toast.makeText(context, "✅ Consejo publicado correctamente", Toast.LENGTH_SHORT).show()
                binding.etTipTitle.text?.clear()
                binding.etTipContent.text?.clear()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al publicar", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
