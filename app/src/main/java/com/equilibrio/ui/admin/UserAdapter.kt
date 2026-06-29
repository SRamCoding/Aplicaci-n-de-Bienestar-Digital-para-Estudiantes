package com.equilibrio.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.equilibrio.data.model.User
import com.equilibrio.databinding.ItemUserBinding

class UserAdapter(
    private val users: List<User>,
    private val onClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.VH>() {

    inner class VH(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = users[position]
        holder.binding.apply {
            tvUserName.text  = user.name
            tvUserEmail.text = user.email
            tvUserRole.text  = if (user.role == "admin") "Admin" else "Usuario"
            tvUserRole.setBackgroundResource(
                if (user.role == "admin") android.R.color.holo_green_light
                else android.R.color.transparent
            )
            root.setOnClickListener { onClick(user) }
        }
    }
}
