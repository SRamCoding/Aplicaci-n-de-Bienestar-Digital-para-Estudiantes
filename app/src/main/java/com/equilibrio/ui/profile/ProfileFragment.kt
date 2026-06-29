package com.equilibrio.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.equilibrio.R
import com.equilibrio.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserData()

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        binding.btnAdmin.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_adminFragment)
        }
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val name   = doc.getString("name")   ?: "Estudiante"
                val email  = doc.getString("email")  ?: auth.currentUser?.email ?: ""
                val uni    = doc.getString("university") ?: ""
                val career = doc.getString("career") ?: ""
                val streak = doc.getLong("streak")?.toInt() ?: 0
                val role   = doc.getString("role") ?: "user"

                binding.tvUserName.text  = name
                binding.tvUserEmail.text = email
                binding.tvUniversity.text = "$uni · $career"
                binding.tvStreak.text = "🔥 $streak días de racha"

                // Avatar con iniciales
                val initials = name.split(" ")
                    .filter { it.isNotEmpty() }
                    .take(2)
                    .map { it.first().uppercaseChar() }
                    .joinToString("")
                binding.tvAvatar.text = initials

                // Mostrar botón admin solo si es admin
                binding.btnAdmin.visibility = if (role == "admin") View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error cargando perfil", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
