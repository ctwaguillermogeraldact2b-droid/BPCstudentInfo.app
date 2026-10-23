package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.adapter.ScheduleAdapter
import ph.edu.bpc.sis.data.ClassSession
import ph.edu.bpc.sis.data.Day
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.FragmentScheduleBinding
import java.util.Calendar

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val adapter = ScheduleAdapter()
    private val sessions: List<ClassSession> by lazy { StudentRepository.schedule() }
    private var selected: Day = Day.MON

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvScheduleSub.text = StudentRepository.currentTerm().label
        binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedule.adapter = adapter

        selected = todayAsDay() ?: Day.MON
        buildDayTabs()
        render()
    }

    private fun buildDayTabs() {
        binding.dayTabs.removeAllViews()
        Day.entries.forEach { day ->
            val tab = TextView(requireContext()).apply {
                text = day.short
                gravity = Gravity.CENTER
                textSize = 13f
                setBackgroundResource(R.drawable.bg_day_tab)
                setTextColor(ContextCompat.getColorStateList(context, R.color.day_tab_text))
                setPadding(0, 22, 0, 22)
                isSelected = day == selected
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    .apply { marginEnd = 8 }
                setOnClickListener {
                    selected = day
                    binding.dayTabs.children().forEach { it.isSelected = false }
                    isSelected = true
                    render()
                }
            }
            binding.dayTabs.addView(tab)
        }
    }

    private fun render() {
        val forDay = sessions
            .filter { it.day == selected }
            .sortedBy { it.start }

        adapter.submit(forDay)
        binding.tvDayTotal.text = getString(R.string.day_load, selected.full, forDay.size)
        binding.rvSchedule.visibility = if (forDay.isEmpty()) View.GONE else View.VISIBLE
        binding.tvScheduleEmpty.visibility = if (forDay.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun LinearLayout.children(): List<View> = (0 until childCount).map { getChildAt(it) }

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
