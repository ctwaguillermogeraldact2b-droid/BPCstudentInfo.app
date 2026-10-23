package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.edu.bpc.sis.ManageStudentActivity
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.TeacherManageStudentActivity
import ph.edu.bpc.sis.data.ClassSession
import ph.edu.bpc.sis.data.Day
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.FragmentStudentScheduleBinding
import ph.edu.bpc.sis.databinding.ItemScheduleBinding

class StudentScheduleFragment : Fragment() {
    private var _binding: FragmentStudentScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StudentScheduleAdapter

    private val currentStudentNo: String?
        get() = (activity as? TeacherManageStudentActivity)?.studentNo
            ?: (activity as? ManageStudentActivity)?.studentNo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val studentNo = currentStudentNo ?: return

        adapter = StudentScheduleAdapter(
            onItemClick = { session -> showAddEditScheduleDialog(studentNo, session) },
            onItemLongClick = { session -> showDeleteScheduleDialog(studentNo, session) }
        )
        binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedule.adapter = adapter

        binding.btnAddSchedule.setOnClickListener {
            showAddEditScheduleDialog(studentNo, null)
        }
        refresh(studentNo)
    }

    private fun refresh(studentNo: String) {
        adapter.submitList(StudentRepository.schedule(studentNo))
    }

    private fun showAddEditScheduleDialog(studentNo: String, existing: ClassSession?) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_schedule, null)
        val acCode = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.acCode)
        val acTitle = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.acTitle)
        val acDay = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.acDay)
        val acStart = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.acStart)
        val acEnd = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.acEnd)
        val acRoom = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.acRoom)
        val acInstructor = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.acInstructor)

        val offeredSubjects = StudentRepository.offeredSubjects()
        val codeList = offeredSubjects.map { it.code }
        val titleList = offeredSubjects.map { it.title }
        val dayNames = Day.entries.map { it.full }
        val timeList = listOf("08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM")
        val roomList = listOf("CL 1", "CL 2", "CL 3", "Net Lab", "Rm 204", "Rm 208", "Rm 301", "Rm 105", "Gym")
        val instructorList = StudentRepository.getAllTeacherCodes()

        acCode.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, codeList))
        acTitle.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, titleList))
        acDay.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dayNames))
        acStart.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, timeList))
        acEnd.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, timeList))
        acRoom.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, roomList))
        acInstructor.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, instructorList))

        // Sync Code <-> Title
        acCode.setOnItemClickListener { _, _, position, _ ->
            val code = codeList[position]
            val matched = offeredSubjects.find { it.code == code }
            if (matched != null) {
                acTitle.setText(matched.title, false)
            }
        }
        acTitle.setOnItemClickListener { _, _, position, _ ->
            val title = titleList[position]
            val matched = offeredSubjects.find { it.title == title }
            if (matched != null) {
                acCode.setText(matched.code, false)
            }
        }

        if (existing != null) {
            acCode.setText(existing.code, false)
            acTitle.setText(existing.title, false)
            acDay.setText(existing.day.full, false)
            acStart.setText(existing.start, false)
            acEnd.setText(existing.end, false)
            acRoom.setText(existing.room, false)
            acInstructor.setText(existing.instructor, false)
        } else {
            acCode.setText(codeList.firstOrNull() ?: "", false)
            acTitle.setText(titleList.firstOrNull() ?: "", false)
            acDay.setText(Day.MON.full, false)
            acStart.setText("08:00 AM", false)
            acEnd.setText("10:00 AM", false)
            acRoom.setText("CL 1", false)
            acInstructor.setText(instructorList.firstOrNull() ?: "Faculty", false)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (existing == null) "Add Schedule & Time" else "Edit Schedule & Time")
            .setView(view)
            .setPositiveButton(if (existing == null) "Add" else "Update") { _, _ ->
                val code = acCode.text?.toString().orEmpty().trim()
                val title = acTitle.text?.toString().orEmpty().trim()
                val selectedDayStr = acDay.text?.toString().orEmpty().trim()
                val start = acStart.text?.toString().orEmpty().trim().ifEmpty { "08:00 AM" }
                val end = acEnd.text?.toString().orEmpty().trim().ifEmpty { "10:00 AM" }
                val room = acRoom.text?.toString().orEmpty().trim().ifEmpty { "CL 1" }
                val instructor = acInstructor.text?.toString().orEmpty().trim().ifEmpty { "Faculty" }

                val matchedDay = Day.entries.firstOrNull { it.full.equals(selectedDayStr, ignoreCase = true) } ?: Day.MON

                if (code.isNotEmpty() && title.isNotEmpty()) {
                    val newSession = ClassSession(
                        day = matchedDay,
                        start = start,
                        end = end,
                        code = code,
                        title = title,
                        room = room,
                        instructor = instructor
                    )

                    val current = StudentRepository.schedule(studentNo).toMutableList()
                    if (existing != null) {
                        val index = current.indexOf(existing)
                        if (index != -1) {
                            current[index] = newSession
                        }
                    } else {
                        current.add(newSession)
                    }

                    StudentRepository.updateSchedule(studentNo, current)
                    refresh(studentNo)
                    Toast.makeText(requireContext(), "Schedule updated ($code: $start - $end)", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please select subject code and title", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteScheduleDialog(studentNo: String, session: ClassSession) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Schedule?")
            .setMessage("Remove ${session.code} (${session.start} - ${session.end}) from schedule?")
            .setPositiveButton("Delete") { _, _ ->
                val current = StudentRepository.schedule(studentNo).toMutableList()
                current.remove(session)
                StudentRepository.updateSchedule(studentNo, current)
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

class StudentScheduleAdapter(
    private val onItemClick: (ClassSession) -> Unit,
    private val onItemLongClick: (ClassSession) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<StudentScheduleAdapter.Holder>() {
    private var list: List<ClassSession> = emptyList()

    fun submitList(newList: List<ClassSession>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val session = list[position]
        holder.binding.tvStart.text = session.start
        holder.binding.tvEnd.text = session.end
        holder.binding.tvSubject.text = "${session.code} ${session.title}"
        holder.binding.tvMeta.text = "${session.day.full} | ${session.room} | ${session.instructor}"

        holder.binding.root.setOnClickListener {
            onItemClick(session)
        }
        holder.binding.root.setOnLongClickListener {
            onItemLongClick(session)
            true
        }
    }

    override fun getItemCount() = list.size

    class Holder(val binding: ItemScheduleBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
}
