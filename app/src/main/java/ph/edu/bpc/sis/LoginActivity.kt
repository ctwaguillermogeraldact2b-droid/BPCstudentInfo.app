package ph.edu.bpc.sis

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.edu.bpc.sis.data.SessionManager
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.ActivityLoginBinding
import ph.edu.bpc.sis.databinding.DialogForgotPasswordBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)

        if (session.isSignedIn) {
            StudentRepository.setActiveStudent(session.studentNo)
            if (StudentRepository.isAdmin()) {
                startActivity(Intent(this, AdminActivity::class.java))
                finish()
            } else if (StudentRepository.isTeacher()) {
                startActivity(Intent(this, TeacherActivity::class.java))
                finish()
            } else {
                openDashboard()
            }
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etStudentNo.doOnTextChange { binding.tilStudentNo.error = null }
        binding.etPassword.doOnTextChange { binding.tilPassword.error = null }

        binding.btnSignIn.setOnClickListener { attemptSignIn() }
        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun showForgotPasswordDialog() {
        val dialogBinding = DialogForgotPasswordBinding.inflate(layoutInflater)
        var isTeacherReset = false

        dialogBinding.toggleResetRole.addOnButtonCheckedListener { _, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                isTeacherReset = (checkedId == R.id.btnResetTeacher)
                dialogBinding.tilResetId.hint = if (isTeacherReset) {
                    "Teacher Code (e.g. SANCHEZ-T101)"
                } else {
                    "Student Number (e.g. 2024-00417)"
                }
            }
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Reset Password")
            .setView(dialogBinding.root)
            .setPositiveButton("Reset Password", null) // Overridden below for custom validation
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val id = dialogBinding.etResetId.text?.toString().orEmpty().trim()
            val email = dialogBinding.etResetEmail.text?.toString().orEmpty().trim()
            val newPass = dialogBinding.etNewPassword.text?.toString().orEmpty()
            val confirmPass = dialogBinding.etConfirmPassword.text?.toString().orEmpty()

            var valid = true
            if (id.isEmpty()) {
                dialogBinding.tilResetId.error = "Enter ID or Teacher Code"
                valid = false
            } else {
                dialogBinding.tilResetId.error = null
            }

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                dialogBinding.tilResetEmail.error = getString(R.string.err_email)
                valid = false
            } else {
                dialogBinding.tilResetEmail.error = null
            }

            if (newPass.isEmpty()) {
                dialogBinding.tilNewPassword.error = "Enter new password"
                valid = false
            } else {
                dialogBinding.tilNewPassword.error = null
            }

            if (confirmPass != newPass) {
                dialogBinding.tilConfirmPassword.error = "Passwords do not match"
                valid = false
            } else {
                dialogBinding.tilConfirmPassword.error = null
            }

            if (!valid) return@setOnClickListener

            val success = if (isTeacherReset) {
                StudentRepository.resetTeacherPassword(id, email, newPass)
            } else {
                StudentRepository.resetStudentPassword(id, email, newPass)
            }

            if (success) {
                Toast.makeText(this, "Password reset successfully! Please sign in.", Toast.LENGTH_LONG).show()
                binding.etStudentNo.setText(id)
                binding.etPassword.setText(newPass)
                dialog.dismiss()
            } else {
                dialogBinding.tilResetEmail.error = "Account ID/Code and Email do not match our records."
            }
        }
    }

    private fun attemptSignIn() {
        val studentNo = binding.etStudentNo.text?.toString().orEmpty().trim()
        val password = binding.etPassword.text?.toString().orEmpty()

        var valid = true
        if (studentNo.isEmpty()) {
            binding.tilStudentNo.error = getString(R.string.err_student_no)
            valid = false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.err_password)
            valid = false
        }
        if (!valid) return

        val student = StudentRepository.signIn(studentNo, password)
        if (student == null) {
            binding.tilPassword.error = getString(R.string.err_credentials)
            return
        }

        session.open(student)
        if (StudentRepository.isAdmin()) {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        } else if (StudentRepository.isTeacher()) {
            startActivity(Intent(this, TeacherActivity::class.java))
            finish()
        } else {
            openDashboard()
        }
    }

    private fun openDashboard() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
