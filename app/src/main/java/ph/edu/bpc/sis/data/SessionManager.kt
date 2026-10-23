package ph.edu.bpc.sis.data

import android.content.Context
import androidx.core.content.edit

/**
 * Remembers who is signed in between launches.
 * Stores only the student number — never the password.
 */
class SessionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("bpc_session", Context.MODE_PRIVATE)

    var studentNo: String?
        get() = prefs.getString(KEY_STUDENT_NO, null)
        private set(value) = prefs.edit { putString(KEY_STUDENT_NO, value) }

    val isSignedIn: Boolean get() = studentNo != null

    fun open(student: Student) {
        studentNo = student.studentNo
    }

    fun close() {
        prefs.edit { clear() }
    }

    private companion object {
        const val KEY_STUDENT_NO = "student_no"
    }
}
