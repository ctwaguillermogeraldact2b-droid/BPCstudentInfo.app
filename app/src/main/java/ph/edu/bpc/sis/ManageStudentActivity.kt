package ph.edu.bpc.sis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.ActivityManageStudentBinding
import ph.edu.bpc.sis.ui.StudentAcademicFragment
import ph.edu.bpc.sis.ui.StudentGradesFragment
import ph.edu.bpc.sis.ui.StudentScheduleFragment
import ph.edu.bpc.sis.ui.StudentSubjectsFragment

class ManageStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageStudentBinding
    var studentNo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentNo = intent.getStringExtra("student_no") ?: ""
        val student = StudentRepository.getStudentByNo(studentNo)

        if (student == null) {
            finish()
            return
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        val adapter = StudentPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Academic Info"
                1 -> "Subjects"
                2 -> "Grades"
                3 -> "Schedule"
                else -> ""
            }
        }.attach()
    }

    class StudentPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> StudentAcademicFragment()
                1 -> StudentSubjectsFragment()
                2 -> StudentGradesFragment()
                3 -> StudentScheduleFragment()
                else -> StudentAcademicFragment()
            }
        }
    }
}
