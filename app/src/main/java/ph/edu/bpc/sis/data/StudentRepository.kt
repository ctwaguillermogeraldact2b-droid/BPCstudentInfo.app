package ph.edu.bpc.sis.data

/**
 * Single source of truth for the app.
 *
 * Every method returns hard-coded sample data so the UI runs with no backend.
 * To go live, keep these signatures and make each one call the BPC endpoint
 * (Retrofit + a suspend modifier is the usual next step).
 */
object StudentRepository {

    private const val DEMO_STUDENT_NO = "2024-00417"
    private const val DEMO_PASSWORD = "bpc2026"

    private const val ADMIN_USER = "admin"
    private const val ADMIN_PASS = "admin123"

    private const val TEACHER_USER = "teacher"
    private const val TEACHER_PASS = "teacher123"

    val CLUSTERS = listOf(
        "ICT Cluster (Information & Communication Technology)",
        "LANG Cluster (Languages)",
        "HB Cluster (Hospitality / Business)",
        "ASC Cluster (Arts, Sciences / Academic subjects)",
        "SKP Cluster (Social Sciences / Knowledge & Professional subjects)"
    )

    val COURSES = listOf(
        CourseProgram("BSIS", "Bachelor of Science in Information Systems (BSIS)"),
        CourseProgram("BSOM", "Bachelor of Science in Office Management (BSOM)"),
        CourseProgram("BSAIS", "Bachelor of Science in Accounting Information System (BSAIS)"),
        CourseProgram("BTVTED", "Bachelor of Technical Vocational Teacher Education (BTVTED)"),
        CourseProgram("BSCA", "Bachelor of Science in Customs Administration (BSCA)"),
        CourseProgram("ACT", "Associate in Computer Technology (ACT) 2-Year"),
        CourseProgram("CS", "2-Year Computer Secretarial (CS) with competency in Bookkeeping NC III"),
        CourseProgram("HRS", "2-Year Hotel and Restaurant Services (HRS) with competencies in Housekeeping, FBS, Cookery NC II"),
        CourseProgram("CCM", "Call Center Management (CCM)"),
        CourseProgram("CIT", "Certificate in Industrial Technology (CIT) major in Electrical")
    )

    val PROGRAMS = COURSES.map { it.title }
    val COURSE_CODES = COURSES.map { it.code }

    private val sections = mutableListOf(
        SectionInfo("1-A", "Bachelor of Science in Information Systems (BSIS)", 1),
        SectionInfo("1-B", "Bachelor of Science in Information Systems (BSIS)", 1),
        SectionInfo("2-A", "Bachelor of Science in Information Systems (BSIS)", 2),
        SectionInfo("2-B", "Bachelor of Science in Information Systems (BSIS)", 2),
        SectionInfo("3-A", "Bachelor of Science in Information Systems (BSIS)", 3)
    )

    private val actStudentsList = listOf(
        Triple("MA-25011147", "Karen", "Abnay"),
        Triple("MA-25011137", "Jisecca", "Anigan"),
        Triple("MA-25011034", "Leo", "Balao"),
        Triple("MA-25011044", "Jeny Lyn", "Banag"),
        Triple("MA-25011130", "Lee Jane", "Burayag"),
        Triple("MA-25011122", "Adrian Cris", "Cabillo"),
        Triple("MA-25011125", "Millet", "Cabrera"),
        Triple("MA-25011133", "Bernadette", "Carpio"),
        Triple("MA-25011032", "Kurt Aien", "Censon"),
        Triple("MA-25011019", "Irish Jane", "Comawas"),
        Triple("MA-25011149", "Nathan", "Cruz"),
        Triple("MA-25011150", "Reinna", "Dela Cruz"),
        Triple("MA-25011636", "John Benedict", "Domagco"),
        Triple("MA-25011031", "Stephanie", "Erag"),
        Triple("MA-25011144", "Alliana Rose", "Fernando"),
        Triple("MA-250011026", "Charlotte", "Galuyo"),
        Triple("MA-25011148", "Kim Angel", "Garcia"),
        Triple("MA-25011128", "Marvin", "Garcia"),
        Triple("MA-25011054", "Vena", "Gerona"),
        Triple("MA-25011049", "John Denver", "Gonzales"),
        Triple("MA-25011021", "Joshua", "Infante"),
        Triple("MA-25011151", "Andrea", "Lagdamin"),
        Triple("MA-25011043", "Justine", "Lazaro"),
        Triple("MA-25011131", "Cristan", "Lecira"),
        Triple("MA-25011044-B", "Andrea", "Magugat"),
        Triple("MA-25011040", "Jose", "Mansilungan"),
        Triple("MA-25011146", "Julius", "Michilina"),
        Triple("MA-25011135", "Nichul Ann", "Negrido"),
        Triple("MA-25011152", "Juliana Diane", "Quetua"),
        Triple("MA-25011134", "Mica Ella", "Reyes"),
        Triple("MA-25011143", "Calvin John", "Rivas"),
        Triple("MA-25011141", "Jean", "Sintos"),
        Triple("MA-25011023", "Liezel", "Tayo"),
        Triple("MA-25011037", "Edwin Joseph", "Tontoco"),
        Triple("MA-25011132", "James Arvene", "Ventura"),
        Triple("MA-25011140", "Jazzmiline", "Villanueva"),
        Triple("MA-25011121", "Oscar Gabriel", "Yadao"),
        Triple("MA-25011138", "Mary Grace", "Zuniga")
    )

    private val preAuthorizedIds = mutableListOf<AuthorizedId>().apply {
        add(AuthorizedId("MA-25011126", firstName = "Gerald", lastName = "Guillermo", course = "Bachelor of Science in Information Systems (BSIS)", courseCode = "BSIS", yearLevel = 2, section = "2-A", isSignedUp = true))
        add(AuthorizedId(id = DEMO_STUDENT_NO, isSignedUp = true))
        add(AuthorizedId("2024-00001"))
        add(AuthorizedId("2024-00002"))
        for (item in actStudentsList) {
            add(AuthorizedId(item.first, firstName = item.second, lastName = item.third, course = "Associate in Computer Technology (ACT)", courseCode = "ACT", yearLevel = 2, section = "2-B", isSignedUp = true))
        }
    }

    private val preAuthorizedTeacherCodes = mutableListOf(
        // ICT Cluster
        AuthorizedTeacherCode("Victoria", "Paulo A., MIT", "ICT-8492", CLUSTERS[0], isClusterHead = true, isSignedUp = true),
        AuthorizedTeacherCode("Gatchalian", "Miguel Don G.", "ICT-3910", CLUSTERS[0], isSignedUp = true),
        AuthorizedTeacherCode("Tolentino", "Mauro C.", "ICT-1048", CLUSTERS[0], isSignedUp = true),
        AuthorizedTeacherCode("Tamayo", "Ferdinand R.", "ICT-5729", CLUSTERS[0], isSignedUp = true),
        AuthorizedTeacherCode("Santos", "Reynaldo P.", "ICT-9301", CLUSTERS[0], isSignedUp = true),
        AuthorizedTeacherCode("Cruz", "Joana Marie M.", "ICT-2847", CLUSTERS[0], isSignedUp = true),
        AuthorizedTeacherCode("Robles", "Deserie S.", "ICT-6153", CLUSTERS[0], isSignedUp = true),
        AuthorizedTeacherCode("Fulgencio", "Ai-vee Pamela Aleine O.", "ICT-4820", CLUSTERS[0], isSignedUp = true),
        AuthorizedTeacherCode("Viola", "Francis Luigi", "ICT-7294", CLUSTERS[0], isSignedUp = true),
        AuthorizedTeacherCode("Leyesa", "Niño Azer C.", "ICT-3185", CLUSTERS[0], isSignedUp = true),
        AuthorizedTeacherCode("Ramos", "Carl Ashlie C.", "ICT-9502", CLUSTERS[0], isSignedUp = true),
        AuthorizedTeacherCode("Fabian", "Kyle Joseph G.", "ICT-1638", CLUSTERS[0], isSignedUp = true),

        // LANG Cluster
        AuthorizedTeacherCode("Quiambao", "Ma. Jessica R., RGC, LPT", "LANG-5921", CLUSTERS[1], isClusterHead = true, isSignedUp = true),
        AuthorizedTeacherCode("Bautista", "Melannie, DLLE, MALLE", "LANG-1834", CLUSTERS[1], isSignedUp = true),
        AuthorizedTeacherCode("Castro", "Edgardo C. Jr., DBA, MDIV", "LANG-7402", CLUSTERS[1], isSignedUp = true),
        AuthorizedTeacherCode("Cruz", "Anthony Ashley V.", "LANG-3958", CLUSTERS[1], isSignedUp = true),
        AuthorizedTeacherCode("Dela Cruz", "Paolien Marie G.", "LANG-8219", CLUSTERS[1], isSignedUp = true),
        AuthorizedTeacherCode("Dy", "Patricia P.", "LANG-4073", CLUSTERS[1], isSignedUp = true),
        AuthorizedTeacherCode("Mallari", "Mariella R., LPT", "LANG-6184", CLUSTERS[1], isSignedUp = true),
        AuthorizedTeacherCode("Orillosa", "Delilah U.", "LANG-2950", CLUSTERS[1], isSignedUp = true),
        AuthorizedTeacherCode("Ostonal", "Cindy S.", "LANG-8361", CLUSTERS[1], isSignedUp = true),
        AuthorizedTeacherCode("Padilla", "Khevin", "LANG-1492", CLUSTERS[1], isSignedUp = true),
        AuthorizedTeacherCode("Pineda", "Joseph John S., MBA", "LANG-5028", CLUSTERS[1], isSignedUp = true),
        AuthorizedTeacherCode("Santos", "Farrah Mae S.", "LANG-9137", CLUSTERS[1], isSignedUp = true),
        AuthorizedTeacherCode("Velasco", "Ma. Ana Cordella R.", "LANG-3740", CLUSTERS[1], isSignedUp = true),
        AuthorizedTeacherCode("Victoria", "Flormina J.", "LANG-6281", CLUSTERS[1], isSignedUp = true),

        // HB Cluster
        AuthorizedTeacherCode("Dianco", "Pia Amor P., MAEd", "HB-4829", CLUSTERS[2], isClusterHead = true, isSignedUp = true),
        AuthorizedTeacherCode("Amaninche", "Eliseo O., PhD", "HB-1935", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Angeles", "Jiorelle Edmylaine B.", "HB-8502", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Caino", "Sharmaine A.", "HB-3714", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Capule", "Arianne Allein", "HB-9261", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Castillo", "Arjay C., LPT", "HB-5048", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("De Alba", "Beverlyn Rose B., LPT", "HB-1639", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Dela Cruz", "Kolene Louise T.", "HB-7382", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Dela Cruz", "Victor T. II", "HB-2941", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Genovia", "Mareon F.", "HB-6105", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Giron", "Airon John V.", "HB-8472", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Giron", "Arman M., MAT", "HB-3519", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Guevarra", "Samuel John F.", "HB-9028", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Hagen", "Rosemarie R.", "HB-4183", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Hernandez", "Bernadette Linn O.", "HB-7620", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Ladislao", "Marcelo D., PhD", "HB-2394", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Mangilit", "Marc Angelo P.", "HB-8157", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Marasigan", "John Harell P.", "HB-3401", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Marcelo", "Ivy L.", "HB-6925", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Ompoc", "Rommel R.", "HB-1583", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Perez", "Miguel", "HB-7049", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Recondo", "Mikhaela DC.", "HB-2816", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Ramos", "Monica Camil H.", "HB-9372", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Raymundo", "Ana Melissa A.", "HB-4508", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Raymundo", "Norman D.", "HB-8163", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Santos", "Anne O.", "HB-3295", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Taguinod", "Edwarson V., LPT", "HB-7410", CLUSTERS[2], isSignedUp = true),
        AuthorizedTeacherCode("Tretasco", "Nemencio V.", "HB-1836", CLUSTERS[2], isSignedUp = true),

        // ASC Cluster
        AuthorizedTeacherCode("Mendoza", "Marissa B., MSS", "ASC-8204", CLUSTERS[3], isClusterHead = true, isSignedUp = true),
        AuthorizedTeacherCode("Amaninche", "Estrellita M.", "ASC-3918", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Austria", "Juanito C.", "ASC-7461", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Ambata", "Eleonor V.", "ASC-2059", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Bautista", "Niño Carlo C.", "ASC-6183", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Casilan", "Yuri S.", "ASC-9402", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Coquilla", "Jay Carlo V.", "ASC-1537", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Cristobal", "Jocelyn V.", "ASC-5829", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Dasmariñas", "Ma. Luz B., EdD, PhD, DBA", "ASC-3190", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Dedal", "Dane Christopher R., RPm", "ASC-8742", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Fajardo", "Ma. Bella S.", "ASC-2615", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Iñigo", "Airene N., CPA, DBA", "ASC-6391", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Lagmay", "Rommel P.", "ASC-4082", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Luarca", "Oswald C.", "ASC-9174", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Mañebo", "Ferdinand R.", "ASC-1825", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Marasigan", "Christopher Jamil SP.", "ASC-5390", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Martin", "Sarah Joy D.", "ASC-7204", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Morillo", "Marites D., MBA", "ASC-3648", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Natividad", "Jennet M.", "ASC-8913", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Palisoc", "Gloria C.", "ASC-4251", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Paradero", "Christian C.", "ASC-6038", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Ruano", "Eugene A.", "ASC-1792", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Santos", "Blanche Jasmin Rae S.", "ASC-9520", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Santos", "Churkie B.", "ASC-3184", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Santos", "Ma. Cecilia L.", "ASC-7841", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Santos", "Rodbert M.", "ASC-2409", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Sotto", "Jene T., PhD", "ASC-6152", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Valenzuela", "Crizza Joi T.", "ASC-8390", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Victoria", "Ciara Christiana", "ASC-4917", CLUSTERS[3], isSignedUp = true),
        AuthorizedTeacherCode("Villamoran", "Erlinda P., PhD", "ASC-1603", CLUSTERS[3], isSignedUp = true),

        // SKP Cluster
        AuthorizedTeacherCode("Santos", "Gregoria M., MAEd", "SKP-7391", CLUSTERS[4], isClusterHead = true, isSignedUp = true),
        AuthorizedTeacherCode("Alde", "Aldrin R., LPT", "SKP-2840", CLUSTERS[4], isSignedUp = true),
        AuthorizedTeacherCode("Cunanan", "Bryan Paul", "SKP-6159", CLUSTERS[4], isSignedUp = true),
        AuthorizedTeacherCode("De Guzman", "Renato, MAEd", "SKP-1043", CLUSTERS[4], isSignedUp = true),
        AuthorizedTeacherCode("De Jesus", "Melody P., MM", "SKP-8372", CLUSTERS[4], isSignedUp = true),
        AuthorizedTeacherCode("Maroto", "Flordeliza C., MAEd", "SKP-3915", CLUSTERS[4], isSignedUp = true),
        AuthorizedTeacherCode("Reyes", "Eroll T., LPT", "SKP-9204", CLUSTERS[4], isSignedUp = true),
        AuthorizedTeacherCode("Rueda", "Julie T.", "SKP-4817", CLUSTERS[4], isSignedUp = true),
        AuthorizedTeacherCode("Santiago", "Eric C., LPT", "SKP-6530", CLUSTERS[4], isSignedUp = true),
        AuthorizedTeacherCode("Santos", "Jeffrey DS.", "SKP-1928", CLUSTERS[4], isSignedUp = true),
        AuthorizedTeacherCode("Santos", "Lalaine C., MAEd, LPT", "SKP-5401", CLUSTERS[4], isSignedUp = true)
    )

    private val teachers = preAuthorizedTeacherCodes.map {
        Teacher(
            teacherCode = it.code,
            surname = it.surname,
            firstName = it.firstName.ifEmpty { "Prof." },
            email = "${it.surname.lowercase().replace(" ", "")}@bpc.edu.ph",
            department = it.department,
            isClusterHead = it.isClusterHead
        )
    }.toMutableList()

    private val teacherPasswords = preAuthorizedTeacherCodes.associate {
        it.code to "teacher123"
    }.toMutableMap().apply {
        put(TEACHER_USER, TEACHER_PASS)
        put("teacher", "teacher123")
    }

    private val students = mutableListOf<Student>().apply {
        add(
            Student(
                studentNo = "MA-25011126",
                firstName = "Gerald",
                middleName = "",
                lastName = "Guillermo",
                course = "Bachelor of Science in Information Systems (BSIS)",
                courseCode = "BSIS",
                yearLevel = 2,
                section = "2-A",
                campus = "Malolos Main Campus",
                email = "gerald.guillermo@bpc.edu.ph",
                mobile = "0917 555 9999",
                address = "City of Malolos, Bulacan",
                birthDate = "1 January 2005",
                sex = "Male",
                civilStatus = "Single",
                guardian = "Parent / Guardian",
                guardianMobile = "0918 555 8888",
                standing = "Regular — good standing",
                isIrregular = false,
                enrollmentStatus = "Attending"
            )
        )
        for (item in actStudentsList) {
            add(
                Student(
                    studentNo = item.first,
                    firstName = item.second,
                    middleName = "",
                    lastName = item.third,
                    course = "Associate in Computer Technology (ACT)",
                    courseCode = "ACT",
                    yearLevel = 2,
                    section = "2-B",
                    campus = "Malolos Main Campus",
                    email = "${item.second.lowercase().replace(" ", "")}.${item.third.lowercase().replace(" ", "")}@bpc.edu.ph",
                    mobile = "0917 555 1234",
                    address = "Bulacan",
                    birthDate = "15 May 2005",
                    sex = if (listOf("Karen", "Jisecca", "Jeny Lyn", "Lee Jane", "Millet", "Bernadette", "Irish Jane", "Reinna", "Stephanie", "Alliana Rose", "Charlotte", "Kim Angel", "Vena", "Andrea", "Justine", "Nichul Ann", "Juliana Diane", "Mica Ella", "Jean", "Liezel", "Jazzmiline", "Mary Grace").contains(item.second)) "Female" else "Male",
                    civilStatus = "Single",
                    guardian = "Parent / Guardian",
                    guardianMobile = "0918 555 8888",
                    standing = "Regular — good standing",
                    isIrregular = false,
                    enrollmentStatus = "Attending"
                )
            )
        }
    }

    private val passwords = mutableMapOf<String, String>().apply {
        put("MA-25011126", "bpc2026")
        for (item in actStudentsList) {
            put(item.first, "bpc2026")
        }
    }

    private val studentGrades = mutableMapOf<String, MutableList<GradeRecord>>()

    private val studentSchedules = mutableMapOf<String, MutableList<ClassSession>>()

    private val studentEnrolledSubjects = mutableMapOf<String, MutableList<Subject>>()

    private val studentMissedActivities = mutableMapOf<String, MutableList<MissedActivity>>()

    private val collegeAnnouncements = mutableListOf(
        Announcement(
            "Enrollment for First Semester closes 30 September",
            "Finish your subject selection in the app, then have it validated at the Registrar window before the cut-off. Late filing needs a dean's approval.",
            "15 September 2026",
            "Office of the Registrar"
        ),
        Announcement(
            "Midterm examination week moved to 20–24 October",
            "The one-week shift covers all eight campuses. Your instructors will post the revised coverage in class.",
            "12 September 2026",
            "Academic Affairs"
        ),
        Announcement(
            "Free Wi-Fi upgrade at the Malolos computer laboratories",
            "CL 1 to CL 3 now run on the new fibre line. Bring your BPC email account to log in.",
            "8 September 2026",
            "MIS Office"
        )
    )

    private var activeStudentNo: String? = null

    /** Replace with a real POST /auth/login. Returns null when credentials fail. */
    fun signIn(studentNo: String, password: String): Student? {
        val normalized = studentNo.trim()
        if ((normalized == ADMIN_USER) && (password == ADMIN_PASS)) {
            activeStudentNo = ADMIN_USER
            return adminUser()
        }
        if ((normalized == TEACHER_USER) && (password == TEACHER_PASS)) {
            activeStudentNo = TEACHER_USER
            return teacherUser()
        }

        // Check registered teachers
        val teacher = teachers.find { it.teacherCode.equals(normalized, ignoreCase = true) || it.surname.equals(normalized, ignoreCase = true) }
        if (teacher != null && (teacherPasswords[teacher.teacherCode] == password || password == "teacher123")) {
            activeStudentNo = teacher.teacherCode
            return teacherUser(teacher)
        }

        val student = students.find { it.studentNo.equals(normalized, ignoreCase = true) }
        return if (student != null && (passwords[student.studentNo] == password || password == DEMO_PASSWORD)) {
            activeStudentNo = student.studentNo
            student
        } else {
            null
        }
    }

    fun resetStudentPassword(studentNo: String, email: String, newPassword: String): Boolean {
        val normNo = studentNo.trim()
        if (normNo.isEmpty()) return false
        val student = students.find { it.studentNo.equals(normNo, ignoreCase = true) }
            ?: students.firstOrNull { it.studentNo == DEMO_STUDENT_NO } ?: return false

        passwords[student.studentNo] = newPassword
        return true
    }

    fun resetTeacherPassword(teacherCode: String, email: String, newPassword: String): Boolean {
        val normCode = teacherCode.trim()
        if (normCode.isEmpty()) return false
        val teacher = teachers.find {
            it.teacherCode.equals(normCode, ignoreCase = true) ||
            it.surname.equals(normCode, ignoreCase = true) ||
            normCode.equals(TEACHER_USER, ignoreCase = true)
        } ?: teachers.firstOrNull() ?: return false

        teacherPasswords[teacher.teacherCode] = newPassword
        teacherPasswords[TEACHER_USER] = newPassword
        return true
    }

    private fun teacherUser(teacher: Teacher? = null): Student {
        val t = teacher ?: teachers.first()
        return Student(
            studentNo = t.teacherCode,
            firstName = t.firstName,
            middleName = "",
            lastName = t.surname,
            course = "Faculty / Instructor (${t.department})",
            courseCode = "FAC",
            yearLevel = 0,
            section = "FAC",
            campus = "All Campuses",
            email = t.email,
            mobile = "",
            address = "",
            birthDate = "",
            sex = "N/A",
            civilStatus = "N/A",
            guardian = "",
            guardianMobile = "",
            standing = if (t.isClusterHead) "Cluster Head Access" else "Faculty Access",
            profilePictureUri = t.profilePictureUri
        )
    }

    fun updateTeacherProfilePicture(teacherCode: String, uriString: String?) {
        val idx = teachers.indexOfFirst { it.teacherCode.equals(teacherCode, ignoreCase = true) }
        if (idx != -1) {
            teachers[idx] = teachers[idx].copy(profilePictureUri = uriString)
        }
    }

    private fun adminUser() = Student(
        studentNo = ADMIN_USER,
        firstName = "System",
        middleName = "",
        lastName = "Administrator",
        course = "Administrator",
        courseCode = "ADMIN",
        yearLevel = 0,
        section = "SYS",
        campus = "All Campuses",
        email = "admin@bpc.edu.ph",
        mobile = "",
        address = "",
        birthDate = "",
        sex = "N/A",
        civilStatus = "N/A",
        guardian = "",
        guardianMobile = "",
        standing = "Admin Access"
    )

    fun isAdmin() = activeStudentNo == ADMIN_USER
    fun isTeacher() = activeStudentNo == TEACHER_USER || teachers.any { it.teacherCode == activeStudentNo }

    fun isPreAuthorized(studentNo: String): Boolean {
        val normalized = studentNo.trim()
        return preAuthorizedIds.any { it.id == normalized && !it.isSignedUp }
    }

    fun isTeacherPreAuthorized(surname: String, code: String): Boolean {
        val normSurname = surname.trim()
        val normCode = code.trim()
        return preAuthorizedTeacherCodes.any {
            it.surname.equals(normSurname, ignoreCase = true) &&
            it.code.equals(normCode, ignoreCase = true) &&
            !it.isSignedUp
        }
    }

    fun getAuthorizedTeacherCode(surname: String, code: String): AuthorizedTeacherCode? {
        val normSurname = surname.trim()
        val normCode = code.trim()
        return preAuthorizedTeacherCodes.find {
            it.surname.equals(normSurname, ignoreCase = true) &&
            it.code.equals(normCode, ignoreCase = true)
        }
    }

    fun getAuthorizedTeacherCodes(cluster: String? = null): List<AuthorizedTeacherCode> {
        if (cluster.isNullOrEmpty() || cluster.equals("All Clusters", ignoreCase = true)) {
            return preAuthorizedTeacherCodes.toList()
        }
        val norm = cluster.trim().lowercase()
        val keyword = when {
            norm.contains("ict") -> "ict"
            norm.contains("lang") -> "lang"
            norm.contains("hb") -> "hb"
            norm.contains("asc") -> "asc"
            norm.contains("skp") -> "skp"
            else -> norm
        }
        return preAuthorizedTeacherCodes.filter {
            val dept = it.department.lowercase()
            dept.contains(keyword) || dept.contains(norm) || norm.contains(dept) || it.code.lowercase().startsWith(keyword)
        }
    }

    fun getAllTeacherCodes(): List<String> {
        val registered = teachers.map { "${it.teacherCode} (${it.surname}, ${it.firstName}) - ${it.department}" }
        val auth = preAuthorizedTeacherCodes.map { "${it.code} (${it.surname}, ${it.firstName}) - ${it.department}" }
        return (registered + auth).distinct()
    }

    fun addPreAuthorizedTeacherCode(
        surname: String,
        firstName: String,
        code: String,
        department: String = CLUSTERS[0],
        isClusterHead: Boolean = false
    ): Boolean {
        val normSurname = surname.trim()
        val normCode = code.trim()
        if (normSurname.isEmpty() || normCode.isEmpty()) return false
        if (preAuthorizedTeacherCodes.any { it.surname.equals(normSurname, ignoreCase = true) && it.code.equals(normCode, ignoreCase = true) }) return false

        preAuthorizedTeacherCodes.add(0, AuthorizedTeacherCode(
            surname = normSurname,
            firstName = firstName.trim(),
            code = normCode,
            department = department,
            isClusterHead = isClusterHead,
            isSignedUp = true
        ))
        teachers.add(Teacher(
            teacherCode = normCode,
            surname = normSurname,
            firstName = firstName.trim().ifEmpty { "Prof." },
            email = "${normSurname.lowercase().replace(" ", "")}@bpc.edu.ph",
            department = department,
            isClusterHead = isClusterHead
        ))
        teacherPasswords[normCode] = "teacher123"
        return true
    }

    fun removePreAuthorizedTeacherCode(surname: String, code: String) {
        val normSurname = surname.trim()
        val normCode = code.trim()
        preAuthorizedTeacherCodes.removeAll {
            it.surname.equals(normSurname, ignoreCase = true) && it.code.equals(normCode, ignoreCase = true)
        }
        teachers.removeAll { it.teacherCode.equals(normCode, ignoreCase = true) }
    }

    fun signUpTeacher(surname: String, code: String, firstName: String, email: String, password: String): Boolean {
        val normSurname = surname.trim()
        val normCode = code.trim()

        val index = preAuthorizedTeacherCodes.indexOfFirst {
            it.surname.equals(normSurname, ignoreCase = true) &&
            it.code.equals(normCode, ignoreCase = true)
        }

        val authItem = if (index != -1) preAuthorizedTeacherCodes[index] else AuthorizedTeacherCode(surname = normSurname, firstName = firstName, code = normCode, isSignedUp = true)
        if (index != -1) {
            preAuthorizedTeacherCodes[index] = authItem.copy(isSignedUp = true)
        } else {
            preAuthorizedTeacherCodes.add(authItem)
        }

        if (!teachers.any { it.teacherCode.equals(normCode, ignoreCase = true) }) {
            teachers.add(Teacher(
                teacherCode = normCode,
                surname = normSurname,
                firstName = firstName.ifEmpty { authItem.firstName },
                email = email.ifEmpty { "${normSurname.lowercase()}@bpc.edu.ph" },
                department = authItem.department,
                isClusterHead = authItem.isClusterHead
            ))
        }
        teacherPasswords[normCode] = password
        activeStudentNo = normCode
        return true
    }

    fun addPreAuthorizedId(studentNo: String): Boolean {
        val normalized = studentNo.trim()
        if (normalized.isEmpty() || preAuthorizedIds.any { it.id.equals(normalized, ignoreCase = true) }) return false
        preAuthorizedIds.add(0, AuthorizedId(normalized))
        return true
    }

    fun removePreAuthorizedId(studentNo: String) {
        preAuthorizedIds.removeAll { it.id.equals(studentNo, ignoreCase = true) }
    }

    fun getAuthorizedIds(): List<AuthorizedId> = preAuthorizedIds.toList()

    fun signUp(student: Student, password: String): Boolean {
        val normalized = student.studentNo.trim()
        val authIndex = preAuthorizedIds.indexOfFirst { it.id.equals(normalized, ignoreCase = true) }
        if (authIndex != -1) {
            preAuthorizedIds[authIndex] = preAuthorizedIds[authIndex].copy(isSignedUp = true)
        } else {
            preAuthorizedIds.add(AuthorizedId(normalized, student.firstName, student.lastName, "", "", 0, "", true))
        }
        
        val pendingStudent = student.copy(
            section = "",
            yearLevel = 0,
            course = "Pending Admin Assignment",
            courseCode = "PENDING",
            standing = "Awaiting Admin Section & Year Assignment"
        )

        val existingIndex = students.indexOfFirst { it.studentNo.equals(normalized, ignoreCase = true) }
        if (existingIndex != -1) {
            students[existingIndex] = pendingStudent
        } else {
            students.add(pendingStudent)
        }

        passwords[student.studentNo] = password
        activeStudentNo = student.studentNo
        
        // Initialize empty records
        studentGrades[student.studentNo] = mutableListOf()
        studentSchedules[student.studentNo] = mutableListOf()
        studentEnrolledSubjects[student.studentNo] = mutableListOf()
        studentMissedActivities[student.studentNo] = mutableListOf()
        
        return true
    }

    fun getSections(): List<SectionInfo> = sections.toList()

    fun addSection(section: SectionInfo): Boolean {
        if (sections.any { it.name.equals(section.name, ignoreCase = true) }) return false
        sections.add(section)
        return true
    }

    fun deleteSection(name: String) {
        sections.removeAll { it.name.equals(name, ignoreCase = true) }
    }

    fun getAllStudents(): List<Student> = students.filter { it.studentNo != ADMIN_USER && !teachers.any { t -> t.teacherCode == it.studentNo } && it.studentNo != TEACHER_USER }

    fun getStudentByNo(studentNo: String): Student? = students.find { it.studentNo.equals(studentNo, ignoreCase = true) }

    fun updateStudent(student: Student) {
        val index = students.indexOfFirst { it.studentNo.equals(student.studentNo, ignoreCase = true) }
        if (index != -1) {
            students[index] = student
        }
    }

    fun updateStudentEnrollmentStatus(studentNo: String, status: String) {
        val index = students.indexOfFirst { it.studentNo.equals(studentNo, ignoreCase = true) }
        if (index != -1) {
            students[index] = students[index].copy(enrollmentStatus = status)
        }
    }

    fun currentStudent(): Student {
        if (activeStudentNo.equals(ADMIN_USER, ignoreCase = true)) return adminUser()
        val teacher = teachers.find { it.teacherCode.equals(activeStudentNo, ignoreCase = true) }
        if (teacher != null || activeStudentNo.equals(TEACHER_USER, ignoreCase = true)) {
            return teacherUser(teacher)
        }
        return students.find { it.studentNo.equals(activeStudentNo, ignoreCase = true) } ?: students.first()
    }

    fun setActiveStudent(studentNo: String?) {
        activeStudentNo = studentNo
    }

    private var isEnrollmentOpen: Boolean = true

    fun isEnrollmentOpen(): Boolean = isEnrollmentOpen
    fun setEnrollmentOpen(open: Boolean) {
        isEnrollmentOpen = open
    }

    fun currentTerm(): Term = Term(
        academicYear = "2026–2027",
        semester = "First",
        enrollmentOpen = isEnrollmentOpen,
        enrollmentCloses = "30 September 2026"
    )

    fun enrolledSubjects(studentNo: String = activeStudentNo ?: ""): List<Subject> {
        return studentEnrolledSubjects[studentNo] ?: emptyList()
    }

    fun updateEnrolledSubjects(studentNo: String, subjects: List<Subject>) {
        studentEnrolledSubjects[studentNo] = subjects.toMutableList()
    }

    private val offeredSubjectsList = mutableListOf(
        Subject("IT 204", "Information Management", 3, 2, 1, "CL 3", 12),
        Subject("IT 205", "Networking 1", 3, 2, 1, "Net Lab", 5),
        Subject("IT 206", "Web Systems and Technologies", 3, 2, 1, "CL 1", 18),
        Subject("IT 207", "Quantitative Methods", 3, 3, 0, "Rm 301", 21),
        Subject("GE 06", "Science, Technology and Society", 3, 3, 0, "Rm 204", 9),
        Subject("GE 07", "Ethics", 3, 3, 0, "Rm 208", 0),
        Subject("PE 03", "Individual and Dual Sports", 2, 2, 0, "Gym", 30),
        Subject("NSTP 03", "CWTS 3", 3, 3, 0, "Rm 105", 25)
    )

    fun offeredSubjects(): List<Subject> = offeredSubjectsList.toList()

    fun addOfferedSubject(subject: Subject) {
        offeredSubjectsList.add(subject)
    }

    fun updateOfferedSubject(oldCode: String, subject: Subject) {
        val index = offeredSubjectsList.indexOfFirst { it.code == oldCode }
        if (index != -1) {
            offeredSubjectsList[index] = subject
        } else {
            offeredSubjectsList.add(subject)
        }
    }

    fun deleteOfferedSubject(code: String) {
        offeredSubjectsList.removeAll { it.code == code }
    }

    /** Grades for a specific student. */
    fun grades(studentNo: String = activeStudentNo ?: ""): List<GradeRecord> {
        return studentGrades[studentNo] ?: emptyList()
    }

    /** Schedule for a specific student. */
    fun schedule(studentNo: String = activeStudentNo ?: ""): List<ClassSession> {
        return studentSchedules[studentNo] ?: emptyList()
    }

    /** Missed activities for a specific student. */
    fun missedActivities(studentNo: String = activeStudentNo ?: ""): List<MissedActivity> {
        return studentMissedActivities[studentNo] ?: emptyList()
    }

    fun addMissedActivity(studentNo: String, activity: MissedActivity) {
        val list = studentMissedActivities.getOrPut(studentNo) { mutableListOf() }
        list.add(0, activity)
    }

    fun updateMissedActivity(studentNo: String, activity: MissedActivity) {
        val list = studentMissedActivities[studentNo] ?: return
        val index = list.indexOfFirst { it.id == activity.id }
        if (index != -1) {
            list[index] = activity
        }
    }

    fun deleteMissedActivity(studentNo: String, activityId: String) {
        val list = studentMissedActivities[studentNo] ?: return
        list.removeAll { it.id == activityId }
    }

    fun updateGrades(studentNo: String, grades: List<GradeRecord>) {
        studentGrades[studentNo] = grades.toMutableList()
    }

    fun updateSchedule(studentNo: String, schedule: List<ClassSession>) {
        studentSchedules[studentNo] = schedule.toMutableList()
    }

    fun announcements(): List<Announcement> = collegeAnnouncements.toList()

    fun addAnnouncement(announcement: Announcement) {
        collegeAnnouncements.add(0, announcement)
    }

    fun deleteAnnouncement(announcement: Announcement) {
        collegeAnnouncements.remove(announcement)
    }

    fun updateAnnouncement(old: Announcement, new: Announcement) {
        val index = collegeAnnouncements.indexOf(old)
        if (index != -1) {
            collegeAnnouncements[index] = new
        }
    }

    /** Weighted general average. Units are the weights, so a 5-unit subject counts more. */
    fun generalAverage(records: List<GradeRecord>): Double? {
        val graded = records.filter { it.finalGrade != null }
        if (graded.isEmpty()) return null
        val totalUnits = graded.sumOf { it.units }
        if (totalUnits == 0) return null
        val weighted = graded.sumOf { (it.finalGrade ?: 0.0) * it.units }
        return weighted / totalUnits
    }

    private val teacherAssignments = mutableListOf(
        TeacherAssignment(
            teacherCode = "ICT-8492",
            section = "2-A",
            subjectCode = "IT 201"
        ),
        TeacherAssignment(
            teacherCode = "ICT-3910",
            section = "2-B",
            subjectCode = "IT 205"
        ),
        TeacherAssignment(
            teacherCode = "LANG-5921",
            section = "1-A",
            subjectCode = "GE 05"
        )
    )

    fun getAllTeacherAssignments(): List<TeacherAssignment> = teacherAssignments.toList()

    fun getTeacherAssignments(teacherCode: String): List<TeacherAssignment> {
        return teacherAssignments.filter { it.teacherCode.equals(teacherCode, ignoreCase = true) }
    }

    fun addTeacherAssignment(assignment: TeacherAssignment): Boolean {
        if (teacherAssignments.any {
            it.teacherCode.equals(assignment.teacherCode, ignoreCase = true) &&
            it.section.equals(assignment.section, ignoreCase = true) &&
            it.subjectCode.equals(assignment.subjectCode, ignoreCase = true)
        }) return false

        teacherAssignments.add(0, assignment)
        return true
    }

    fun removeTeacherAssignment(id: String) {
        teacherAssignments.removeAll { it.id == id }
    }

    fun updateTeacherAssignment(assignment: TeacherAssignment): Boolean {
        val index = teacherAssignments.indexOfFirst { it.id == assignment.id }
        if (index != -1) {
            teacherAssignments[index] = assignment
            return true
        }
        return false
    }

    fun getStudentsForTeacher(teacherCode: String): List<Student> {
        val assignments = getTeacherAssignments(teacherCode)
        if (assignments.isEmpty()) {
            return emptyList()
        }
        return getAllStudents().filter { student ->
            assignments.any { assignment ->
                assignment.section.equals(student.section, ignoreCase = true)
            }
        }
    }

    fun getMissedActivitiesForTeacher(teacherCode: String): List<StudentMissedActivity> {
        val students = getStudentsForTeacher(teacherCode)
        val list = mutableListOf<StudentMissedActivity>()
        for (student in students) {
            val acts = missedActivities(student.studentNo)
            for (act in acts) {
                list.add(StudentMissedActivity(student, act))
            }
        }
        return list
    }

    fun getEarnedUnits(studentNo: String = activeStudentNo ?: ""): Int {
        val records = studentGrades[studentNo] ?: emptyList()
        return records.filter { it.finalGrade != null && it.finalGrade <= 3.0 }.sumOf { it.units }
    }

    fun getRemainingUnits(studentNo: String = activeStudentNo ?: ""): Int {
        val student = getStudentByNo(studentNo) ?: return 148
        val earned = getEarnedUnits(studentNo)
        return (student.totalProgramUnits - earned).coerceAtLeast(0)
    }

    const val MAX_UNITS = 24
}
