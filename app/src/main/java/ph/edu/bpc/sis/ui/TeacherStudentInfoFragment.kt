package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.TeacherManageStudentActivity
import ph.edu.bpc.sis.data.Student
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.FragmentTeacherStudentInfoBinding
import ph.edu.bpc.sis.databinding.ViewDetailRowBinding

class TeacherStudentInfoFragment : Fragment() {

    private var _binding: FragmentTeacherStudentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherStudentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val studentNo = (activity as? TeacherManageStudentActivity)?.studentNo ?: return
        val student = StudentRepository.getStudentByNo(studentNo) ?: return

        fillAcademic(student)
        fillPersonal(student)
        fillGuardian(student)
    }

    private fun fillAcademic(student: Student) = with(binding.academicRows) {
        removeAllViews()
        addRow(this, "Student Number", student.studentNo)
        addDivider(this)
        addRow(this, "Program", student.course)
        addDivider(this)
        addRow(this, "Year & Section", "${student.yearLabel} · ${student.section}")
        addDivider(this)
        addRow(this, "Campus", student.campus)
        addDivider(this)
        addRow(this, "Academic Standing", student.standing)
    }

    private fun fillPersonal(student: Student) = with(binding.personalRows) {
        removeAllViews()
        addRow(this, "Full Name", student.fullName)
        addDivider(this)
        addRow(this, "Email", student.email)
        addDivider(this)
        addRow(this, "Mobile", student.mobile)
        addDivider(this)
        addRow(this, "Date of Birth", student.birthDate)
        addDivider(this)
        addRow(this, "Home Address", student.address)
    }

    private fun fillGuardian(student: Student) = with(binding.guardianRows) {
        removeAllViews()
        addRow(this, "Guardian Name", student.guardian)
        addDivider(this)
        addRow(this, "Guardian Mobile", student.guardianMobile)
    }

    private fun addRow(parent: LinearLayout, label: String, value: String) {
        val row = ViewDetailRowBinding.inflate(layoutInflater, parent, false)
        row.tvLabel.text = label
        row.tvValue.text = value.ifEmpty { "Not provided" }
        row.ivEdit.visibility = View.GONE
        row.root.isClickable = false
        parent.addView(row.root)
    }

    private fun addDivider(parent: LinearLayout) {
        val divider = View(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
            setBackgroundResource(R.color.hairline)
        }
        parent.addView(divider)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
