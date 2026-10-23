package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.adapter.OfferedSubjectAdapter
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.data.Subject
import ph.edu.bpc.sis.databinding.FragmentEnrollmentBinding

class EnrollmentFragment : Fragment() {

    private var _binding: FragmentEnrollmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var subjectAdapter: OfferedSubjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnrollmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupEnrollmentState()
    }

    override fun onResume() {
        super.onResume()
        setupEnrollmentState()
    }

    private fun setupEnrollmentState() {
        val student = StudentRepository.currentStudent()
        val term = StudentRepository.currentTerm()

        binding.tvEnrollTerm.text = if (term.enrollmentOpen) {
            getString(R.string.enrollment_window, term.enrollmentCloses)
        } else {
            getString(R.string.enrollment_closed)
        }

        binding.btnSubmit.isEnabled = term.enrollmentOpen
        binding.btnSubmit.alpha = if (term.enrollmentOpen) 1.0f else 0.5f
        binding.btnSubmit.text = if (term.enrollmentOpen) getString(R.string.action_submit_enrollment) else "Enrollment Closed"

        // Pre-fill from the student record
        binding.tvIrregularBanner.visibility = if (student.isIrregularStudent) View.VISIBLE else View.GONE
        binding.etName.setText(student.fullName)
        binding.etMobile.setText(student.mobile)

        bindDropdown(binding.acCourse, StudentRepository.PROGRAMS, student.course)
        bindDropdown(binding.acYear, YEAR_LEVELS, student.yearLevel.toString())
        
        val dbSections = StudentRepository.getSections().map { it.name }
        val sectionOptions = if (dbSections.isNotEmpty()) dbSections else listOf("1-A", "1-B", "2-A", "2-B", "3-A")
        bindDropdown(binding.acSection, sectionOptions, student.section)

        bindDropdown(binding.acCampus, CAMPUSES, student.campus)

        subjectAdapter = OfferedSubjectAdapter(StudentRepository.offeredSubjects()) { selected ->
            updateFooter(selected)
        }
        binding.rvOffered.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOffered.adapter = subjectAdapter
        updateFooter(emptyList())

        binding.btnSubmit.setOnClickListener { submit() }
    }

    private fun bindDropdown(
        field: com.google.android.material.textfield.MaterialAutoCompleteTextView,
        options: List<String>,
        preset: String?
    ) {
        field.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, options)
        )
        val match = options.firstOrNull { it.equals(preset, ignoreCase = true) } ?: options.first()
        field.setText(match, false)
    }

    private fun updateFooter(selected: List<Subject>) {
        val units = selected.sumOf { it.units }
        val max = StudentRepository.MAX_UNITS

        binding.tvUnitCount.text = getString(R.string.unit_count, units, max)
        binding.tvUnitHint.setText(
            when {
                units > max -> R.string.unit_hint_over
                units == max -> R.string.unit_hint_full
                else -> R.string.unit_hint_ok
            }
        )

        val colorRes = if (units > max) R.color.status_fail else R.color.bpc_green
        binding.tvUnitCount.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
    }

    private fun submit() {
        val term = StudentRepository.currentTerm()
        if (!term.enrollmentOpen) {
            Snackbar.make(binding.root, "Enrollment is currently closed by the administrator.", Snackbar.LENGTH_LONG).show()
            return
        }

        val selected = subjectAdapter.selected
        val units = subjectAdapter.totalUnits

        if (binding.etName.text.isNullOrBlank()) {
            Snackbar.make(binding.root, "Enter your full name.", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (selected.isEmpty()) {
            Snackbar.make(binding.root, R.string.enroll_empty, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (units > StudentRepository.MAX_UNITS) {
            Snackbar.make(binding.root, R.string.enroll_over_load, Snackbar.LENGTH_LONG).show()
            return
        }

        val student = StudentRepository.currentStudent()
        val selectedSection = binding.acSection.text.toString().trim()
        val selectedYear = binding.acYear.text.toString().toIntOrNull() ?: 1
        val selectedCourse = binding.acCourse.text.toString().trim()
        val selectedCampus = binding.acCampus.text.toString().trim()

        // Update student record with chosen section, year level, and course
        StudentRepository.updateStudent(student.copy(
            section = selectedSection,
            yearLevel = selectedYear,
            course = selectedCourse,
            campus = selectedCampus
        ))
        StudentRepository.updateEnrolledSubjects(student.studentNo, selected)

        val summary = selected.joinToString("\n") { "${it.code} — ${it.title} (${it.units} units)" }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Enrollment ($units units - Section $selectedSection)")
            .setMessage(summary)
            .setNegativeButton("Keep editing", null)
            .setPositiveButton("File enrollment") { _, _ ->
                subjectAdapter.clearSelection()
                Snackbar.make(binding.root, R.string.enroll_submitted, Snackbar.LENGTH_LONG).show()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        val YEAR_LEVELS = listOf("1", "2", "3", "4")
        val CAMPUSES = listOf(
            "Malolos Main Campus",
            "Angat Campus",
            "Bocaue Campus",
            "Obando Campus",
            "Pandi Campus",
            "San Miguel Campus",
            "San Rafael Campus",
            "San Jose del Monte Campus"
        )
    }
}
