package ph.edu.bpc.sis

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import ph.edu.bpc.sis.data.SessionManager
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.ActivityAdminBinding
import ph.edu.bpc.sis.ui.AdminAnnouncementsFragment
import ph.edu.bpc.sis.ui.AdminAuthorizedIdsFragment
import ph.edu.bpc.sis.ui.AdminAuthorizedTeacherCodesFragment
import ph.edu.bpc.sis.ui.AdminGradingTallyFragment
import ph.edu.bpc.sis.ui.AdminScheduleFragment
import ph.edu.bpc.sis.ui.AdminSettingsFragment
import ph.edu.bpc.sis.ui.AdminStudentsFragment
import ph.edu.bpc.sis.ui.AdminSubjectsFragment
import ph.edu.bpc.sis.ui.AdminTeacherAssignmentsFragment

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_sign_out) {
                StudentRepository.setActiveStudent(null)
                SessionManager(this).close()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            } else {
                false
            }
        }

        val adapter = AdminPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Students"
                1 -> "Teacher Assignments"
                2 -> "Student IDs"
                3 -> "Teacher Codes"
                4 -> "Grading Tally"
                5 -> "Schedule"
                6 -> "Subjects"
                7 -> "Announcements"
                8 -> "Settings"
                else -> ""
            }
        }.attach()
    }

    class AdminPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 9

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AdminStudentsFragment()
                1 -> AdminTeacherAssignmentsFragment()
                2 -> AdminAuthorizedIdsFragment()
                3 -> AdminAuthorizedTeacherCodesFragment()
                4 -> AdminGradingTallyFragment()
                5 -> AdminScheduleFragment()
                6 -> AdminSubjectsFragment()
                7 -> AdminAnnouncementsFragment()
                8 -> AdminSettingsFragment()
                else -> AdminStudentsFragment()
            }
        }
    }
}
