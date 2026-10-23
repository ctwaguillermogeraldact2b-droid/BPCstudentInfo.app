package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.data.Subject
import ph.edu.bpc.sis.databinding.DialogAddSubjectBinding
import ph.edu.bpc.sis.databinding.FragmentAdminSubjectsBinding
import ph.edu.bpc.sis.databinding.ItemOfferedSubjectBinding
import ph.edu.bpc.sis.doOnTextChange

class AdminSubjectsFragment : Fragment() {
    private var _binding: FragmentAdminSubjectsBinding? = null
    private val binding get() = _binding!!
    private lateinit var subjectAdapter: AdminOfferedSubjectAdapter
    private var selectedClusterFilter = "All Clusters"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subjectAdapter = AdminOfferedSubjectAdapter(
            onClick = { subject -> showAddEditSubjectDialog(subject) },
            onDelete = { subject -> showDeleteSubjectDialog(subject) }
        )
        binding.rvOfferedSubjects.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOfferedSubjects.adapter = subjectAdapter
        binding.btnAddSubject.setOnClickListener { showAddEditSubjectDialog(null) }

        val clusterOptions = listOf("All Clusters") + StudentRepository.CLUSTERS
        val clusterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, clusterOptions)
        binding.acClusterFilter.setAdapter(clusterAdapter)
        binding.acClusterFilter.setText("All Clusters", false)

        binding.acClusterFilter.setOnItemClickListener { parent, _, position, _ ->
            selectedClusterFilter = parent.getItemAtPosition(position).toString()
            refreshData()
        }

        binding.acClusterFilter.doOnTextChange {
            val currentText = binding.acClusterFilter.text?.toString().orEmpty().ifEmpty { "All Clusters" }
            if (currentText != selectedClusterFilter) {
                selectedClusterFilter = currentText
                refreshData()
            }
        }

        refreshData()
    }

    private fun refreshData() {
        val currentText = binding.acClusterFilter.text?.toString().orEmpty().trim()
        selectedClusterFilter = if (currentText.isEmpty()) "All Clusters" else currentText

        val allSubjects = StudentRepository.offeredSubjects()
        val filtered = if (selectedClusterFilter == "All Clusters") {
            allSubjects
        } else {
            val keyword = when {
                selectedClusterFilter.contains("ICT", ignoreCase = true) -> "ict"
                selectedClusterFilter.contains("LANG", ignoreCase = true) -> "lang"
                selectedClusterFilter.contains("HB", ignoreCase = true) -> "hb"
                selectedClusterFilter.contains("ASC", ignoreCase = true) -> "asc"
                selectedClusterFilter.contains("SKP", ignoreCase = true) -> "skp"
                else -> selectedClusterFilter
            }
            allSubjects.filter { it.cluster.contains(keyword, ignoreCase = true) }
        }

        subjectAdapter.submitList(filtered)
    }

    private fun showAddEditSubjectDialog(existing: Subject?) {
        val dialogBinding = DialogAddSubjectBinding.inflate(layoutInflater)

        val clusters = StudentRepository.CLUSTERS
        val typeOptions = listOf("Major", "Minor")
        val hoursOptions = listOf("3 hrs", "1 and 1/2 hrs", "2 hrs", "4 hrs", "1 hr")
        val timeOptions = listOf("07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM")

        dialogBinding.acCluster.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, clusters))
        dialogBinding.acSubjectType.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, typeOptions))
        dialogBinding.acHours.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, hoursOptions))
        dialogBinding.acStartTime.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, timeOptions))
        dialogBinding.acEndTime.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, timeOptions))

        existing?.let {
            dialogBinding.etCode.setText(it.code)
            dialogBinding.etTitle.setText(it.title)
            dialogBinding.acCluster.setText(it.cluster, false)
            dialogBinding.acSubjectType.setText(it.subjectType, false)
            dialogBinding.acHours.setText(it.hoursLabel, false)
            dialogBinding.acStartTime.setText(it.startTime, false)
            dialogBinding.acEndTime.setText(it.endTime, false)
            dialogBinding.etUnits.setText(it.units.toString())
            dialogBinding.etRoom.setText(it.room)
            dialogBinding.etSlots.setText(it.slotsLeft.toString())
            dialogBinding.etCode.isEnabled = false
        } ?: run {
            dialogBinding.acCluster.setText(clusters.first(), false)
            dialogBinding.acSubjectType.setText("Major", false)
            dialogBinding.acHours.setText("3 hrs", false)
            dialogBinding.acStartTime.setText("07:00 AM", false)
            dialogBinding.acEndTime.setText("09:30 AM", false)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (existing == null) "Add Schedule & Subject" else "Edit Schedule & Subject")
            .setView(dialogBinding.root)
            .setPositiveButton(if (existing == null) "Add" else "Update") { _, _ ->
                val code = dialogBinding.etCode.text.toString().trim()
                val title = dialogBinding.etTitle.text.toString().trim()
                val cluster = dialogBinding.acCluster.text.toString().trim().ifEmpty { clusters[0] }
                val subjType = dialogBinding.acSubjectType.text.toString().trim().ifEmpty { "Major" }
                val hoursStr = dialogBinding.acHours.text.toString().trim()
                val hoursVal = when {
                    hoursStr.contains("1 and 1/2", ignoreCase = true) -> 1.5
                    hoursStr.contains("1", ignoreCase = true) && hoursStr.contains("hr") -> 1.0
                    hoursStr.contains("2", ignoreCase = true) -> 2.0
                    hoursStr.contains("4", ignoreCase = true) -> 4.0
                    else -> 3.0
                }
                val startTime = dialogBinding.acStartTime.text.toString().trim().ifEmpty { "07:00 AM" }
                val endTime = dialogBinding.acEndTime.text.toString().trim().ifEmpty { "09:30 AM" }
                val units = dialogBinding.etUnits.text.toString().toIntOrNull() ?: 3
                val room = dialogBinding.etRoom.text.toString().trim()
                val slots = dialogBinding.etSlots.text.toString().toIntOrNull() ?: 20

                if (code.isNotEmpty() && title.isNotEmpty()) {
                    val newSubject = Subject(
                        code = code,
                        title = title,
                        units = units,
                        lecUnits = units,
                        labUnits = 0,
                        room = room,
                        slotsLeft = slots,
                        hoursPerWeek = hoursVal,
                        subjectType = subjType,
                        startTime = startTime,
                        endTime = endTime,
                        cluster = cluster
                    )
                    if (existing != null) {
                        StudentRepository.updateOfferedSubject(existing.code, newSubject)
                    } else {
                        StudentRepository.addOfferedSubject(newSubject)
                    }
                    refreshData()
                    Toast.makeText(requireContext(), "Schedule and Subject saved under $cluster", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteSubjectDialog(subject: Subject) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Subject?")
            .setMessage("Remove ${subject.code} from offered curriculum?")
            .setPositiveButton("Delete") { _, _ ->
                StudentRepository.deleteOfferedSubject(subject.code)
                refreshData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AdminOfferedSubjectAdapter(
    private val onClick: (Subject) -> Unit,
    private val onDelete: (Subject) -> Unit
) : ListAdapter<Subject, AdminOfferedSubjectAdapter.Holder>(SubjectDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemOfferedSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val subject = getItem(position)
        holder.binding.cbSelect.visibility = View.GONE
        holder.binding.tvCode.text = "${subject.code} (${subject.subjectType} — ${subject.hoursLabel})"
        holder.binding.tvTitle.text = subject.title
        holder.binding.tvUnits.text = holder.itemView.context.resources.getQuantityString(R.plurals.units, subject.units, subject.units)
        holder.binding.tvMeta.text = "[${subject.cluster.substringBefore(" ")}] Time: ${subject.startTime} - ${subject.endTime}  ·  Room: ${subject.room}"
        holder.binding.tvSlots.text = holder.itemView.context.resources.getQuantityString(
            R.plurals.slots_left,
            subject.slotsLeft,
            subject.slotsLeft
        )
        holder.binding.tvSlots.visibility = View.VISIBLE

        holder.binding.root.setOnClickListener { onClick(subject) }
        holder.binding.root.setOnLongClickListener {
            onDelete(subject)
            true
        }
    }

    class Holder(val binding: ItemOfferedSubjectBinding) : RecyclerView.ViewHolder(binding.root)

    private object SubjectDiffCallback : DiffUtil.ItemCallback<Subject>() {
        override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem == newItem
        }
    }
}
