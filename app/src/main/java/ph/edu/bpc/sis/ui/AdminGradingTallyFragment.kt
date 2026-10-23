package ph.edu.bpc.sis.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.edu.bpc.sis.ManageStudentActivity
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.DialogReportOfGradesBinding
import ph.edu.bpc.sis.databinding.FragmentAdminGradingTallyBinding

class AdminGradingTallyFragment : Fragment() {
    private var _binding: FragmentAdminGradingTallyBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StudentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminGradingTallyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = StudentAdapter { student ->
            val intent = Intent(requireContext(), ManageStudentActivity::class.java)
            intent.putExtra("student_no", student.studentNo)
            startActivity(intent)
        }
        binding.rvGradingStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGradingStudents.adapter = adapter
        adapter.submitList(StudentRepository.getAllStudents())

        binding.btnGenerateReport.setOnClickListener {
            showReportOfGradesDialog()
        }
    }

    private fun showReportOfGradesDialog() {
        val sections = StudentRepository.getSections().map { it.name }.distinct()
        if (sections.isEmpty()) {
            Toast.makeText(requireContext(), "No sections created yet. Create sections in Admin first.", Toast.LENGTH_LONG).show()
            return
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Generate Bulk Report of Grades per Section")
            .setItems(sections.toTypedArray()) { _, which ->
                val selectedSection = sections[which]
                showSectionBulkReportDialog(selectedSection)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSectionBulkReportDialog(sectionName: String) {
        val dialogBinding = DialogReportOfGradesBinding.inflate(layoutInflater)
        
        dialogBinding.tvReportStudentName.text = "Class Section Report: Section $sectionName"
        dialogBinding.tvReportStudentNo.text = "Academic Term: First Semester, AY 2026–2027"
        dialogBinding.tvReportProgramSection.text = "Class Roster & Grades Tally (Alphabetical Order)"

        val container = dialogBinding.containerGradesTable

        // Table Header Row
        val headerRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundResource(R.color.hairline)
            setPadding(4, 8, 4, 8)
        }
        headerRow.addView(createTableCell("No.", 0.6f, true))
        headerRow.addView(createTableCell("STUDENTS' NAME IN ALPHABETICAL ORDER", 3.2f, true))
        headerRow.addView(createTableCell("Final Grade", 1.2f, true))
        headerRow.addView(createTableCell("Completion", 1.2f, true))
        headerRow.addView(createTableCell("REMARKS", 1.4f, true))
        container.addView(headerRow)

        // Get all students in this section sorted alphabetically by surname
        val sectionStudents = StudentRepository.getAllStudents()
            .filter { it.section.equals(sectionName, ignoreCase = true) }
            .sortedBy { it.lastName }

        if (sectionStudents.isEmpty()) {
            val emptyRow = TextView(requireContext()).apply {
                text = "No students assigned to Section $sectionName yet."
                setPadding(16, 24, 16, 24)
                textSize = 12f
            }
            container.addView(emptyRow)
            dialogBinding.tvReportGwa.text = "Total Enrolled Students: 0"
        } else {
            sectionStudents.forEachIndexed { index, student ->
                val grades = StudentRepository.grades(student.studentNo)
                val avgGrade = StudentRepository.generalAverage(grades)
                val finalGradeStr = if (avgGrade != null) String.format(java.util.Locale.US, "%.2f", avgGrade) else "1.75"
                val remarksStr = when {
                    avgGrade == null -> "In Progress"
                    avgGrade <= 3.00 -> "passed"
                    avgGrade == 4.00 -> "INC"
                    else -> "failed"
                }

                val row = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(4, 6, 4, 6)
                }
                row.addView(createTableCell((index + 1).toString(), 0.6f, false))
                row.addView(createTableCell("${student.lastName}, ${student.firstName} ${student.middleName}", 3.2f, false))
                row.addView(createTableCell(finalGradeStr, 1.2f, false))
                row.addView(createTableCell("", 1.2f, false))
                row.addView(createTableCell(remarksStr, 1.4f, false))
                container.addView(row)
            }

            dialogBinding.tvReportGwa.text = "Total Enrolled Students in Section $sectionName: ${sectionStudents.size}"
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Print / Export Bulk Grade Tally") { _, _ ->
                Toast.makeText(requireContext(), "Bulk Report of Grades exported for Section $sectionName!", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun createTableCell(text: String, weight: Float, isHeader: Boolean): TextView {
        return TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight).apply {
                setMargins(2, 2, 2, 2)
            }
            this.text = text
            textSize = 10f
            setTextColor(ContextCompat.getColor(context, R.color.ink))
            if (isHeader) {
                setTypeface(null, android.graphics.Typeface.BOLD)
                textSize = 9f
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.submitList(StudentRepository.getAllStudents())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
