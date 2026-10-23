package ph.edu.bpc.sis

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ph.edu.bpc.sis.data.SessionManager
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.ActivityMainBinding
import ph.edu.bpc.sis.ui.DashboardFragment
import ph.edu.bpc.sis.ui.EnrollmentFragment
import ph.edu.bpc.sis.ui.GradesFragment
import ph.edu.bpc.sis.ui.ProfileFragment
import ph.edu.bpc.sis.ui.ScheduleFragment

/**
 * Holds the five tabs. Fragments are created once and shown/hidden so scroll
 * position and half-filled forms survive tab switches.
 */
class MainActivity : AppCompatActivity(), ProfileFragment.SignOutListener {

    private lateinit var binding: ActivityMainBinding

    private val tabs = linkedMapOf<Int, Pair<String, () -> Fragment>>(
        R.id.nav_home to ("home" to { DashboardFragment() }),
        R.id.nav_grades to ("grades" to { GradesFragment() }),
        R.id.nav_schedule to ("schedule" to { ScheduleFragment() }),
        R.id.nav_enroll to ("enroll" to { EnrollmentFragment() }),
        R.id.nav_profile to ("profile" to { ProfileFragment() })
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener { item ->
            show(item.itemId)
            true
        }

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_home
        }
    }

    private fun show(itemId: Int) {
        val (tag, factory) = tabs[itemId] ?: return
        val tx = supportFragmentManager.beginTransaction()

        tabs.values.forEach { (otherTag, _) ->
            supportFragmentManager.findFragmentByTag(otherTag)?.let { tx.hide(it) }
        }

        val existing = supportFragmentManager.findFragmentByTag(tag)
        if (existing == null) {
            tx.add(R.id.fragmentHost, factory(), tag)
        } else {
            tx.show(existing)
        }
        tx.commit()
    }

    override fun onSignOut() {
        StudentRepository.setActiveStudent(null)
        SessionManager(this).close()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
