package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import ph.edu.bpc.sis.ManageStudentActivity
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.FragmentStudentAcademicBinding

class StudentAcademicFragment : Fragment() {
    private var _binding: FragmentStudentAcademicBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentAcademicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val studentNo = (activity as? ManageStudentActivity)?.studentNo ?: return
        val student = StudentRepository.getStudentByNo(studentNo) ?: return

        binding.tvStudentName.text = student.fullName
        binding.tvStudentNo.text = student.studentNo

        // Course Program Dropdown
        val programsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, StudentRepository.PROGRAMS)
        binding.acCourse.setAdapter(programsAdapter)
        binding.acCourse.setText(student.course, false)

        // Course Code Dropdown
        val courseCodesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, StudentRepository.COURSE_CODES)
        binding.acCourseCode.setAdapter(courseCodesAdapter)
        binding.acCourseCode.setText(student.courseCode, false)

        // Sync Selection: Program -> Code
        binding.acCourse.setOnItemClickListener { _, _, position, _ ->
            val selectedProgTitle = StudentRepository.PROGRAMS[position]
            val matched = StudentRepository.COURSES.find { it.title == selectedProgTitle }
            if (matched != null) {
                binding.acCourseCode.setText(matched.code, false)
            }
        }

        // Sync Selection: Code -> Program
        binding.acCourseCode.setOnItemClickListener { _, _, position, _ ->
            val selectedCode = StudentRepository.COURSE_CODES[position]
            val matched = StudentRepository.COURSES.find { it.code == selectedCode }
            if (matched != null) {
                binding.acCourse.setText(matched.title, false)
            }
        }

        // Year Level Dropdown
        val yearLevels = listOf("1", "2", "3", "4")
        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, yearLevels)
        binding.acYearLevel.setAdapter(yearAdapter)
        binding.acYearLevel.setText(student.yearLevel.toString(), false)

        // Section Dropdown from Database
        val dbSections = StudentRepository.getSections()
        val sectionNames = (dbSections.map { it.name } + student.section).distinct()
        val sectionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, sectionNames)
        binding.acSection.setAdapter(sectionAdapter)
        binding.acSection.setText(student.section, false)

        binding.acSection.setOnItemClickListener { _, _, position, _ ->
            val selectedSecName = sectionNames[position]
            val matchedSec = dbSections.find { it.name == selectedSecName }
            if (matchedSec != null) {
                binding.acYearLevel.setText(matchedSec.yearLevel.toString(), false)
            }
        }

        // Enrollment Status Dropdown
        val statusOptions = listOf("Attending", "Dropped")
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, statusOptions)
        binding.acEnrollmentStatus.setAdapter(statusAdapter)
        binding.acEnrollmentStatus.setText(if (student.isDropped) "Dropped" else "Attending", false)

        // Academic Standing Dropdown
        val standings = listOf("Regular — good standing", "Regular — dean's lister", "Irregular — back subjects", "Irregular — conditional", "Probationary")
        val standingAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, standings)
        binding.acStanding.setAdapter(standingAdapter)
        binding.acStanding.setText(student.standing, false)

        binding.btnSaveAcademic.setOnClickListener {
            val newCourse = binding.acCourse.text.toString().trim().ifEmpty { student.course }
            val newCourseCode = binding.acCourseCode.text.toString().trim().ifEmpty { student.courseCode }
            val newYearLevel = binding.acYearLevel.text.toString().toIntOrNull() ?: student.yearLevel
            val newSection = binding.acSection.text.toString().trim().ifEmpty { student.section }
            val newStanding = binding.acStanding.text.toString().trim().ifEmpty { student.standing }
            val newStatus = binding.acEnrollmentStatus.text.toString().trim().ifEmpty { "Attending" }
            val isIrreg = newStanding.lowercase().contains("irregular")

            if (newCourse.isNotEmpty() && newSection.isNotEmpty()) {
                StudentRepository.updateStudent(student.copy(
                    course = newCourse,
                    courseCode = newCourseCode,
                    yearLevel = newYearLevel,
                    section = newSection,
                    standing = newStanding,
                    isIrregular = isIrreg,
                    enrollmentStatus = newStatus
                ))
                Toast.makeText(requireContext(), "Academic information updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
