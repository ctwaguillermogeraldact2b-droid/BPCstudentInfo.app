package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.data.AuthorizedTeacherCode
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.DialogAddTeacherCodeBinding
import ph.edu.bpc.sis.databinding.FragmentAdminTeacherCodesBinding
import ph.edu.bpc.sis.databinding.ItemAuthorizedTeacherCodeBinding
import ph.edu.bpc.sis.doOnTextChange
import kotlin.random.Random

class AdminAuthorizedTeacherCodesFragment : Fragment() {
    private var _binding: FragmentAdminTeacherCodesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AuthorizedTeacherCodeAdapter
    private var selectedClusterFilter = "All Clusters"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminTeacherCodesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AuthorizedTeacherCodeAdapter { item ->
            showRemoveDialog(item)
        }
        binding.rvAuthorizedTeachers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAuthorizedTeachers.adapter = adapter

        val clusterOptions = listOf("All Clusters") + StudentRepository.CLUSTERS
        val clusterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, clusterOptions)
        binding.acClusterFilter.setAdapter(clusterAdapter)
        binding.acClusterFilter.setText("All Clusters", false)

        binding.acClusterFilter.setOnItemClickListener { parent, _, position, _ ->
            selectedClusterFilter = parent.getItemAtPosition(position).toString()
            refreshList()
        }

        binding.acClusterFilter.doOnTextChange {
            val currentText = binding.acClusterFilter.text?.toString().orEmpty().ifEmpty { "All Clusters" }
            if (currentText != selectedClusterFilter) {
                selectedClusterFilter = currentText
                refreshList()
            }
        }

        binding.etSearch.doOnTextChange {
            refreshList()
        }

        binding.btnAuthorizeTeacher.setOnClickListener {
            showAuthorizeDialog()
        }

        refreshList()
    }

    private fun refreshList() {
        val currentText = binding.acClusterFilter.text?.toString().orEmpty().trim()
        selectedClusterFilter = if (currentText.isEmpty()) "All Clusters" else currentText

        val searchQuery = binding.etSearch.text?.toString().orEmpty().trim().lowercase()
        val allFiltered = StudentRepository.getAuthorizedTeacherCodes(selectedClusterFilter)

        val finalFiltered = if (searchQuery.isEmpty()) {
            allFiltered
        } else {
            allFiltered.filter {
                it.surname.lowercase().contains(searchQuery) ||
                it.firstName.lowercase().contains(searchQuery) ||
                it.code.lowercase().contains(searchQuery) ||
                it.department.lowercase().contains(searchQuery)
            }
        }

        adapter.submitList(finalFiltered)
    }

    private fun generateShuffledNonYearCode(clusterName: String): String {
        val prefix = when {
            clusterName.contains("ICT", ignoreCase = true) -> "ICT"
            clusterName.contains("LANG", ignoreCase = true) -> "LANG"
            clusterName.contains("HB", ignoreCase = true) -> "HB"
            clusterName.contains("ASC", ignoreCase = true) -> "ASC"
            clusterName.contains("SKP", ignoreCase = true) -> "SKP"
            else -> "TCH"
        }
        // Generate random 4-digit number (avoiding year patterns like 2024, 2026)
        var num: Int
        do {
            num = Random.nextInt(1000, 9999)
        } while (num in 2020..2030)
        return "$prefix-$num"
    }

    private fun showAuthorizeDialog() {
        val dialogBinding = DialogAddTeacherCodeBinding.inflate(layoutInflater)

        val clusterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, StudentRepository.CLUSTERS)
        dialogBinding.acCluster.setAdapter(clusterAdapter)
        dialogBinding.acCluster.setText(StudentRepository.CLUSTERS[0], false)

        dialogBinding.etTeacherCode.setText(generateShuffledNonYearCode(StudentRepository.CLUSTERS[0]))

        dialogBinding.acCluster.setOnItemClickListener { parent, _, position, _ ->
            val selectedCluster = parent.getItemAtPosition(position).toString()
            dialogBinding.etTeacherCode.setText(generateShuffledNonYearCode(selectedCluster))
        }

        dialogBinding.btnGenerateCode.setOnClickListener {
            val clusterStr = dialogBinding.acCluster.text?.toString().orEmpty().ifEmpty { StudentRepository.CLUSTERS[0] }
            dialogBinding.etTeacherCode.setText(generateShuffledNonYearCode(clusterStr))
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Authorize New Teacher Code")
            .setMessage("Select Department Cluster, enter name and shuffle a unique non-year registration code.")
            .setView(dialogBinding.root)
            .setPositiveButton("Authorize") { _, _ ->
                val firstName = dialogBinding.etTeacherFirstName.text?.toString().orEmpty().trim()
                val surname = dialogBinding.etTeacherSurname.text?.toString().orEmpty().trim()
                val code = dialogBinding.etTeacherCode.text?.toString().orEmpty().trim()
                val cluster = dialogBinding.acCluster.text?.toString().orEmpty().trim().ifEmpty { StudentRepository.CLUSTERS[0] }
                val isClusterHead = dialogBinding.cbClusterHead.isChecked

                if (surname.isNotEmpty() && code.isNotEmpty()) {
                    if (StudentRepository.addPreAuthorizedTeacherCode(surname, firstName, code, cluster, isClusterHead)) {
                        Toast.makeText(requireContext(), "Teacher Code $code authorized for $surname", Toast.LENGTH_SHORT).show()
                        refreshList()
                    } else {
                        Toast.makeText(requireContext(), "Code already authorized or invalid", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Surname and Code are required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRemoveDialog(item: AuthorizedTeacherCode) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Teacher Authorization?")
            .setMessage("Remove authorization for ${item.displayName} (${item.code})?")
            .setPositiveButton("Remove") { _, _ ->
                StudentRepository.removePreAuthorizedTeacherCode(item.surname, item.code)
                refreshList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AuthorizedTeacherCodeAdapter(
    private val onClick: (AuthorizedTeacherCode) -> Unit
) : RecyclerView.Adapter<AuthorizedTeacherCodeAdapter.Holder>() {
    private var list: List<AuthorizedTeacherCode> = emptyList()

    fun submitList(newList: List<AuthorizedTeacherCode>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemAuthorizedTeacherCodeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
        val prefix = if (item.isClusterHead) "Cluster Head: " else "Faculty: "
        holder.binding.tvSurname.text = "$prefix${item.displayName}"
        holder.binding.tvClusterHeadBadge.visibility = if (item.isClusterHead) View.VISIBLE else View.GONE
        holder.binding.tvDepartment.text = item.department
        holder.binding.tvCode.text = "Code: ${item.code}"
        holder.binding.tvStatus.text = if (item.isSignedUp) "Signed Up" else "Authorized"

        val color = if (item.isSignedUp) R.color.ink_muted else R.color.bpc_green
        holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.binding.root.context, color))

        holder.binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = list.size

    class Holder(val binding: ItemAuthorizedTeacherCodeBinding) : RecyclerView.ViewHolder(binding.root)
}
