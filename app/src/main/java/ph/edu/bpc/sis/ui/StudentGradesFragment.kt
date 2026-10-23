package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.edu.bpc.sis.ManageStudentActivity
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.TeacherManageStudentActivity
import ph.edu.bpc.sis.adapter.GradeAdapter
import ph.edu.bpc.sis.data.GradeRecord
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.FragmentStudentGradesBinding

class StudentGradesFragment : Fragment() {
    private var _binding: FragmentStudentGradesBinding? = null
    private val binding get() = _binding!!

    private val currentStudentNo: String?
        get() = (activity as? TeacherManageStudentActivity)?.studentNo
            ?: (activity as? ManageStudentActivity)?.studentNo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentGradesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val studentNo = currentStudentNo ?: return

        binding.rvGrades.layoutManager = LinearLayoutManager(requireContext())

        val isTeacherMode = activity is TeacherManageStudentActivity
        binding.btnAddGrade.visibility = if (isTeacherMode || StudentRepository.isAdmin()) View.VISIBLE else View.GONE
        binding.btnAddGrade.setOnClickListener { showAddGradeDialog(studentNo, null) }

        refresh(studentNo)
    }

    private fun refresh(studentNo: String) {
        val grades = StudentRepository.grades(studentNo)
        binding.rvGrades.adapter = GradeAdapter(grades)
    }

    private fun showAddGradeDialog(studentNo: String, existing: GradeRecord?) {
        val student = StudentRepository.getStudentByNo(studentNo)
        val isTeacherMode = activity is TeacherManageStudentActivity

        if (isTeacherMode && student != null) {
            val currentTeacherCode = StudentRepository.currentStudent().studentNo
            val assignedSubjectCodes = StudentRepository.getTeacherAssignments(currentTeacherCode)
                .filter { it.section.equals(student.section, ignoreCase = true) }
                .map { it.subjectCode.uppercase() }
                .toSet()

            if (assignedSubjectCodes.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "No subjects assigned to you for Section ${student.section}",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            if (existing != null && !assignedSubjectCodes.contains(existing.code.uppercase())) {
                Toast.makeText(
                    requireContext(),
                    "You can only set grades for your assigned subject(s): ${assignedSubjectCodes.joinToString()}",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_grade, null)
        val etCode = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etCode)
        val etTitle = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etTitle)
        val etUnits = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etUnits)
        val etGrade = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etGrade)

        if (existing != null) {
            etCode.setText(existing.code)
            etTitle.setText(existing.title)
            etUnits.setText(existing.units.toString())
            etGrade.setText(existing.finalGrade?.toString() ?: "")
        } else if (isTeacherMode && student != null) {
            val currentTeacherCode = StudentRepository.currentStudent().studentNo
            val assignedSubjectCodes = StudentRepository.getTeacherAssignments(currentTeacherCode)
                .filter { it.section.equals(student.section, ignoreCase = true) }
                .map { it.subjectCode }
            if (assignedSubjectCodes.isNotEmpty()) {
                etCode.setText(assignedSubjectCodes.first())
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (existing == null) "Add Grade Record" else "Edit Grade Record")
            .setView(view)
            .setPositiveButton(if (existing == null) "Add" else "Update") { _, _ ->
                val code = etCode.text.toString().trim()
                val title = etTitle.text.toString().trim()
                val units = etUnits.text.toString().toIntOrNull() ?: 3
                val gradeVal = etGrade.text.toString().toDoubleOrNull()

                if (isTeacherMode && student != null) {
                    val currentTeacherCode = StudentRepository.currentStudent().studentNo
                    val assignedSubjectCodes = StudentRepository.getTeacherAssignments(currentTeacherCode)
                        .filter { it.section.equals(student.section, ignoreCase = true) }
                        .map { it.subjectCode.uppercase() }
                        .toSet()

                    if (!assignedSubjectCodes.contains(code.uppercase())) {
                        Toast.makeText(
                            requireContext(),
                            "You can only set grades for your assigned subject(s): ${assignedSubjectCodes.joinToString()}",
                            Toast.LENGTH_LONG
                        ).show()
                        return@setPositiveButton
                    }
                }

                if (code.isNotEmpty() && title.isNotEmpty()) {
                    val newGrade = GradeRecord(code, title, units, null, null, gradeVal)
                    val current = StudentRepository.grades(studentNo).toMutableList()
                    
                    if (existing != null) {
                        val index = current.indexOf(existing)
                        if (index != -1) current[index] = newGrade
                    } else {
                        current.add(newGrade)
                    }
                    
                    StudentRepository.updateGrades(studentNo, current)
                    refresh(studentNo)
                    Toast.makeText(requireContext(), "Grade updated for $code", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteGradeDialog(studentNo: String, grade: GradeRecord) {
        val student = StudentRepository.getStudentByNo(studentNo)
        val isTeacherMode = activity is TeacherManageStudentActivity

        if (isTeacherMode && student != null) {
            val currentTeacherCode = StudentRepository.currentStudent().studentNo
            val assignedSubjectCodes = StudentRepository.getTeacherAssignments(currentTeacherCode)
                .filter { it.section.equals(student.section, ignoreCase = true) }
                .map { it.subjectCode.uppercase() }
                .toSet()

            if (!assignedSubjectCodes.contains(grade.code.uppercase())) {
                Toast.makeText(
                    requireContext(),
                    "You can only delete grades for your assigned subject(s): ${assignedSubjectCodes.joinToString().ifEmpty { "None" }}",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Grade Record?")
            .setMessage("Remove ${grade.code} — ${grade.title}?")
            .setPositiveButton("Delete") { _, _ ->
                val current = StudentRepository.grades(studentNo).toMutableList()
                current.remove(grade)
                StudentRepository.updateGrades(studentNo, current)
                refresh(studentNo)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        val studentNo = currentStudentNo ?: return
        refresh(studentNo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
