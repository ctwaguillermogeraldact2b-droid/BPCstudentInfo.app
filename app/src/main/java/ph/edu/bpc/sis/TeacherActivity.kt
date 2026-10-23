package ph.edu.bpc.sis

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.edu.bpc.sis.data.SessionManager
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.data.StudentMissedActivity
import ph.edu.bpc.sis.data.TeacherAssignment
import ph.edu.bpc.sis.databinding.ActivityTeacherBinding
import ph.edu.bpc.sis.databinding.ItemMissedActivityBinding
import ph.edu.bpc.sis.ui.StudentAdapter
import ph.edu.bpc.sis.ui.TeacherAssignmentAdapter
import java.io.File
import java.io.FileOutputStream

class TeacherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherBinding
    private lateinit var session: SessionManager
    private lateinit var adapter: StudentAdapter
    private lateinit var scheduleAdapter: TeacherAssignmentAdapter
    private lateinit var activitiesAdapter: TeacherMissedActivitiesAdapter

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            saveAndSetProfilePicture(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        binding = ActivityTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        refreshTeacherHeader()

        binding.flTeacherAvatar.setOnClickListener {
            showProfilePictureOptions()
        }

        scheduleAdapter = TeacherAssignmentAdapter { assignment ->
            showEditScheduleTimeDialog(assignment)
        }
        binding.rvSectionSchedules.layoutManager = LinearLayoutManager(this)
        binding.rvSectionSchedules.adapter = scheduleAdapter

        activitiesAdapter = TeacherMissedActivitiesAdapter { studentActivity ->
            val intent = Intent(this, TeacherManageStudentActivity::class.java)
            intent.putExtra("student_no", studentActivity.student.studentNo)
            startActivity(intent)
        }
        binding.rvStudentActivities.layoutManager = LinearLayoutManager(this)
        binding.rvStudentActivities.adapter = activitiesAdapter

        adapter = StudentAdapter { student ->
            val intent = Intent(this, TeacherManageStudentActivity::class.java)
            intent.putExtra("student_no", student.studentNo)
            startActivity(intent)
        }

        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = adapter

        val currentTeacher = StudentRepository.currentStudent()
        val teacherCode = currentTeacher.studentNo

        val myAssignments = StudentRepository.getTeacherAssignments(teacherCode)
        val sectionOptions = listOf("All Sections") + myAssignments.map { it.section }.distinct()
        binding.acSectionFilter.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, sectionOptions))
        binding.acSectionFilter.setText("All Sections", false)

        binding.acSectionFilter.setOnItemClickListener { _, _, _, _ ->
            refreshStudentList(teacherCode)
        }
        binding.acSectionFilter.doOnTextChanged { _, _, _, _ ->
            refreshStudentList(teacherCode)
        }

        binding.etSearch.doOnTextChanged { _, _, _, _ ->
            refreshStudentList(teacherCode)
        }

        binding.btnSignOut.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Sign out of Teacher Portal?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Sign out") { _, _ ->
                    session.close()
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        refreshAllData(teacherCode)
    }

    private fun refreshTeacherHeader() {
        val currentTeacher = StudentRepository.currentStudent()
        binding.tvTeacherName.text = currentTeacher.fullName
        binding.tvTeacherInitials.text = currentTeacher.initials

        val pictureUriString = currentTeacher.profilePictureUri
        if (!pictureUriString.isNullOrEmpty()) {
            try {
                val uri = pictureUriString.toUri()
                val filePath = if (uri.scheme == "file") uri.path ?: pictureUriString else pictureUriString
                if (File(filePath).exists()) {
                    val bitmap = BitmapFactory.decodeFile(filePath)
                    if (bitmap != null) {
                        binding.ivTeacherProfile.setImageBitmap(bitmap)
                        binding.ivTeacherProfile.visibility = View.VISIBLE
                        binding.tvTeacherInitials.visibility = View.GONE
                    } else {
                        binding.ivTeacherProfile.visibility = View.GONE
                        binding.tvTeacherInitials.visibility = View.VISIBLE
                    }
                } else {
                    binding.ivTeacherProfile.visibility = View.GONE
                    binding.tvTeacherInitials.visibility = View.VISIBLE
                }
            } catch (_: Exception) {
                binding.ivTeacherProfile.visibility = View.GONE
                binding.tvTeacherInitials.visibility = View.VISIBLE
            }
        } else {
            binding.ivTeacherProfile.visibility = View.GONE
            binding.tvTeacherInitials.visibility = View.VISIBLE
        }
    }

    private fun showProfilePictureOptions() {
        val currentTeacher = StudentRepository.currentStudent()
        val options = if (!currentTeacher.profilePictureUri.isNullOrEmpty()) {
            arrayOf("Choose from Gallery", "Remove Photo")
        } else {
            arrayOf("Choose from Gallery")
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageLauncher.launch("image/*")
                    1 -> removeProfilePicture()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveAndSetProfilePicture(sourceUri: Uri) {
        val currentTeacher = StudentRepository.currentStudent()
        try {
            val file = File(filesDir, "profile_teacher_${currentTeacher.studentNo}.jpg")
            contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            val savedUriString = file.toURI().toString()
            StudentRepository.updateTeacherProfilePicture(currentTeacher.studentNo, savedUriString)
            refreshTeacherHeader()
            Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeProfilePicture() {
        val currentTeacher = StudentRepository.currentStudent()
        currentTeacher.profilePictureUri?.let { uriString ->
            try {
                val uri = uriString.toUri()
                if (uri.scheme == "file") {
                    val file = File(uri.path ?: "")
                    if (file.exists()) file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        StudentRepository.updateTeacherProfilePicture(currentTeacher.studentNo, null)
        refreshTeacherHeader()
        Toast.makeText(this, "Profile picture removed", Toast.LENGTH_SHORT).show()
    }

    private fun refreshAllData(teacherCode: String) {
        val myAssignments = StudentRepository.getTeacherAssignments(teacherCode)
        scheduleAdapter.submitList(myAssignments)

        val studentActivities = StudentRepository.getMissedActivitiesForTeacher(teacherCode)
        activitiesAdapter.submitList(studentActivities)

        refreshStudentList(teacherCode)
    }

    private fun refreshStudentList(teacherCode: String) {
        val query = binding.etSearch.text?.toString().orEmpty().lowercase()
        val selectedSection = binding.acSectionFilter.text?.toString().orEmpty().trim().ifEmpty { "All Sections" }
        val assignedStudents = StudentRepository.getStudentsForTeacher(teacherCode)
        val filtered = assignedStudents.filter { student ->
            val matchesSection = selectedSection == "All Sections" || student.section.equals(selectedSection, ignoreCase = true)
            val matchesQuery = query.isEmpty() ||
                student.studentNo.lowercase().contains(query) ||
                student.fullName.lowercase().contains(query) ||
                student.section.lowercase().contains(query) ||
                student.courseCode.lowercase().contains(query)
            matchesSection && matchesQuery
        }.sortedWith(compareBy({ it.section }, { it.lastName }))

        adapter.submitList(filtered)
    }

    private fun showEditScheduleTimeDialog(assignment: TeacherAssignment) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_teacher_assignment, null)
        val acTeacherCode = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.acTeacherCode)
        val acSection = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.acSection)
        val acSubjectCode = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.acSubjectCode)
        val acMeetingTime = view.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.acMeetingTime)

        acTeacherCode.setText(assignment.teacherCode, false)
        acSection.setText(assignment.section, false)
        acSubjectCode.setText(assignment.subjectCode, false)
        acMeetingTime.setText(assignment.meetingTime, false)

        acTeacherCode.isEnabled = false
        acSection.isEnabled = false
        acSubjectCode.isEnabled = false

        val timeList = listOf("08:00 - 10:00 AM", "10:00 - 12:00 PM", "01:00 - 03:00 PM", "03:00 - 05:00 PM", "MWF 8:00–9:00", "TTh 9:00–10:30", "MW 13:00–14:30")
        acMeetingTime.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, timeList))

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Schedule Time (${assignment.section} - ${assignment.subjectCode})")
            .setView(view)
            .setPositiveButton("Save Time") { _, _ ->
                val newTime = acMeetingTime.text?.toString().orEmpty().trim().ifEmpty { assignment.meetingTime }
                val updated = assignment.copy(meetingTime = newTime)
                StudentRepository.updateTeacherAssignment(updated)
                val currentTeacher = StudentRepository.currentStudent()
                refreshAllData(currentTeacher.studentNo)
                Toast.makeText(this, "Schedule time updated to $newTime", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        val currentTeacher = StudentRepository.currentStudent()
        refreshTeacherHeader()
        refreshAllData(currentTeacher.studentNo)
    }
}

class TeacherMissedActivitiesAdapter(
    private val onClick: (StudentMissedActivity) -> Unit
) : RecyclerView.Adapter<TeacherMissedActivitiesAdapter.Holder>() {
    private var list: List<StudentMissedActivity> = emptyList()

    fun submitList(newList: List<StudentMissedActivity>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemMissedActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
        holder.binding.tvSubjectCode.text = "${item.student.fullName} (${item.student.section}) · ${item.activity.subjectCode}"
        holder.binding.tvTitle.text = item.activity.activityTitle
        holder.binding.tvDueDate.text = "Due: ${item.activity.dueDate}"
        holder.binding.tvStatus.text = item.activity.status
        holder.binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = list.size

    class Holder(val binding: ItemMissedActivityBinding) : RecyclerView.ViewHolder(binding.root)
}
