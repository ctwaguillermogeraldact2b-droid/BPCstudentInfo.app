package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.data.TeacherAssignment
import ph.edu.bpc.sis.databinding.DialogAddTeacherAssignmentBinding
import ph.edu.bpc.sis.databinding.FragmentAdminTeacherAssignmentsBinding
import ph.edu.bpc.sis.databinding.ItemTeacherAssignmentBinding
import ph.edu.bpc.sis.doOnTextChange

class AdminTeacherAssignmentsFragment : Fragment() {
    private var _binding: FragmentAdminTeacherAssignmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TeacherAssignmentAdapter
    private var selectedClusterFilter = "All Clusters"
    private var selectedSectionFilter = "All Sections"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminTeacherAssignmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = TeacherAssignmentAdapter { item ->
            showOptionsDialog(item)
        }
        binding.rvAssignments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAssignments.adapter = adapter

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

        val sectionOptions = listOf("All Sections") + StudentRepository.getSections().map { it.name }
        val sectionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, sectionOptions)
        binding.acSectionFilter.setAdapter(sectionAdapter)
        binding.acSectionFilter.setText("All Sections", false)

        binding.acSectionFilter.setOnItemClickListener { parent, _, position, _ ->
            selectedSectionFilter = parent.getItemAtPosition(position).toString()
            refreshList()
        }

        binding.acSectionFilter.doOnTextChange {
            val currentText = binding.acSectionFilter.text?.toString().orEmpty().ifEmpty { "All Sections" }
            if (currentText != selectedSectionFilter) {
                selectedSectionFilter = currentText
                refreshList()
            }
        }

        binding.etSearch.doOnTextChange {
            refreshList()
        }

        binding.btnAssignTeacher.setOnClickListener {
            showAddAssignmentDialog(null)
        }

        refreshList()
    }

    private fun refreshList() {
        val currentClusterText = binding.acClusterFilter.text?.toString().orEmpty().trim()
        selectedClusterFilter = if (currentClusterText.isEmpty()) "All Clusters" else currentClusterText

        val currentSecText = binding.acSectionFilter.text?.toString().orEmpty().trim()
        selectedSectionFilter = if (currentSecText.isEmpty()) "All Sections" else currentSecText

        val query = binding.etSearch.text?.toString().orEmpty().trim().lowercase()
        val allAssignments = StudentRepository.getAllTeacherAssignments()

        val keyword = when {
            selectedClusterFilter.contains("ICT", ignoreCase = true) -> "ict"
            selectedClusterFilter.contains("LANG", ignoreCase = true) -> "lang"
            selectedClusterFilter.contains("HB", ignoreCase = true) -> "hb"
            selectedClusterFilter.contains("ASC", ignoreCase = true) -> "asc"
            selectedClusterFilter.contains("SKP", ignoreCase = true) -> "skp"
            else -> ""
        }

        val filtered = allAssignments.filter { assignment ->
            val teacherInfo = StudentRepository.getAllTeacherCodes().find { it.contains(assignment.teacherCode, ignoreCase = true) } ?: assignment.teacherCode
            val matchesCluster = if (selectedClusterFilter == "All Clusters" || keyword.isEmpty()) {
                true
            } else {
                teacherInfo.lowercase().contains(keyword) || assignment.teacherCode.lowercase().contains(keyword)
            }

            val matchesSection = selectedSectionFilter == "All Sections" || assignment.section.equals(selectedSectionFilter, ignoreCase = true)

            val matchesSearch = query.isEmpty() ||
                assignment.teacherCode.lowercase().contains(query) ||
                assignment.section.lowercase().contains(query) ||
                assignment.subjectCode.lowercase().contains(query) ||
                teacherInfo.lowercase().contains(query)

            matchesCluster && matchesSection && matchesSearch
        }

        adapter.submitList(filtered)
    }

    private fun showOptionsDialog(item: TeacherAssignment) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Teacher Assignment")
            .setItems(arrayOf("Edit Time & Details", "Remove Assignment")) { _, which ->
                when (which) {
                    0 -> showAddAssignmentDialog(item)
                    1 -> showRemoveDialog(item)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddAssignmentDialog(existing: TeacherAssignment?) {
        val dialogBinding = DialogAddTeacherAssignmentBinding.inflate(layoutInflater)
        val clusters = StudentRepository.CLUSTERS
        val sections = StudentRepository.getSections()
        val sectionList = sections.map { it.name }
        val subjectList = StudentRepository.offeredSubjects().map { "${it.code} — ${it.title}" }
        val timeList = listOf("08:00 - 10:00 AM", "10:00 - 12:00 PM", "01:00 - 03:00 PM", "03:00 - 05:00 PM", "MWF 8:00–9:00", "TTh 9:00–10:30", "MW 13:00–14:30")

        dialogBinding.acCluster.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, clusters))
        dialogBinding.acSection.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, sectionList))
        dialogBinding.acSubjectCode.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, subjectList))
        dialogBinding.acMeetingTime.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, timeList))

        fun updateTeacherDropdown(clusterName: String) {
            val teachersInCluster = StudentRepository.getAllTeacherCodes().filter { it.contains(clusterName, ignoreCase = true) }
            val finalTeachers = if (teachersInCluster.isNotEmpty()) teachersInCluster else StudentRepository.getAllTeacherCodes()
            dialogBinding.acTeacherCode.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, finalTeachers))
            dialogBinding.acTeacherCode.setText(finalTeachers.firstOrNull() ?: "", false)
        }

        dialogBinding.acCluster.setOnItemClickListener { parent, _, position, _ ->
            val cluster = parent.getItemAtPosition(position).toString()
            updateTeacherDropdown(cluster)
        }

        if (existing != null) {
            val allTeachers = StudentRepository.getAllTeacherCodes()
            val match = allTeachers.firstOrNull { it.startsWith(existing.teacherCode, ignoreCase = true) } ?: existing.teacherCode
            val matchingCluster = clusters.firstOrNull { match.contains(it, ignoreCase = true) } ?: clusters.first()
            dialogBinding.acCluster.setText(matchingCluster, false)
            updateTeacherDropdown(matchingCluster)
            dialogBinding.acTeacherCode.setText(match, false)

            dialogBinding.acSection.setText(existing.section, false)
            val subMatch = subjectList.firstOrNull { it.startsWith(existing.subjectCode, ignoreCase = true) } ?: existing.subjectCode
            dialogBinding.acSubjectCode.setText(subMatch, false)
            dialogBinding.acMeetingTime.setText(existing.meetingTime, false)
        } else {
            dialogBinding.acCluster.setText(clusters.first(), false)
            updateTeacherDropdown(clusters.first())
            dialogBinding.acSection.setText(sectionList.firstOrNull() ?: "1-A", false)
            dialogBinding.acSubjectCode.setText(subjectList.firstOrNull() ?: "", false)
            dialogBinding.acMeetingTime.setText(timeList.firstOrNull() ?: "08:00 - 10:00 AM", false)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (existing == null) "Assign Section to Teacher" else "Edit Teacher Assignment & Time")
            .setMessage("Select Department Cluster first, then pick registered teacher, section, subject, and time.")
            .setView(dialogBinding.root)
            .setPositiveButton(if (existing == null) "Assign" else "Update") { _, _ ->
                val rawTeacherStr = dialogBinding.acTeacherCode.text?.toString().orEmpty().trim()
                val teacherCode = rawTeacherStr.substringBefore(" ").trim()
                val section = dialogBinding.acSection.text?.toString().orEmpty().trim()
                val rawSubjStr = dialogBinding.acSubjectCode.text?.toString().orEmpty().trim()
                val subjectCode = rawSubjStr.substringBefore(" — ").trim()
                val meetingTime = dialogBinding.acMeetingTime.text?.toString().orEmpty().trim().ifEmpty { "08:00 - 10:00 AM" }

                if (teacherCode.isNotEmpty() && section.isNotEmpty() && subjectCode.isNotEmpty()) {
                    val assignment = TeacherAssignment(
                        id = existing?.id ?: java.util.UUID.randomUUID().toString(),
                        teacherCode = teacherCode,
                        section = section,
                        subjectCode = subjectCode,
                        meetingTime = meetingTime
                    )
                    if (existing != null) {
                        StudentRepository.updateTeacherAssignment(assignment)
                        Toast.makeText(requireContext(), "Updated assignment for $teacherCode ($meetingTime)", Toast.LENGTH_SHORT).show()
                        refreshList()
                    } else {
                        if (StudentRepository.addTeacherAssignment(assignment)) {
                            Toast.makeText(requireContext(), "Assigned $teacherCode to Section $section ($subjectCode - $meetingTime)", Toast.LENGTH_SHORT).show()
                            refreshList()
                        } else {
                            Toast.makeText(requireContext(), "Assignment already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRemoveDialog(item: TeacherAssignment) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Assignment?")
            .setMessage("Remove assignment for ${item.teacherCode} (Section ${item.section} - ${item.subjectCode})?")
            .setPositiveButton("Remove") { _, _ ->
                StudentRepository.removeTeacherAssignment(item.id)
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

class TeacherAssignmentAdapter(
    private val onClick: (TeacherAssignment) -> Unit
) : RecyclerView.Adapter<TeacherAssignmentAdapter.Holder>() {
    private var list: List<TeacherAssignment> = emptyList()

    fun submitList(newList: List<TeacherAssignment>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemTeacherAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
        val teacherDetails = StudentRepository.getAllTeacherCodes().find { it.contains(item.teacherCode, ignoreCase = true) } ?: item.teacherCode
        val subject = StudentRepository.offeredSubjects().find { it.code.equals(item.subjectCode, ignoreCase = true) }
        val subjectInfo = if (subject != null) "${subject.code} (${subject.subjectType} — ${subject.hoursLabel})" else item.subjectCode
        holder.binding.tvTeacherCode.text = "Teacher: $teacherDetails"
        holder.binding.tvAssignmentMeta.text = "Section ${item.section} · $subjectInfo | ${item.meetingTime}"
        holder.binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = list.size

    class Holder(val binding: ItemTeacherAssignmentBinding) : RecyclerView.ViewHolder(binding.root)
}
