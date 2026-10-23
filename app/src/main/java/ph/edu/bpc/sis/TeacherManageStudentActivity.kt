package ph.edu.bpc.sis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.ActivityTeacherManageStudentBinding
import ph.edu.bpc.sis.ui.StudentGradesFragment
import ph.edu.bpc.sis.ui.StudentMissedActivitiesFragment
import ph.edu.bpc.sis.ui.StudentScheduleFragment
import ph.edu.bpc.sis.ui.TeacherStudentInfoFragment

class TeacherManageStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherManageStudentBinding
    var studentNo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherManageStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentNo = intent.getStringExtra("student_no") ?: ""
        val student = StudentRepository.getStudentByNo(studentNo)

        if (student == null) {
            finish()
            return
        }

        binding.toolbar.title = "${student.fullName} (${student.studentNo})"
        binding.toolbar.setNavigationOnClickListener { finish() }

        val adapter = TeacherPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Student Info"
                1 -> "Schedule"
                2 -> "Grades"
                3 -> "Missed Activities"
                else -> ""
            }
        }.attach()
    }

    class TeacherPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> TeacherStudentInfoFragment()
                1 -> StudentScheduleFragment()
                2 -> StudentGradesFragment()
                3 -> StudentMissedActivitiesFragment()
                else -> TeacherStudentInfoFragment()
            }
        }
    }
}
