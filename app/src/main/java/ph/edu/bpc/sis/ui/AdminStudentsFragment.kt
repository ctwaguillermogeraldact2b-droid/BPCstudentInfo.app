package ph.edu.bpc.sis.ui

import android.content.Intent
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
import ph.edu.bpc.sis.ManageStudentActivity
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.data.SectionInfo
import ph.edu.bpc.sis.data.Student
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.DialogAddSectionBinding
import ph.edu.bpc.sis.databinding.FragmentAdminStudentsBinding
import ph.edu.bpc.sis.databinding.ItemStudentAdminBinding
import ph.edu.bpc.sis.doOnTextChange

class AdminStudentsFragment : Fragment() {
    private var _binding: FragmentAdminStudentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StudentAdapter
    private var selectedSectionFilter = "All Sections"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminStudentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = StudentAdapter(
            onClick = { student ->
                val intent = Intent(requireContext(), ManageStudentActivity::class.java)
                intent.putExtra("student_no", student.studentNo)
                startActivity(intent)
            },
            onMove = { student ->
                showMoveSectionDialog(student)
            }
        )
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = adapter

        val sections = listOf("All Sections") + StudentRepository.getSections().map { it.name }.distinct()
        val sectionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, sections)
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

        binding.btnCreateSection.setOnClickListener {
            showCreateSectionDialog()
        }

        refreshList()
    }

    private fun refreshList() {
        val secText = binding.acSectionFilter.text?.toString().orEmpty().trim()
        selectedSectionFilter = if (secText.isEmpty()) "All Sections" else secText

        val query = binding.etSearch.text?.toString().orEmpty().trim().lowercase()
        val allStudents = StudentRepository.getAllStudents()

        val filtered = allStudents.filter { student ->
            val matchesSection = selectedSectionFilter == "All Sections" || student.section.equals(selectedSectionFilter, ignoreCase = true)
            val matchesQuery = query.isEmpty() ||
                student.studentNo.lowercase().contains(query) ||
                student.fullName.lowercase().contains(query) ||
                student.section.lowercase().contains(query) ||
                student.courseCode.lowercase().contains(query) ||
                student.course.lowercase().contains(query)

            matchesSection && matchesQuery
        }.sortedWith(compareBy({ it.section }, { it.lastName }))

        adapter.submitList(filtered)
    }

    private fun showMoveSectionDialog(student: Student) {
        val sectionNames = StudentRepository.getSections().map { it.name }.distinct()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Move ${student.shortName} to Section")
            .setSingleChoiceItems(sectionNames.toTypedArray(), sectionNames.indexOf(student.section)) { dialog, which ->
                val newSec = sectionNames[which]
                StudentRepository.updateStudent(student.copy(section = newSec))
                Toast.makeText(requireContext(), "${student.shortName} moved to Section $newSec", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                refreshList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCreateSectionDialog() {
        val context = requireContext()
        val dialogBinding = DialogAddSectionBinding.inflate(layoutInflater)

        dialogBinding.acProgram.setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, StudentRepository.PROGRAMS))
        dialogBinding.acProgram.setText(StudentRepository.PROGRAMS.firstOrNull() ?: "", false)

        dialogBinding.acYearLevel.setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, listOf("1", "2", "3", "4")))
        dialogBinding.acYearLevel.setText("1", false)

        MaterialAlertDialogBuilder(context)
            .setTitle("Create New Section per Department")
            .setView(dialogBinding.root)
            .setPositiveButton("Create") { _, _ ->
                val secName = dialogBinding.etSectionName.text?.toString().orEmpty().trim()
                val program = dialogBinding.acProgram.text?.toString().orEmpty().trim().ifEmpty { StudentRepository.PROGRAMS[0] }
                val yLevel = dialogBinding.acYearLevel.text?.toString().orEmpty().toIntOrNull() ?: 1

                if (secName.isNotEmpty()) {
                    val newSec = SectionInfo(name = secName, program = program, yearLevel = yLevel)
                    if (StudentRepository.addSection(newSec)) {
                        Toast.makeText(context, "Section $secName created under $program", Toast.LENGTH_SHORT).show()
                        refreshList()
                    } else {
                        Toast.makeText(context, "Section already exists", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter a section name", Toast.LENGTH_SHORT).show()
                }
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

class StudentAdapter(
    private val onClick: (Student) -> Unit,
    private val onMove: ((Student) -> Unit)? = null
) : RecyclerView.Adapter<StudentAdapter.Holder>() {
    constructor(onClick: (Student) -> Unit) : this(onClick, null)

    private var list: List<Student> = emptyList()

    fun submitList(newList: List<Student>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemStudentAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val student = list[position]
        holder.binding.tvName.text = student.fullName
        holder.binding.tvStudentNo.text = "${student.studentNo} · ${student.courseCode}"
        holder.binding.tvSection.text = "Section ${student.section.ifEmpty { "Unassigned" }}"
        holder.binding.tvStatusBadge.text = student.enrollmentStatus

        holder.binding.root.setOnClickListener { onClick(student) }
        onMove?.let { move ->
            holder.binding.root.setOnLongClickListener {
                move(student)
                true
            }
        }
    }

    override fun getItemCount() = list.size

    class Holder(val binding: ItemStudentAdminBinding) : RecyclerView.ViewHolder(binding.root)
}
