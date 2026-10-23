package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.adapter.AnnouncementAdapter
import ph.edu.bpc.sis.adapter.NextClassAdapter
import ph.edu.bpc.sis.data.Day
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.FragmentDashboardBinding
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val student = StudentRepository.currentStudent()
        val term = StudentRepository.currentTerm()
        val enrolled = StudentRepository.enrolledSubjects()

        binding.tvGreeting.text = getString(greetingRes())
        binding.tvStudentName.text = student.shortName
        binding.tvTerm.text = term.label

        binding.idCard.tvIdNumber.text = student.studentNo
        binding.idCard.tvIdCourse.text = getString(
            R.string.id_card_meta,
            student.courseCode,
            student.yearLabel,
            student.section
        )
        binding.idCard.tvIdCampus.text = student.campus
        binding.idCard.tvIrregularBadge.visibility = if (student.isIrregularStudent) View.VISIBLE else View.GONE

        val totalEnrolledUnits = enrolled.sumOf { it.units }
        val earnedUnits = StudentRepository.getEarnedUnits(student.studentNo)
        val remainingUnits = StudentRepository.getRemainingUnits(student.studentNo)

        binding.statUnits.tvStatValue.text = totalEnrolledUnits.toString()
        binding.statUnits.tvStatLabel.text = "Enrolled Units"

        binding.statSubjects.tvStatValue.text = earnedUnits.toString()
        binding.statSubjects.tvStatLabel.text = "Units Earned"

        binding.statAverage.tvStatValue.text = remainingUnits.toString()
        binding.statAverage.tvStatLabel.text = "Remaining Units"

        val today = todayAsDay()
        val todaysClasses = StudentRepository.schedule().filter { it.day == today }
        binding.rvNextClasses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNextClasses.adapter = NextClassAdapter(todaysClasses)
        binding.rvNextClasses.visibility = if (todaysClasses.isEmpty()) View.GONE else View.VISIBLE
        binding.tvNoClasses.visibility = if (todaysClasses.isEmpty()) View.VISIBLE else View.GONE

        val missedList = StudentRepository.missedActivities(student.studentNo)
        if (missedList.isNotEmpty()) {
            val missedAdapter = MissedActivitiesAdapter(
                onClick = {},
                onLongClick = {}
            )
            binding.rvMissedActivities.layoutManager = LinearLayoutManager(requireContext())
            binding.rvMissedActivities.adapter = missedAdapter
            missedAdapter.submitList(missedList)
            binding.tvMissedTitle.visibility = View.VISIBLE
            binding.rvMissedActivities.visibility = View.VISIBLE
        } else {
            binding.tvMissedTitle.visibility = View.GONE
            binding.rvMissedActivities.visibility = View.GONE
        }

        binding.rvAnnouncements.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnnouncements.adapter = AnnouncementAdapter(StudentRepository.announcements())
    }

    private fun greetingRes(): Int =
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> R.string.greeting_morning
            in 12..17 -> R.string.greeting_afternoon
            else -> R.string.greeting_evening
        }

    private fun todayAsDay(): Day? = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> Day.MON
        Calendar.TUESDAY -> Day.TUE
        Calendar.WEDNESDAY -> Day.WED
        Calendar.THURSDAY -> Day.THU
        Calendar.FRIDAY -> Day.FRI
        Calendar.SATURDAY -> Day.SAT
        else -> null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
