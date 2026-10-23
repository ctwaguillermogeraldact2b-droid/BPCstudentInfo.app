package ph.edu.bpc.sis.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.data.Student
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.FragmentProfileBinding
import ph.edu.bpc.sis.databinding.ViewDetailRowBinding
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    /** MainActivity handles the actual sign-out so the back stack is cleared properly. */
    interface SignOutListener {
        fun onSignOut()
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var listener: SignOutListener? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { saveAndSetProfilePicture(it) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? SignOutListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        refreshUi()

        binding.flAvatarContainer.setOnClickListener {
            showProfilePictureOptions()
        }

        binding.btnSignOut.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sign out of BPC Student?")
                .setMessage("You'll need your student number and password to get back in.")
                .setNegativeButton("Stay signed in", null)
                .setPositiveButton(R.string.action_sign_out) { _, _ -> listener?.onSignOut() }
                .show()
        }
    }

    private fun showProfilePictureOptions() {
        val student = StudentRepository.currentStudent()
        val options = if (!student.profilePictureUri.isNullOrEmpty()) {
            arrayOf("Choose from Gallery", "Remove Photo")
        } else {
            arrayOf("Choose from Gallery")
        }

        MaterialAlertDialogBuilder(requireContext())
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
        val student = StudentRepository.currentStudent()
        try {
            val context = requireContext()
            val file = File(context.filesDir, "profile_${student.studentNo}.jpg")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            val savedUriString = file.toURI().toString()
            val updatedStudent = student.copy(profilePictureUri = savedUriString)
            StudentRepository.updateStudent(updatedStudent)
            refreshUi()
            Toast.makeText(context, "Profile picture updated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeProfilePicture() {
        val student = StudentRepository.currentStudent()
        student.profilePictureUri?.let { uriString ->
            try {
                val uri = uriString.toUri()
                if (uri.scheme == "file") {
                    val file = File(uri.path ?: "")
                    if (file.exists()) {
                        file.delete()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val updatedStudent = student.copy(profilePictureUri = null)
        StudentRepository.updateStudent(updatedStudent)
        refreshUi()
        Toast.makeText(requireContext(), "Profile picture removed", Toast.LENGTH_SHORT).show()
    }

    private fun refreshUi() {
        val student = StudentRepository.currentStudent()

        displayProfilePicture(student)
        binding.tvName.text = student.fullName
        binding.tvStudentNo.text = student.studentNo
        binding.tvStanding.text = student.standing

        binding.academicRows.removeAllViews()
        binding.personalRows.removeAllViews()
        binding.guardianRows.removeAllViews()

        fillAcademic(student)
        fillPersonal(student)
        fillGuardian(student)
    }

    private fun displayProfilePicture(student: Student) {
        binding.tvInitials.text = student.initials
        val pictureUriString = student.profilePictureUri
        if (!pictureUriString.isNullOrEmpty()) {
            try {
                val uri = pictureUriString.toUri()
                val filePath = if (uri.scheme == "file") uri.path ?: pictureUriString else pictureUriString
                if (File(filePath).exists()) {
                    val bitmap = BitmapFactory.decodeFile(filePath)
                    if (bitmap != null) {
                        binding.ivProfilePicture.setImageBitmap(bitmap)
                        binding.ivProfilePicture.visibility = View.VISIBLE
                        binding.tvInitials.visibility = View.GONE
                        return
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.ivProfilePicture.visibility = View.GONE
        binding.tvInitials.visibility = View.VISIBLE
    }

    private fun fillAcademic(student: Student) = with(binding.academicRows) {
        addRow(this, "Program", student.course)
        addDivider(this)
        addRow(this, "Year and section", "${student.yearLabel} · ${student.section}")
        addDivider(this)
        addRow(this, "Academic standing", student.standing, false)
        addDivider(this)
        addRow(this, "Campus", student.campus)
        addDivider(this)
        addRow(this, "BPC email", student.email, true) { newValue ->
            StudentRepository.updateStudent(student.copy(email = newValue))
            refreshUi()
        }
    }

    private fun fillPersonal(student: Student) = with(binding.personalRows) {
        addRow(this, "Date of birth", student.birthDate, true) { newValue ->
            StudentRepository.updateStudent(student.copy(birthDate = newValue))
            refreshUi()
        }
        addDivider(this)
        addRow(this, "Sex", student.sex, true) { newValue ->
            StudentRepository.updateStudent(student.copy(sex = newValue))
            refreshUi()
        }
        addDivider(this)
        addRow(this, "Civil status", student.civilStatus, true) { newValue ->
            StudentRepository.updateStudent(student.copy(civilStatus = newValue))
            refreshUi()
        }
        addDivider(this)
        addRow(this, "Mobile", student.mobile, true) { newValue ->
            StudentRepository.updateStudent(student.copy(mobile = newValue))
            refreshUi()
        }
        addDivider(this)
        addRow(this, "Home address", student.address, true) { newValue ->
            StudentRepository.updateStudent(student.copy(address = newValue))
            refreshUi()
        }
    }

    private fun fillGuardian(student: Student) = with(binding.guardianRows) {
        addRow(this, "Parent or guardian", student.guardian, true) { newValue ->
            StudentRepository.updateStudent(student.copy(guardian = newValue))
            refreshUi()
        }
        addDivider(this)
        addRow(this, "Contact number", student.guardianMobile, true) { newValue ->
            StudentRepository.updateStudent(student.copy(guardianMobile = newValue))
            refreshUi()
        }
    }

    private fun addRow(
        parent: LinearLayout,
        label: String,
        value: String,
        editable: Boolean = false,
        onUpdate: (String) -> Unit = {}
    ) {
        val row = ViewDetailRowBinding.inflate(layoutInflater, parent, false)
        row.tvLabel.text = label
        row.tvValue.text = value.ifEmpty { "Not set" }
        row.ivEdit.visibility = if (editable) View.VISIBLE else View.GONE

        if (editable) {
            row.root.setOnClickListener {
                showEditDialog(label, value, onUpdate)
            }
        } else {
            row.root.isClickable = false
        }

        parent.addView(row.root)
    }

    private fun showEditDialog(label: String, currentValue: String, onUpdate: (String) -> Unit) {
        val input = com.google.android.material.textfield.TextInputEditText(requireContext()).apply {
            setText(currentValue)
            hint = label
            setSelection(text?.length ?: 0)
        }
        val container = android.widget.FrameLayout(requireContext()).apply {
            val margin = (16 * resources.displayMetrics.density).toInt()
            setPadding(margin, margin / 2, margin, 0)
            addView(input)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit $label")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                onUpdate(input.text?.toString().orEmpty().trim())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addDivider(parent: LinearLayout) {
        val divider = View(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
            setBackgroundResource(R.color.hairline)
        }
        parent.addView(divider)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
