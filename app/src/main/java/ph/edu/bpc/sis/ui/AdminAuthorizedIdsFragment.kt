package ph.edu.bpc.sis.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.edu.bpc.sis.ManageStudentActivity
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.data.AuthorizedId
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.FragmentAdminIdsBinding
import ph.edu.bpc.sis.databinding.ItemAuthorizedIdBinding

class AdminAuthorizedIdsFragment : Fragment() {
    private var _binding: FragmentAdminIdsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AuthorizedIdAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminIdsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AuthorizedIdAdapter(
            onClick = { authId ->
                val registeredStudent = StudentRepository.getStudentByNo(authId.id)
                if (registeredStudent != null) {
                    val intent = Intent(requireContext(), ManageStudentActivity::class.java)
                    intent.putExtra("student_no", registeredStudent.studentNo)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "ID ${authId.id} is authorized but not yet registered.", Toast.LENGTH_SHORT).show()
                }
            },
            onLongClick = { authId ->
                showRemoveAuthIdDialog(authId)
            }
        )
        binding.rvAuthorizedIds.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAuthorizedIds.adapter = adapter

        binding.btnAuthorizeId.setOnClickListener {
            showAuthorizeDialog()
        }
        adapter.submitList(StudentRepository.getAuthorizedIds())
    }

    private fun showAuthorizeDialog() {
        val input = com.google.android.material.textfield.TextInputEditText(requireContext()).apply {
            hint = "Example: 2024-00123"
            inputType = android.text.InputType.TYPE_CLASS_TEXT
            maxLines = 1
        }
        val container = android.widget.FrameLayout(requireContext()).apply {
            val margin = (24 * resources.displayMetrics.density).toInt()
            setPadding(margin, margin / 2, margin, 0)
            addView(input)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Authorize New Student ID")
            .setMessage("This Student ID will be allowed to sign up for an account.")
            .setView(container)
            .setPositiveButton("Authorize") { _, _ ->
                val newId = input.text?.toString().orEmpty().trim()
                if (StudentRepository.addPreAuthorizedId(newId)) {
                    Toast.makeText(requireContext(), "ID $newId authorized", Toast.LENGTH_SHORT).show()
                    adapter.submitList(StudentRepository.getAuthorizedIds())
                } else {
                    Toast.makeText(requireContext(), "ID already authorized or invalid", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRemoveAuthIdDialog(authId: AuthorizedId) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Authorization?")
            .setMessage("Are you sure you want to remove ${authId.id}? This will prevent sign-up if the user hasn't registered yet.")
            .setPositiveButton("Remove") { _, _ ->
                StudentRepository.removePreAuthorizedId(authId.id)
                adapter.submitList(StudentRepository.getAuthorizedIds())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        adapter.submitList(StudentRepository.getAuthorizedIds())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AuthorizedIdAdapter(
    private val onClick: (AuthorizedId) -> Unit,
    private val onLongClick: (AuthorizedId) -> Unit
) : RecyclerView.Adapter<AuthorizedIdAdapter.Holder>() {
    private var list: List<AuthorizedId> = emptyList()

    fun submitList(newList: List<AuthorizedId>) {
        list = newList.reversed() // Show newest first
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemAuthorizedIdBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
        holder.binding.tvId.text = item.id

        val registeredStudent = StudentRepository.getStudentByNo(item.id)
        if (registeredStudent != null) {
            holder.binding.tvDetails.text = "${registeredStudent.fullName} · Section ${registeredStudent.section} (${registeredStudent.courseCode})"
            holder.binding.tvStatus.text = "Signed Up (Used)"
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.ink_muted))
        } else if (item.isSignedUp) {
            holder.binding.tvDetails.text = "Account registered"
            holder.binding.tvStatus.text = "Signed Up"
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.ink_muted))
        } else {
            holder.binding.tvDetails.text = "Awaiting student sign-up"
            holder.binding.tvStatus.text = "Authorized"
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.bpc_green))
        }

        holder.binding.root.setOnClickListener { onClick(item) }
        holder.binding.root.setOnLongClickListener {
            onLongClick(item)
            true
        }
    }

    override fun getItemCount() = list.size

    class Holder(val binding: ItemAuthorizedIdBinding) : RecyclerView.ViewHolder(binding.root)
}
