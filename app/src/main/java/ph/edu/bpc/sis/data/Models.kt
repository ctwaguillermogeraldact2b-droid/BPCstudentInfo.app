package ph.edu.bpc.sis.data

/**
 * Plain data holders. Nothing here touches the UI, so when you swap the mock
 * repository for the real BPC API you only change [StudentRepository].
 */

data class Student(
    val studentNo: String,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val course: String,
    val courseCode: String,
    val yearLevel: Int,
    val section: String,
    val campus: String,
    val email: String,
    val mobile: String,
    val address: String,
    val birthDate: String,
    val sex: String,
    val civilStatus: String,
    val guardian: String,
    val guardianMobile: String,
    val standing: String,
    val profilePictureUri: String? = null,
    val isIrregular: Boolean = false,
    val enrollmentStatus: String = "Attending" // "Attending" or "Dropped"
) {
    val isIrregularStudent: Boolean get() = isIrregular || standing.lowercase().contains("irregular")
    val isDropped: Boolean get() = enrollmentStatus.equals("Dropped", ignoreCase = true)
    val fullName: String get() = "$firstName $middleName $lastName".replace("\\s+".toRegex(), " ").trim()
    val shortName: String get() = "$firstName $lastName".trim()
    val initials: String
        get() = "${firstName.firstOrNull() ?: ' '}${lastName.firstOrNull() ?: ' '}".trim().uppercase()
    val yearLabel: String
        get() = when (yearLevel) {
            1 -> "1st Year"
            2 -> "2nd Year"
            3 -> "3rd Year"
            else -> "${yearLevel}th Year"
        }
    val totalProgramUnits: Int get() = 148
}

data class CourseProgram(
    val code: String,
    val title: String
)

/** A subject in the curriculum. [units] drives every load and GWA calculation. */
data class Subject(
    val code: String,
    val title: String,
    val units: Int,
    val lecUnits: Int,
    val labUnits: Int,
    val room: String,
    val slotsLeft: Int,
    val hoursPerWeek: Double = 3.0,
    val subjectType: String = "Major", // "Major" or "Minor"
    val startTime: String = "07:00 AM",
    val endTime: String = "09:30 AM",
    val cluster: String = "ICT Cluster (Information & Communication Technology)"
) {
    val hoursLabel: String get() = if (hoursPerWeek == 1.5) "1 and 1/2 hrs" else "${hoursPerWeek.toInt()} hrs"
    val designationLabel: String get() = "$subjectType — $hoursLabel"
}

/** One graded subject for a finished or ongoing term. */
data class GradeRecord(
    val code: String,
    val title: String,
    val units: Int,
    val midterm: Double?,
    val finals: Double?,
    val finalGrade: Double?
) {
    /** Philippine 1.00–5.00 scale: 1.00 is highest, 3.00 is the passing floor. */
    val remarks: String
        get() = when {
            finalGrade == null -> "In progress"
            finalGrade <= 3.00 -> "Passed"
            finalGrade == 4.00 -> "Conditional"
            else -> "Failed"
        }
}

data class ClassSession(
    val day: Day,
    val start: String,
    val end: String,
    val code: String,
    val title: String,
    val room: String,
    val instructor: String
)

enum class Day(val short: String, val full: String) {
    MON("Mon", "Monday"),
    TUE("Tue", "Tuesday"),
    WED("Wed", "Wednesday"),
    THU("Thu", "Thursday"),
    FRI("Fri", "Friday"),
    SAT("Sat", "Saturday")
}

data class Announcement(
    val title: String,
    val body: String,
    val postedOn: String,
    val source: String
)

data class Term(
    val academicYear: String,
    val semester: String,
    val enrollmentOpen: Boolean,
    val enrollmentCloses: String
) {
    val label: String get() = "$semester Semester, AY $academicYear"
}

data class AuthorizedId(
    val id: String,
    val firstName: String = "",
    val lastName: String = "",
    val course: String = "Bachelor of Science in Information Systems (BSIS)",
    val courseCode: String = "BSIS",
    val yearLevel: Int = 1,
    val section: String = "1-A",
    val isSignedUp: Boolean = false
) {
    val studentName: String
        get() = if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
            "$firstName $lastName".trim()
        } else {
            "Pending Registration"
        }
}

data class AuthorizedTeacherCode(
    val surname: String,
    val firstName: String = "",
    val code: String,
    val department: String = "ICT Cluster (Information & Communication Technology)",
    val isClusterHead: Boolean = false,
    val isSignedUp: Boolean = false
) {
    val displayName: String get() = if (firstName.isNotEmpty()) "$firstName $surname" else surname
    val titleLabel: String get() = if (isClusterHead) "Cluster Head" else "Faculty"
}

data class Teacher(
    val teacherCode: String,
    val surname: String,
    val firstName: String,
    val email: String,
    val department: String = "ICT Cluster (Information & Communication Technology)",
    val isClusterHead: Boolean = false,
    val profilePictureUri: String? = null
)

data class SectionInfo(
    val name: String,
    val program: String = "Bachelor of Science in Information Systems (BSIS)",
    val yearLevel: Int = 1
)

data class MissedActivity(
    val id: String = java.util.UUID.randomUUID().toString(),
    val subjectCode: String,
    val activityTitle: String,
    val dueDate: String,
    val status: String = "Pending", // "Pending", "Completed", "Excused"
    val remarks: String = ""
)

data class StudentMissedActivity(
    val student: Student,
    val activity: MissedActivity
)

data class TeacherAssignment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val teacherCode: String,
    val section: String,
    val subjectCode: String,
    val meetingTime: String = "08:00 - 10:00 AM"
)
