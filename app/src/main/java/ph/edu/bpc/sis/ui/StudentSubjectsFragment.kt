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
import ph.edu.bpc.sis.data.GradeRecord
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.data.Subject
import ph.edu.bpc.sis.databinding.FragmentStudentSubjectsBinding
import ph.edu.bpc.sis.databinding.ItemOfferedSubjectBinding

class StudentSubjectsFragment : Fragment() {
    private var _binding: FragmentStudentSubjectsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StudentSubjectsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val studentNo = (activity as? ManageStudentActivity)?.studentNo ?: return

        adapter = StudentSubjectsAdapter(
            onItemClick = { subject -> showGradeActionDialog(studentNo, subject) },
            onItemLongClick = { subject -> showUnenrollDialog(studentNo, subject) }
        )
        binding.rvSubjects.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSubjects.adapter = adapter

        binding.btnEnrollSubject.setOnClickListener {
            showEnrollDialog(studentNo)
        }
        refresh(studentNo)
    }

    private fun refresh(studentNo: String) {
        adapter.submitList(StudentRepository.enrolledSubjects(studentNo))
    }

    private fun showEnrollDialog(studentNo: String) {
        val offered = StudentRepository.offeredSubjects()
        val items = offered.map { "${it.code} ${it.title}" }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Enroll in Subject")
            .setItems(items) { _, which ->
                val selected = offered[which]
                val current = StudentRepository.enrolledSubjects(studentNo).toMutableList()
                if (current.none { it.code == selected.code }) {
                    current.add(selected)
                    StudentRepository.updateEnrolledSubjects(studentNo, current)
                    refresh(studentNo)
                } else {
                    Toast.makeText(requireContext(), "Already enrolled", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showGradeActionDialog(studentNo: String, subject: Subject) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(subject.code)
            .setMessage("Set final grade for this enrolled subject?")
            .setPositiveButton("Set Grade") { _, _ ->
                val view = LayoutInflater.from(requireContext()).inflate(ph.edu.bpc.sis.R.layout.dialog_add_grade, null)
                val etCode = view.findViewById<com.google.android.material.textfield.TextInputEditText>(ph.edu.bpc.sis.R.id.etCode)
                val etTitle = view.findViewById<com.google.android.material.textfield.TextInputEditText>(ph.edu.bpc.sis.R.id.etTitle)
                val etUnits = view.findViewById<com.google.android.material.textfield.TextInputEditText>(ph.edu.bpc.sis.R.id.etUnits)
                val etGrade = view.findViewById<com.google.android.material.textfield.TextInputEditText>(ph.edu.bpc.sis.R.id.etGrade)

                etCode.setText(subject.code)
                etTitle.setText(subject.title)
                etUnits.setText(subject.units.toString())

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Add Grade Record")
                    .setView(view)
                    .setPositiveButton("Add") { _, _ ->
                        val code = etCode.text.toString().trim()
                        val title = etTitle.text.toString().trim()
                        val units = etUnits.text.toString().toIntOrNull() ?: subject.units
                        val gradeVal = etGrade.text.toString().toDoubleOrNull()

                        if (code.isNotEmpty() && title.isNotEmpty()) {
                            val newGrade = GradeRecord(code, title, units, null, null, gradeVal)
                            val currentGrades = StudentRepository.grades(studentNo).toMutableList()
                            currentGrades.add(newGrade)
                            StudentRepository.updateGrades(studentNo, currentGrades)
                            Toast.makeText(requireContext(), "Grade added", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showUnenrollDialog(studentNo: String, subject: Subject) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Unenroll?")
            .setMessage("Remove ${subject.code} from current enrollment?")
            .setPositiveButton("Unenroll") { _, _ ->
                val current = StudentRepository.enrolledSubjects(studentNo).toMutableList()
                current.removeAll { it.code == subject.code }
                StudentRepository.updateEnrolledSubjects(studentNo, current)
                refresh(studentNo)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        val studentNo = (activity as? ManageStudentActivity)?.studentNo ?: return
        refresh(studentNo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class StudentSubjectsAdapter(
    private val onItemClick: (Subject) -> Unit,
    private val onItemLongClick: (Subject) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<StudentSubjectsAdapter.Holder>() {
    private var list: List<Subject> = emptyList()

    fun submitList(newList: List<Subject>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemOfferedSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val subject = list[position]
        holder.binding.cbSelect.visibility = View.GONE
        holder.binding.tvCode.text = subject.code
        holder.binding.tvTitle.text = subject.title
        holder.binding.tvUnits.text = holder.itemView.context.resources.getQuantityString(ph.edu.bpc.sis.R.plurals.units, subject.units, subject.units)
        holder.binding.tvMeta.text = "${subject.startTime} - ${subject.endTime} | Room: ${subject.room}"
        holder.binding.tvSlots.visibility = View.GONE

        holder.binding.root.setOnClickListener { onItemClick(subject) }
        holder.binding.root.setOnLongClickListener {
            onItemLongClick(subject)
            true
        }
    }

    override fun getItemCount() = list.size

    class Holder(val binding: ItemOfferedSubjectBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
}
