package ph.edu.bpc.sis

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ph.edu.bpc.sis.data.SessionManager
import ph.edu.bpc.sis.data.Student
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var session: SessionManager
    private var isTeacherSignUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etStudentNo.doOnTextChange { binding.tilStudentNo.error = null }
        binding.etTeacherSurname.doOnTextChange { binding.tilTeacherSurname.error = null }
        binding.etTeacherCode.doOnTextChange { binding.tilTeacherCode.error = null }
        binding.etPassword.doOnTextChange { binding.tilPassword.error = null }
        binding.etFirstName.doOnTextChange { binding.tilFirstName.error = null }
        binding.etLastName.doOnTextChange { binding.tilLastName.error = null }
        binding.etEmail.doOnTextChange { binding.tilEmail.error = null }

        binding.toggleGroupAccountType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isTeacherSignUp = (checkedId == R.id.btnTypeTeacher)
                updateFormVisibility()
            }
        }

        binding.btnSignUp.setOnClickListener { attemptSignUp() }
        binding.btnSignIn.setOnClickListener { finish() }
    }

    private fun updateFormVisibility() {
        if (isTeacherSignUp) {
            binding.tilStudentNo.visibility = View.GONE
            binding.tilLastName.visibility = View.GONE
            binding.tilTeacherSurname.visibility = View.VISIBLE
            binding.tilTeacherCode.visibility = View.VISIBLE
        } else {
            binding.tilStudentNo.visibility = View.VISIBLE
            binding.tilLastName.visibility = View.VISIBLE
            binding.tilTeacherSurname.visibility = View.GONE
            binding.tilTeacherCode.visibility = View.GONE
        }
    }

    private fun attemptSignUp() {
        if (isTeacherSignUp) {
            attemptTeacherSignUp()
        } else {
            attemptStudentSignUp()
        }
    }

    private fun attemptTeacherSignUp() {
        val surname = binding.etTeacherSurname.text?.toString().orEmpty().trim()
        val code = binding.etTeacherCode.text?.toString().orEmpty().trim()
        val password = binding.etPassword.text?.toString().orEmpty()
        val firstName = binding.etFirstName.text?.toString().orEmpty().trim()
        val email = binding.etEmail.text?.toString().orEmpty().trim()

        var valid = true
        if (surname.isEmpty()) {
            binding.tilTeacherSurname.error = "Enter teacher surname"
            valid = false
        }
        if (code.isEmpty()) {
            binding.tilTeacherCode.error = "Enter teacher code (e.g. ICT-8492)"
            valid = false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.err_password)
            valid = false
        }
        if (firstName.isEmpty()) {
            binding.tilFirstName.error = getString(R.string.err_first_name)
            valid = false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.err_email)
            valid = false
        }

        if (!valid) return

        if (!StudentRepository.isTeacherPreAuthorized(surname, code)) {
            binding.tilTeacherCode.error = "Surname and Teacher Code combination not found or already registered."
            return
        }

        if (StudentRepository.signUpTeacher(surname, code, firstName, email, password)) {
            val teacherUser = StudentRepository.currentStudent()
            session.open(teacherUser)
            Toast.makeText(this, "Teacher account created! Welcome.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, TeacherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        } else {
            binding.tilTeacherCode.error = "Teacher code already registered."
        }
    }

    private fun attemptStudentSignUp() {
        val studentNo = binding.etStudentNo.text?.toString().orEmpty().trim()
        val password = binding.etPassword.text?.toString().orEmpty()
        val firstName = binding.etFirstName.text?.toString().orEmpty().trim()
        val lastName = binding.etLastName.text?.toString().orEmpty().trim()
        val email = binding.etEmail.text?.toString().orEmpty().trim()

        var valid = true
        if (studentNo.isEmpty()) {
            binding.tilStudentNo.error = getString(R.string.err_student_no)
            valid = false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.err_password)
            valid = false
        }
        if (firstName.isEmpty()) {
            binding.tilFirstName.error = getString(R.string.err_first_name)
            valid = false
        }
        if (lastName.isEmpty()) {
            binding.tilLastName.error = getString(R.string.err_last_name)
            valid = false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.err_email)
            valid = false
        }

        if (!valid) return

        if (!StudentRepository.isPreAuthorized(studentNo)) {
            binding.tilStudentNo.error = "Student ID not found in admin database."
            return
        }

        val newStudent = Student(
            studentNo = studentNo,
            firstName = firstName,
            middleName = "",
            lastName = lastName,
            course = "Bachelor of Science in Information Systems (BSIS)",
            courseCode = "BSIS",
            yearLevel = 1,
            section = "1-A",
            campus = "Malolos Main Campus",
            email = email,
            mobile = "",
            address = "",
            birthDate = "",
            sex = "",
            civilStatus = "",
            guardian = "",
            guardianMobile = "",
            standing = "Regular — new student"
        )

        if (StudentRepository.signUp(newStudent, password)) {
            session.open(newStudent)
            Toast.makeText(this, R.string.sign_up_success, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        } else {
            binding.tilStudentNo.error = "Student number already registered."
        }
    }
}
